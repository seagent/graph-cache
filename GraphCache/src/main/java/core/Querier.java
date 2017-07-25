package core;

import java.text.MessageFormat;
import java.util.List;

import org.apache.jena.riot.lang.BlankNodeAllocatorTraditional;
import org.apache.xerces.util.URI;

import query.ResultRetriever;
import query.UndefinedQueryTypeException;
import query.UnsupportedNodeTypeException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.pfunction.library.blankNode;

public class Querier {

	protected MemoryGraph memoryGraph;

	public Querier() {
		memoryGraph = new MemoryGraph();
	}

	/**
	 * This method gets the query and turns it into a literal using its hash
	 * code.
	 * 
	 * @param query
	 *            {@link Query} instance which will be turned into literal.
	 * @return {@link Literal} instance of query
	 */
	public static Literal getQueryPatternHashCodeLiteral(Query query) {
		return ResourceFactory.createTypedLiteral(
				String.valueOf(query.getQueryPattern().toString().hashCode()),
				XSDDatatype.XSDint);
	}

	/**
	 * This method executes given ask query on the given endpoint and stores the
	 * result if this {@link DirectedCacheQuery} (query-endpoint pair) is not
	 * contained in the cache, otherwise returns the query result
	 * 
	 * @param query
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return boolean result of ask query
	 * @throws UndefinedQueryTypeException
	 */
	public Boolean ask(Query query, String endpoint)
			throws UndefinedQueryTypeException {
		// firstly standardize query in order to avoid duplicate query entries
		// in the cache
		query = standardizeQuery(query);
		// define containment and initialize as false
		Boolean contained = false;
		// try to get results from the cache
		DirectedCacheQuery directedCacheQuery = new DirectedCacheQuery(query,
				endpoint);
		List<CachedTriple> triplesOfQuery = memoryGraph
				.getTriplesOfQuery(directedCacheQuery);
		// check whether the results are contained in the cache
		if (triplesOfQuery == null) {
			// if cache does not contain results retrieve results from related
			// endpoint
			ResultRetriever resultRetriever = new ResultRetriever(endpoint,
					query);
			contained = (Boolean) resultRetriever.retrieve();
			// create triple for containment situation
			Triple triple = createContainmentTriple(directedCacheQuery,
					contained);
			// store results
			memoryGraph.store(directedCacheQuery, triple);
		} else {
			// because of the ask result contains only one triple we get the
			// first.
			Triple triple = triplesOfQuery.get(0).getTriple();
			// assign true if the predicate is contains otherwise it will be
			// false
			if (((Boolean) triple.getObject().getLiteral().getValue())) {
				contained = true;
			}
		}
		return contained;
	}

	/**
	 * This method executes given ask query on the given endpoint and stores the
	 * result if this {@link DirectedCacheQuery} (query-endpoint pair) is not
	 * contained in the cache, otherwise returns the query result
	 * 
	 * @param query
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return boolean result of ask query
	 * @throws UndefinedQueryTypeException
	 */
	public Boolean ask(Query query, Dataset dataset)
			throws UndefinedQueryTypeException {
		// firstly standardize query in order to avoid duplicate query entries
		// in the cache
		query = standardizeQuery(query);
		// define containment and initialize as false
		Boolean contained = false;
		// try to get results from the cache
		FederatedCacheQuery federatedCacheQuery = new FederatedCacheQuery(query);
		List<CachedTriple> triplesOfQuery = memoryGraph
				.getTriplesOfQuery(federatedCacheQuery);
		// check whether the results are contained in the cache
		if (triplesOfQuery == null) {
			// if cache does not contain results retrieve results from related
			// endpoint
			ResultRetriever resultRetriever = new ResultRetriever(dataset,
					query);
			contained = (Boolean) resultRetriever.retrieve();
			// create triple for containment situation
			Triple triple = createContainmentTriple(federatedCacheQuery,
					contained);
			// store results
			memoryGraph.store(federatedCacheQuery, triple);
		} else {
			// because of the ask result contains only one triple we get the
			// first.
			Triple triple = triplesOfQuery.get(0).getTriple();
			// assign true if the predicate is contains otherwise it will be
			// false
			if (triple.getPredicate().getURI()
					.equals(MemoryGraphConstants.HAS_RESULT)) {
				contained = true;
			}
		}
		return contained;
	}

	/**
	 * This method executes given ask query on the given endpoint and stores the
	 * result if this {@link DirectedCacheQuery} (query-endpoint pair) is not
	 * contained in the cache, otherwise returns the query result
	 * 
	 * @param query
	 *            text of query which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return boolean result of ask query
	 * @throws UndefinedQueryTypeException
	 */
	public Boolean ask(String query, Dataset dataset)
			throws UndefinedQueryTypeException {
		return ask(QueryFactory.create(query), dataset);
	}

