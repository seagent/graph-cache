package query;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * This is a worker class that operates SPARQL query execution and retrieves
 * results to the user.
 * 
 * @author etmen
 * 
 */
public class ResultRetriever {

	/**
	 * SPARQL endpoint which the query executed on.
	 */
	private String endpoint;

	/**
	 * Jena Model which the query executed on.
	 */
	private Model model;

	/**
	 * Query which will be executed.
	 */
	private Query query;

	private Dataset dataset;

	public ResultRetriever(Model model, Query query) {
		super();
		this.model = model;
		this.query = query;
	}

	public ResultRetriever(Dataset dataset, Query query) {
		super();
		this.dataset = dataset;
		this.query = query;
	}

	public ResultRetriever(Dataset dataset, String queryText) {
		this(dataset, QueryFactory.create(queryText));
	}

	public ResultRetriever(Model model, String queryText) {
		this(model, QueryFactory.create(queryText));
	}

	public ResultRetriever(String endpoint, Query query) {
		super();
		this.endpoint = endpoint;
		this.query = query;
	}

	public ResultRetriever(String endpoint, String queryText) {
		this(endpoint, QueryFactory.create(queryText));
	}

	public String getEndpoint() {
		return endpoint;
	}

	public Model getModel() {
		return model;
	}

	public Query getQuery() {
		return query;
	}

	/**
	 * This method executed given {@link #query} on given {@link #endpoint} or
	 * given {@link #model} and returns the results of {@link #query}.
	 * 
	 * @return {@link QuerySolution} list if the query is select type,
	 *         {@link Statement} list if the query is construct or describe
	 *         type, and {@link Boolean} is the query is ASK type.
	 * @throws UndefinedQueryTypeException
	 */
	public Object retrieve() throws UndefinedQueryTypeException {
		// create query execution and prepare the query for the execution.
		QueryExecution queryExecution = getQueryExecution();

		// switch query type and execute and return results according to the
		// query type.
		switch (query.getQueryType()) {
		case Query.QueryTypeSelect: {
			return queryExecution.execSelect();
		}
		case Query.QueryTypeConstruct: {
			return queryExecution.execConstruct();
		}
		case Query.QueryTypeDescribe: {
			return queryExecution.execDescribe();
		}
		case Query.QueryTypeAsk: {
			return queryExecution.execAsk();
		}
		default:
			throw new UndefinedQueryTypeException(
					"There is no other query supported other than \"SELECT\", \"CONSTRUCT\", \"DESCRIBE\" and \"ASK\".");
		}
	}

	/**
	 * gets {@link Statement}s from given result model
	 * 
	 * @param resultModel
	 * @return {@link Statement} {@link List}
	 */
	public static List<Statement> getStatements(Model resultModel) {
		return resultModel.listStatements().toList();
	}

	/**
	 * This method takes given model and turns it into a {@link Triple}
	 * {@link List}
	 * 
	 * @param resultModel
	 *            {@link Model} instance which will be turned into triple list.
	 * @return {@link Triple} {@link List} instance contains statements in given
	 *         model.
	 */
	public static List<Triple> getTriples(Model resultModel) {
		// define triple list
		List<Triple> triples = new ArrayList<Triple>();
		// get statement iterator of the given model
		StmtIterator statementIter = resultModel.listStatements();
		// iterate on statements
		while (statementIter.hasNext()) {
			// add triple into triple list
			triples.add(((Statement) statementIter.next()).asTriple());
		}
		return triples;
	}

	/**
	 * gets {@link QuerySolution}s from given {@link ResultSet}
	 * 
	 * @param resultSet
	 * @return {@link QuerySolution} {@link List}
	 */
	public static List<QuerySolution> getQuerySolutions(ResultSet resultSet) {
		List<QuerySolution> querySolutions = new ArrayList<QuerySolution>();
		while (resultSet.hasNext()) {
			querySolutions.add((QuerySolution) resultSet.next());
		}
		return querySolutions;
	}

	private QueryExecution getQueryExecution() {
		// get query execution according to execution on an endpoint or on a
		// model.

		if (dataset != null) {
			return QueryExecutionFactory.create(query, dataset);
		} else if (model != null) {
			return QueryExecutionFactory.create(query, model);
		} else if (endpoint != null) {
			return QueryExecutionFactory.sparqlService(endpoint, query);
		} else {
			throw new QueryExecException(
					MessageFormat
							.format("There is neither endpoint nor model nor dataset to execute the query: \"{0}\"",
									query));
		}
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public static List<Node> getSubjects(Model model) {
		ResIterator subjectIter = model.listSubjects();
		List<Node> subjectNodes = new ArrayList<Node>();
		while (subjectIter.hasNext()) {
			subjectNodes.add(subjectIter.next().asNode());
		}
		return subjectNodes;
	}

	public static List<Node> getPredicates(Model model) {
		List<Statement> statements = model.listStatements().toList();
		List<Node> predicateNodes = new ArrayList<Node>();
		for (Statement statement : statements) {
			if (!predicateNodes.contains(statement.getPredicate().asNode())) {
				predicateNodes.add(statement.getPredicate().asNode());
			}
		}
		return predicateNodes;
	}

	public static List<Node> getObjects(Model model) {
		NodeIterator objectIter = model.listObjects();
		List<Node> objectNodes = new ArrayList<Node>();
		while (objectIter.hasNext()) {
			objectNodes.add(objectIter.next().asNode());
		}
		return objectNodes;
	}

}
