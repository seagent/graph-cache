package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

public class QueryBuilderTool {
	private char[] letters = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };
	/**
	 * It includes triple List of given Query.
	 */
	private Query query;

	public QueryBuilderTool(Query query) {
		super();
		this.query = query;
	}

	public QueryBuilderTool(String queryText) {
		this(QueryFactory.create(queryText));
	}

	/**
	 * It takes the query parameter and analysis union blocks if it has any.
	 * Then gets triples in the union block and compares them to find variable
	 * match such as { ?subject <https//:uri1> ?object. <--> ?subject
	 * <https//:uri2> ?object. }. Because of all query results are stored as
	 * triples and select queries turned into construct queries such situation
	 * can cause multiplying select results. So after sending select query into
	 * the result construct model it will be important to catch such situation
	 * and set select query as distinct.
	 * 
	 * @param query
	 * @return
	 */
	public static boolean checkForMultipleTripleRisk(Query query) {
		// define risky situation
		boolean isRisky = false;
		// get triples in the union block if it has any
		List<List<TriplePath>> triplesInUnionBlocks = getTriplesOfUnionBlocks(query);
		// compare each triple block in the union block with other blocks
		for (int i = 0; i < triplesInUnionBlocks.size(); i++) {
			// get first block
			List<TriplePath> triplesFirst = triplesInUnionBlocks.get(i);
			for (int j = i + 1; j < triplesInUnionBlocks.size(); j++) {
				// get second block
				List<TriplePath> triplesSecond = triplesInUnionBlocks.get(j);
				// compare triples in the block for a similarity of triples
				if (compareTriplesForSimilarity(triplesFirst, triplesSecond)) {
					// if there is found any mapping and similarity of variables
					// then it is risky.
					isRisky = true;
				}
			}

		}
		return isRisky;
	}

	/**
	 * Convert the given triple to the SPARQL statement form.
	 * 
	 * @param triple
	 */
	public static String convertTripleToString(Triple triple) {
		String s;
		String p;
		String o;
		if (triple.getSubject().isVariable())
			s = triple.getSubject().toString();
		else
			s = "<" + triple.getSubject().getURI() + ">";
		// get predicate...
		if (triple.getPredicate().isVariable())
			p = triple.getPredicate().toString();
		else
			p = "<" + triple.getPredicate().getURI() + ">";
		// get object...
		if (triple.getObject().isVariable())
			o = triple.getObject().toString();
		else if (triple.getObject().isURI())
			o = "<" + triple.getObject().getURI() + ">";
		else
			o = triple.getObject().toString();
		return s + " " + p + " " + o + ". ";
	}

	/**
	 * This method gets given element and get the {@link TriplePath}s contained
	 * in it.
	 * 
	 * @param element
	 * @return
	 */
	protected static List<TriplePath> getTriplesOfElement(Element element) {
		// define triple path lists to get the needed element in it because of
		// final modifier
		final List<List<TriplePath>> triplePathLists = new ArrayList<List<TriplePath>>();
		// define element visitor
		ElementVisitorBase visitor = new ElementVisitorBase() {
			// override triple block visiting
			@Override
			public void visit(ElementPathBlock elementPathBlock) {
				// get triple list and add it to the triple path lists
				triplePathLists.add(elementPathBlock.getPattern().getList());
			}

		};
		// walk on element with the visitor
		ElementWalker.walk(element, visitor);
		// check if there is found a triple list return it otherwise return null
		if (!triplePathLists.isEmpty()) {
			return triplePathLists.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Check given node is {@link Var} and add it to the list if it is a
	 * {@link Var} instance
	 * 
	 * @param variables
	 * @param node
	 */
	private static void checkNodeToAddForVariables(List<Var> variables,
			Node node) {
		if (node.isVariable()) {
			variables.add((Var) node);
		}
	}

	/**
	 * This method looks for variable similarity between two {@link TriplePath}
	 * instance
	 * 
	 * @param triplePathFirst
	 *            first {@link TriplePath} instance
	 * @param triplePathSecond
	 *            second {@link TriplePath} instance
	 * @return similarity situation
	 */
	private static boolean compareTripleForSimilarity(
			TriplePath triplePathFirst, TriplePath triplePathSecond) {
		// get variables of first triple path
		List<Var> firstVariables = getVariables(triplePathFirst);
		// get variables of second triple path
		List<Var> secondVariables = getVariables(triplePathSecond);
		// check equality of variable size
		if (firstVariables.size() != secondVariables.size()) {
			return false;
		} else {
			// check all varialbes are equal
			for (Var secondVar : secondVariables) {
				if (!firstVariables.contains(secondVar)) {
					// if there is found any incompatibility
					return false;
				}
			}
		}
		// otherwise
		return true;
	}

	/**
	 * This method compares two {@link TriplePath} block for similarity of
	 * variables
	 * 
	 * @param triplesFirst
	 *            first triple block {@link List}
	 * @param triplesSecond
	 *            second triple block {@link List}
	 * @return similarity situation
	 */
	private static boolean compareTriplesForSimilarity(
			List<TriplePath> triplesFirst, List<TriplePath> triplesSecond) {
		// get first triple path
		for (TriplePath triplePathFirst : triplesFirst) {
			// get second triple path
			for (TriplePath triplePathSecond : triplesSecond) {
				// compare two triple path to check if they are similar
				if (compareTripleForSimilarity(triplePathFirst,
						triplePathSecond)) {
					// if there is found any similarity return true
					return true;
				}
			}
		}
		// otherwise return false
		return false;
	}

	/**
	 * This method gets triple blocks of union blocks of given query if the
	 * query has any union block
	 * 
	 * @param query
	 * @return list of triple list contained in the union blocks
	 */
	private static List<List<TriplePath>> getTriplesOfUnionBlocks(Query query) {
		// define list
		final List<List<TriplePath>> triplePathLists = new ArrayList<List<TriplePath>>();
		// create a union visitor
		ElementVisitorBase unionVisitor = new ElementVisitorBase() {
			// override union visit
			@Override
			public void visit(ElementUnion elementUnion) {
				// iterate on union elements
				for (Element unionElement : elementUnion.getElements()) {
					// get triple block in the given union element
					List<TriplePath> triplesOfElement = getTriplesOfElement(unionElement);
					// check whether block contains triples
					if (triplesOfElement != null) {
						// add triples to the triples list
						triplePathLists.add(triplesOfElement);
					}
				}
				super.visit(elementUnion);
			}
		};
		// walk on query patern with union visitor
		ElementWalker.walk(query.getQueryPattern(), unionVisitor);
		// return the list of triple paths
		return triplePathLists;
	}

	/**
	 * It retrieves variables stored in the given {@link TriplePath}
	 * 
	 * @param triplePath
	 * @return {@link List} of {@link Var} s which can be subject predicate and
	 *         object of given {@link TriplePath} instance.
	 */
	private static List<Var> getVariables(TriplePath triplePath) {
		// define variable list
		List<Var> variables = new ArrayList<Var>();
		// check subject, predicate and object and add if they are variable
		checkNodeToAddForVariables(variables, triplePath.getSubject());
		checkNodeToAddForVariables(variables, triplePath.getPredicate());
		checkNodeToAddForVariables(variables, triplePath.getObject());
		return variables;
	}

	public Query getQuery() {
		return query;
	}

	/**
	 * This method rebuilds SELECT query as an equal CONSTRUCT one.
	 * 
	 * @return
	 */
	public Query rebuildSelectToConstruct() {
		// get query text
		String newQueryText = query.toString();
		// split query text according to "SELECT" pattern
		String[] splittedSelectQuery = newQueryText.split("SELECT");
		// get prefix part of query
		String prefixes = splittedSelectQuery[0];
		// get remaining part
		String remainingPart = splittedSelectQuery[1];
		// split remaining part according to "WHERE" pattern
		String[] splittedWhereClause = remainingPart.split("WHERE");
		// get where clause
		String whereClause = splittedWhereClause[1];
		// define construct query
		String constructQueryText = "";
		// add prefixes and "CONSTRUCT" keyword first
		constructQueryText += prefixes + " CONSTRUCT {";
		// add each triple pattern
		List<Triple> triples = retrieveTriples(query.getQueryPattern());
		for (Triple triple : triples) {
			constructQueryText += convertTripleToString(triple);
		}
		// add "WHERE" clause
		constructQueryText += "} WHERE" + whereClause;

		// return new construct query
		return QueryFactory.create(constructQueryText);
	}

	public List<Triple> retrieveTriples() {
		return retrieveTriples(query.getQueryPattern());
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * It standardizes query as similar query those differ only on variable
	 * names, into same generic one.
	 * 
	 * @return standardized {@link Query} object.
	 */
	public Query standardize() {
		// list variables in the query
		List<Var> vars = getVariables(query.getQueryPattern());
		// get query text
		String queryText = query.toString();
		// iterate on each variable
		for (int i = 0; i < vars.size(); i++) {
			// get variable
			Var var = vars.get(i);
			// retrieve appropriate variable for it.
			String letter = retrieveVariable(i);
			// replace old variable with new one.
			queryText = queryText.replace(var.toString() + " ",
					("?" + letter + " "));
			queryText = queryText.replace(var.toString() + ".",
					("?" + letter + "."));
			queryText = queryText.replace(var.toString() + "\n",
					("?" + letter + "\n"));
			queryText = queryText.replace(var.toString() + ")",
					("?" + letter + ")"));
		}
		// return new standardized query
		return QueryFactory.create(queryText);
	}

	private void checkToAddVariableList(List<Var> variables, Node node) {
		if (node.isVariable() && !variables.contains(node)) {
			variables.add((Var) node);
		}
	}

	/**
	 * Create a triple visitor for optional graph.
	 * 
	 * @param triples
	 * @return
	 */
	private ElementVisitor createTripleVisitor(final List<Triple> triples) {
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementPathBlock el) {
				Iterator<TriplePath> iterator = el.getPattern().iterator();
				while (iterator.hasNext()) {
					TriplePath triplePath = iterator.next();
					triples.add(triplePath.asTriple());
				}
				super.visit(el);
			}
		};
		return tripleVisitor;
	}

	private List<Var> getVariables(Element queryPattern) {
		List<Triple> triples = retrieveTriples();
		List<Var> variables = retrieveServiceVariables(queryPattern);
		for (Triple triple : triples) {
			checkToAddVariableList(variables, triple.getSubject());
			checkToAddVariableList(variables, triple.getPredicate());
			checkToAddVariableList(variables, triple.getObject());
		}
		return variables;
	}

	private List<Var> retrieveServiceVariables(Element queryPart) {
		final List<Var> serviceVariables = new ArrayList<Var>();
		ElementVisitorBase serviceVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementService el) {
				Node serviceNode = el.getServiceNode();
				if (serviceNode.isVariable()
						&& !serviceVariables.contains(serviceNode)) {
					serviceVariables.add((Var) serviceNode);
				}
				super.visit(el);
			}
		};
		ElementWalker.walk(queryPart, serviceVisitor);
		return serviceVariables;
	}

	/**
	 * This method retrieves all triples in a given query {@link Element}
	 * 
	 * @param queryPart
	 *            a part of query
	 * @return {@link List} of contained {@link Triple}s in a {@link Query}
	 */
	private List<Triple> retrieveTriples(Element queryPart) {
		final List<Element> elementsList = new Vector<Element>();
		final List<Triple> tripleList = new ArrayList<Triple>();
		elementsList.clear();
		// create a visitor for visit blocks...
		ElementVisitorBase elementVisitor = new ElementVisitorBase() {

			// get triples in optional block
			@Override
			public void visit(ElementOptional el) {
				elementsList.add(el.getOptionalElement());
				super.visit(el);
			}

			// get other triples
			@Override
			public void visit(ElementPathBlock el) {
				Iterator<TriplePath> iterator = el.getPattern().iterator();
				while (iterator.hasNext()) {
					TriplePath triplePath = iterator.next();
					tripleList.add(triplePath.asTriple());
				}
				super.visit(el);
			}

		};
		ElementWalker.walk(queryPart, elementVisitor);
		List<Triple> triplesInOptional = new Vector<Triple>();
		for (Element element : elementsList) {
			ElementWalker.walk(element, createTripleVisitor(triplesInOptional));
		}
		return tripleList;
	}

	/**
	 * This method returns a standard variable according to the sequence of old
	 * variable.
	 * 
	 * @param index
	 *            {@link Integer} sequence of old variable among other variables
	 *            in the query.
	 * @return {@link String} typed standardized new variable name
	 */
	private String retrieveVariable(int index) {
		// get absolute index not to exceed English alphabet length
		int absoluteIndex = index % (letters.length - 1);
		// how many time index exceeds alphabet
		int order = index / (letters.length - 1);
		// returns new text consist of letter and order
		return "" + letters[absoluteIndex] + order;
	}

	/**
	 * This method, removes service descriptions from a federated query and
	 * turns it into raw query.
	 * 
	 * @return raw {@link Query}
	 */
	public Query makeRawQuery() {
		// define service node list to get the service descriptions in the query
		final List<Node> serviceNodes = new ArrayList<Node>();
		getServiceDescriptions(serviceNodes);

		// turn federated query into string
		String queryText = query.toString();
		// remove service descriptions from query
		queryText = removeServiceDescriptions(serviceNodes, queryText);
		return QueryFactory.create(queryText);
	}

	/**
	 * It removes service descriptions
	 * 
	 * @param serviceNodes
	 *            {@link List} of service {@link Node}s those will be removed
	 *            from the query.
	 * @param queryText
	 *            query text, which the service descriptions removed from
	 * @return raw {@link Query}
	 */
	private String removeServiceDescriptions(final List<Node> serviceNodes,
			String queryText) {
		// iterate on each service node
		for (Node serviceNode : serviceNodes) {
			if (serviceNode.isURI()) {
				// if service node is uri remove SERVICE definition directly and
				// easily.
				queryText = queryText.replace(
						"SERVICE <" + serviceNode.getURI() + ">", "");
			} else if (serviceNode.isVariable()) {
				// if service node is variable firstly remove service
				// description with variable
				queryText = queryText.replace("SERVICE " + serviceNode, "");
				// remove bind union parts from query text
				queryText = removeBindUnionParts(queryText, serviceNode);
			}
		}
		return queryText;
	}

	/**
	 * This method removes given bind union node from given query text.
	 * 
	 * @param queryText
	 *            query text, which the service description removed from
	 * @param serviceNode
	 *            {@link Node} instance which will be removed from given query
	 *            text
	 * @return modified query text
	 */
	private String removeBindUnionParts(String queryText, Node serviceNode) {
		// get first index of binding variable
		int firstIndexOfNode = queryText.indexOf(serviceNode.toString());
		// crop first part according to variable
		String firstPart = queryText.substring(0, firstIndexOfNode);
		// get left curly braces index
		int endIndexOfFirstPart = firstPart.lastIndexOf('{');
		// get final first part
		firstPart = firstPart.substring(0, endIndexOfFirstPart - 1);

		// then get the last index of binding variable
		int lastIndexOfNode = queryText.lastIndexOf(serviceNode.toString());
		// crop right part according to variable
		String secondPart = queryText.substring(lastIndexOfNode,
				queryText.length());
		// get right curly braces index
		int beginIndexOfSecondPart = secondPart.indexOf('}');
		// get final second part
		secondPart = secondPart.substring(beginIndexOfSecondPart + 1,
				secondPart.length());
		// after clearing bind union parts, merge first and second part
		queryText = firstPart + secondPart;
		return queryText;
	}

	/**
	 * This method gets the service node elements in the federated
	 * {@link #query}
	 * 
	 * @param serviceNodes
	 *            service {@link Node} {@link List} which will be filled.
	 */
	private void getServiceDescriptions(final List<Node> serviceNodes) {
		// define element visitor
		ElementVisitorBase visitor = new ElementVisitorBase() {
			// override ElementService visiting
			@Override
			public void visit(ElementService elementService) {
				// add service node
				serviceNodes.add(elementService.getServiceNode());
			}
		};
		// walk on element with the visitor
		ElementWalker.walk(query.getQueryPattern(), visitor);
	}
}