	/**
	 * This method executes given ask query on the given endpoint and stores the
	 * result if this {@link DirectedCacheQuery} (query-endpoint pair) is not
	 * contained in the cache, otherwise returns the query result
	 * 
	 * @param query
	 *            text of query which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return boolean result of ask query
	 * @throws UndefinedQueryTypeException
	 */
	public Boolean ask(String query, String endpoint)
			throws UndefinedQueryTypeException {
		return ask(QueryFactory.create(query), endpoint);
	}

	/**
	 * This method checks given query contained by the {@link #memoryGraph}
	 * 
	 * @param query
	 * @param endpoint
	 * @return
	 */
	public boolean checkContained(Query query, String endpoint) {
		// firstly standardize the query
		query = standardizeQuery(query);
		// get result of directed query
		List<CachedTriple> triplesOfQuery = memoryGraph
				.getTriplesOfQuery(new DirectedCacheQuery(query, endpoint));
		// return false if the result is null otherwise true
		return (triplesOfQuery == null) ? false : true;
	}

	/**
	 * This method checks given query contained by the {@link #memoryGraph}
	 * 
	 * @param queryCachable
	 * @return
	 */
	public boolean checkContained(QueryCachable queryCachable) {
		// get result of directed query
		List<CachedTriple> triplesOfQuery = memoryGraph
				.getTriplesOfQuery(queryCachable);
		// return false if the result is null otherwise true
		return (triplesOfQuery == null) ? false : true;
	}

	/**
	 * It clears the memory graph
	 */
	public void clearMemoryGraph() {
		memoryGraph.clearCache();
	}

	/**
	 * This method executes given construct query on the given endpoint and
	 * stores the results if this {@link DirectedCacheQuery} (query-endpoint
	 * pair) is not contained in the cache, otherwise returns the query results
	 * 
	 * @param query
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given construct query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model construct(Query query, Dataset dataset)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return execModel(query, dataset);
	}

	/**
	 * This method executes given construct query on the given endpoint and
	 * stores the results if this {@link DirectedCacheQuery} (query-endpoint
	 * pair) is not contained in the cache, otherwise returns the query results
	 * 
	 * @param queryTxt
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given construct query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model construct(String queryTxt, Dataset dataset)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return execModel(QueryFactory.create(queryTxt), dataset);
	}

	/**
	 * This method executes given construct query on the given endpoint and
	 * stores the results if this {@link DirectedCacheQuery} (query-endpoint
	 * pair) is not contained in the cache, otherwise returns the query results
	 * 
	 * @param query
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given construct query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model construct(Query query, String endpoint)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return execModel(query, endpoint);
	}

	/**
	 * This method executes given construct query on the given endpoint and
	 * stores the results if this {@link DirectedCacheQuery} (query-endpoint
	 * pair) is not contained in the cache, otherwise returns the query results
	 * 
	 * @param query
	 *            text of query which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given construct query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model construct(String query, String endpoint)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return construct(QueryFactory.create(query), endpoint);
	}

	/**
	 * This method executes given describe query on the given endpoint and
	 * stores the results if this {@link DirectedCacheQuery} (query-endpoint
	 * pair) is not contained in the cache, otherwise returns the query results
	 * 
	 * @param query
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given describe query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model describe(Query query, Dataset dataset)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return execModel(query, dataset);
	}

	/**
	 * This method executes given describe query on the given endpoint and
	 * stores the results if this {@link DirectedCacheQuery} (query-endpoint
	 * pair) is not contained in the cache, otherwise returns the query results
	 * 
	 * @param queryTxt
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given describe query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model describe(String queryTxt, Dataset dataset)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return execModel(QueryFactory.create(queryTxt), dataset);
	}

	/**
	 * This method executes given describe query on the given endpoint and
	 * stores the results if this {@link DirectedCacheQuery} (query-endpoint
	 * pair) is not contained in the cache, otherwise returns the query results
	 * 
	 * @param query
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given describe query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model describe(Query query, String endpoint)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return execModel(query, endpoint);
	}

	/**
	 * This method executes given describe query on the given endpoint and
	 * stores the results if this {@link DirectedCacheQuery} (query-endpoint
	 * pair) is not contained in the cache, otherwise returns the query results
	 * 
	 * @param query
	 *            text of query which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given describe query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model describe(String query, String endpoint)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return describe(QueryFactory.create(query), endpoint);
	}

	/**
	 * This method executes both given construct and describe query on the given
	 * endpoint and stores the results if this {@link DirectedCacheQuery}
	 * (query-endpoint pair) is not contained in the cache, otherwise returns
	 * the query results
	 * 
	 * @param query
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given construct-describe query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model execModel(Query query, String endpoint)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		// firstly standardize query in order to avoid duplicate query entries
		// in the cache
		query = standardizeQuery(query);
		// define result model
		Model model = null;
		// try to get results from the cache
		List<CachedTriple> triplesOfQuery = memoryGraph
				.getTriplesOfQuery(new DirectedCacheQuery(query, endpoint));
		// check whether the results are contained in the cache
		if (triplesOfQuery == null) {
			// if cache does not contain results retrieve results from related
			// endpoint
			ResultRetriever resultRetriever = new ResultRetriever(endpoint,
					query);
			model = (Model) resultRetriever.retrieve();
			// store results
			memoryGraph.store(new DirectedCacheQuery(query, endpoint), model);
		} else {
			// make a model of cached triples
			model = createModel(triplesOfQuery);
		}
		return model;
	}

	/**
	 * This method executes both given construct and describe query on the given
	 * endpoint and stores the results if this {@link DirectedCacheQuery}
	 * (query-endpoint pair) is not contained in the cache, otherwise returns
	 * the query results
	 * 
	 * @param query
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given construct-describe query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public Model execModel(Query query, Dataset dataset)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		// firstly standardize query in order to avoid duplicate query entries
		// in the cache
		query = standardizeQuery(query);
		// define result model
		Model model = null;
		// try to get results from the cache
		FederatedCacheQuery federatedCacheQuery = new FederatedCacheQuery(query);
		List<CachedTriple> triplesOfQuery = memoryGraph
				.getTriplesOfQuery(federatedCacheQuery);
		// check whether the results are contained in the cache
		if (triplesOfQuery == null) {
			// if cache does not contain results retrieve results from related
			// endpoint
			ResultRetriever resultRetriever = new ResultRetriever(dataset,
					query);
			model = (Model) resultRetriever.retrieve();
			// store results
			memoryGraph.store(federatedCacheQuery, model);
		} else {
			// make a model of cached triples
			model = createModel(triplesOfQuery);
		}
		return model;
	}

	/**
	 * This method executes given select query on the given endpoint and stores
	 * the results if this {@link DirectedCacheQuery} (query-endpoint pair) is
	 * not contained in the cache, otherwise returns the query results
	 * 
	 * @param standartQuery
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given select query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public ResultSet select(Query query, String endpoint)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		// firstly standardize query in order to avoid duplicate query entries
		// in the cache
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(query);
		Query standartQuery = queryBuilderTool.standardize();
		// define the result model
		Model model = null;
		// try to get results from the cache
		QueryCachable directedCachableQuery = new DirectedCacheQuery(
				standartQuery, endpoint);
		List<CachedTriple> triplesOfQuery = memoryGraph
				.getTriplesOfQuery(directedCachableQuery);
		// check whether the results are contained in the cache
		if (triplesOfQuery == null) {
			/**
			 * if cache does not contain results retrieve results from related
			 * endpoint
			 **/
			// firstly rebuild select query into construct query in
			// order to store result as triples
			Query constructQuery = queryBuilderTool.rebuildSelectToConstruct();
			// retrieve results
			ResultRetriever resultRetriever = new ResultRetriever(endpoint,
					constructQuery);
			model = (Model) resultRetriever.retrieve();
			// store results
			memoryGraph.store(directedCachableQuery, model);
		} else {
			// make a model of cached triples
			model = createModel(triplesOfQuery);
		}
		// retrive result set from the constructed result model
		Query plainQuery = QueryFactory.create(query);
		plainQuery.setLimit(Query.NOLIMIT);
		plainQuery.setOffset(Query.NOLIMIT);
		if (QueryBuilderTool.checkForMultipleTripleRisk(plainQuery)) {
			plainQuery.setDistinct(true);
		}
		ResultRetriever resultRetriever = new ResultRetriever(model, plainQuery);
		return (ResultSet) resultRetriever.retrieve();
	}

	/**
	 * This method executes given select query on the given endpoint and stores
	 * the results if this {@link DirectedCacheQuery} (query-endpoint pair) is
	 * not contained in the cache, otherwise returns the query results
	 * 
	 * @param standartQuery
	 *            {@link Query} instance which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given select query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public ResultSet select(Query query, Dataset dataset)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		// firstly standardize query in order to avoid duplicate query entries
		// in the cache
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(query);
		Query standartQuery = queryBuilderTool.standardize();
		// define the result model
		Model model = null;
		// try to get results from the cache

		QueryCachable federatedCachableQuery = new FederatedCacheQuery(
				standartQuery);
		List<CachedTriple> triplesOfQuery = memoryGraph
				.getTriplesOfQuery(federatedCachableQuery);
		// check whether the results are contained in the cache
		if (triplesOfQuery == null) {
			/**
			 * if cache does not contain results retrieve results from related
			 * endpoint
			 **/
			// firstly rebuild select query into construct query in
			// order to store result as triples
			Query constructQuery = queryBuilderTool.rebuildSelectToConstruct();
			// retrieve results
			ResultRetriever resultRetriever = new ResultRetriever(dataset,
					constructQuery);
			model = (Model) resultRetriever.retrieve();
			// store results
			memoryGraph.store(federatedCachableQuery, model);
		} else {
			// make a model of cached triples
			model = createModel(triplesOfQuery);
		}
		// retrive result set from the constructed result model
		Query plainQuery = queryBuilderTool.makeRawQuery();
		plainQuery.setLimit(Query.NOLIMIT);
		plainQuery.setOffset(Query.NOLIMIT);
		if (QueryBuilderTool.checkForMultipleTripleRisk(plainQuery)) {
			plainQuery.setDistinct(true);
		}
		ResultRetriever resultRetriever = new ResultRetriever(model, plainQuery);
		return (ResultSet) resultRetriever.retrieve();
	}

	/**
	 * This method executes given select query on the given endpoint and stores
	 * the results if this {@link DirectedCacheQuery} (query-endpoint pair) is
	 * not contained in the cache, otherwise returns the query results
	 * 
	 * @param queryTxt
	 *            text of query which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given select query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public ResultSet select(String queryTxt, Dataset dataset)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return select(QueryFactory.create(queryTxt), dataset);
	}

	/**
	 * This method executes given select query on the given endpoint and stores
	 * the results if this {@link DirectedCacheQuery} (query-endpoint pair) is
	 * not contained in the cache, otherwise returns the query results
	 * 
	 * @param query
	 *            text of query which will be executed.
	 * @param endpoint
	 *            endpoint SPARQL endpoint which the query executed on
	 * @return result {@link Model} of the given select query
	 * @throws UndefinedQueryTypeException
	 * @throws UnsupportedNodeTypeException
	 */
	public ResultSet select(String query, String endpoint)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		return select(QueryFactory.create(query), endpoint);
	}

	/**
	 * It takes the containment situation and create logically a triple which is
	 * "endpoint--containes/doesNotContain--query"
	 * 
	 * @param query
	 *            {@link Query} instance which will be turned into a literal for
	 *            object position
	 * @param endpoint
	 *            endpoint which will be subject
	 * @param contained
	 *            containment situation which will be predicate
	 * @return {@link Triple} instance that defines containment situation
	 */
	private Triple createContainmentTriple(QueryCachable cachableQuery,
			Boolean contained) {
		// create subject node
		Node subject = ResourceFactory.createResource(
				MemoryGraphConstants.QUERY_RSC_PREFIX
						+ cachableQuery.hashCode()).asNode();
		// define predicate node
		Node predicate = ResourceFactory.createProperty(
				MemoryGraphConstants.HAS_RESULT).asNode();
		// create object node
		Node object = null;
		// define predicate node of the triple which is containment or not
		// containment situation
		if (contained) {
			object = ResourceFactory.createTypedLiteral(String.valueOf(true),
					XSDDatatype.XSDboolean).asNode();
		} else {
			object = ResourceFactory.createTypedLiteral(String.valueOf(false),
					XSDDatatype.XSDboolean).asNode();
		}
		// create and return a new triple using the subject predicate and object
		// nodes
		return new Triple(subject, predicate, object);
	}

	/**
	 * This method turns given {@link CachedTriple} list into the {@link Model}
	 * instance
	 * 
	 * @param triplesOfQuery
	 *            {@link CachedTriple} list whose {@link Triple}s will be added
	 *            to the new constructed {@link Model} instance.
	 * @return Model instance that contains {@link Triple}s of the
	 *         {@link CachedTriple}s
	 * @throws UnsupportedNodeTypeException
	 */
	private Model createModel(List<CachedTriple> triplesOfQuery)
			throws UnsupportedNodeTypeException {
		// create new model instance
		Model model = ModelFactory.createDefaultModel();
		// iterate on each cached triple
		for (CachedTriple cachedTriple : triplesOfQuery) {
			// get triple of cached triple
			Triple triple = cachedTriple.getTriple();
			// turn subject node into the resorce
			Resource subject = (Resource) turnNodeIntoResource(triple
					.getSubject());
			// turn property node into the property
			Property predicate = (Property) turnNodeIntoProperty(triple
					.getPredicate());
			// turn object node into the RDFNode which can be literal or
			// resource
			RDFNode object = turnNodeIntoRDFNode(triple.getObject());
			// add a new statement into the model
			model.add(subject, predicate, object);
		}
		return model;
	}

	/**
	 * It standardize the given query using {@link QueryBuilderTool} instance
	 * and returns the standardized query instance
	 * 
	 * @param query
	 * @return
	 */
	protected Query standardizeQuery(Query query) {
		// create a new query builder tool
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(query);
		// standardize query
		return queryBuilderTool.standardize();
	}

	/**
	 * It takes node and turns given node into {@link Literal} instance
	 * 
	 * @param node
	 *            node which will be turned into {@link Literal}
	 * @return {@link Literal} value of the given node
	 */
	private Literal turnNodeIntoLiteral(Node node)
			throws UnsupportedNodeTypeException {
		Literal literalValue = null;
		// check whether node is Literal
		if (node.isLiteral()) {
			// return a new Literal using literal value
			LiteralLabel literal = node.getLiteral();
			if (literal.getDatatype() == null) {
				// check whether plain literal contains language
				if (literal.language() == null) {
					// create default plain literal
					literalValue = ResourceFactory.createPlainLiteral(literal
							.getValue().toString());
				} else {
					// create lang literal
					literalValue = ResourceFactory.createLangLiteral(literal
							.getValue().toString(), literal.language());
				}
			} else {
				// create typed literal
				literalValue = ResourceFactory.createTypedLiteral(
						literal.getLexicalForm(), literal.getDatatype());
			}
		} else {
			// if given node is not a literal throw unsupported node type
			// exception
			throw new UnsupportedNodeTypeException(MessageFormat.format(
					"The given node value \"{0}\" is not a literal", node));
		}
		return literalValue;
	}

	/**
	 * It takes node and turns given node into {@link Property} instance
	 * 
	 * @param node
	 *            node which will be turned into {@link Property}
	 * @return {@link Property} value of the given node
	 */
	private Property turnNodeIntoProperty(Node node)
			throws UnsupportedNodeTypeException {
		Property property = null;
		// check whether node is URI
		if (node.isURI()) {
			// return a new property using this URI
			property = ResourceFactory.createProperty(node.getURI());
		} else {
			throw new UnsupportedNodeTypeException(MessageFormat.format(
					"The given node value \"{0}\" is not a Property", node));
		}
		return property;
	}

	/**
	 * It takes node and turns given node into {@link RDFNode} instance
	 * 
	 * @param node
	 *            node which will be turned into {@link RDFNode}
	 * @return {@link RDFNode} value of the given node
	 * @throws UnsupportedNodeTypeException
	 *             if the node is a variable rather than {@link URI} or
	 *             {@link Literal}
	 */
	private RDFNode turnNodeIntoRDFNode(Node node)
			throws UnsupportedNodeTypeException {
		// check whether node is URI
		if (node.isURI()) {
			// return a new resource using this URI
			return turnNodeIntoResource(node);
		} else {
			// return a new literal
			return turnNodeIntoLiteral(node);
		}
	}

	/**
	 * It takes node and turns given node into {@link Resource} instance
	 * 
	 * @param node
	 *            node which will be turned into {@link Resource}
	 * @return {@link Resource} value of the given node
	 */
	private Resource turnNodeIntoResource(Node node)
			throws UnsupportedNodeTypeException {
		Resource resource = null;
		// check whether node is URI
		if (node.isURI()) {
			// return a new resource using this URI
			resource = ResourceFactory.createResource(node.getURI());
		} else if (node.isBlank()) {
			/**
			 * TODO: Blank Node yapma daha az maliyetli olarak düzeltilecek.
			 */
			resource = ModelFactory.createDefaultModel().createResource(
					node.getBlankNodeId());
		} else {
			throw new UnsupportedNodeTypeException(MessageFormat.format(
					"The given node value \"{0}\" is not a resource", node));
		}
		return resource;
	}

	public boolean isTripleInMemoryGraph(Triple triple) {
		return memoryGraph.isTripleInCache(triple);
	}

	public boolean isNodeInMemoryGraph(Node node) {
		return memoryGraph.isNodeInCache(node);
	}

	public long calculateMemoryUsage() {
		return memoryGraph.calculateMemoryUsage();
	}

	public boolean isQueryInMemoryGraph(Query query, String endpoint) {
		return memoryGraph.isQueryInCache(new DirectedCacheQuery(
				standardizeQuery(query), endpoint));
	}

}
