package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import query.ResultRetriever;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.DatasetImpl;

import core.FederatedCacheQuery;
import core.Querier;
import core.QueryBuilderTool;

public class RegisterIntoMemoryGraphTest {

	private Querier querier;

	private Logger logger;

	@Before
	public void before() {
		// initialize the querier
		querier = new Querier();
		// initialize the logger
		logger = Logger.getLogger(RegisterIntoMemoryGraphTest.class);
	}

	@After
	public void after() throws InterruptedException {
		// clear the memory graph
		querier.clearMemoryGraph();
		Thread.sleep(1000);
	}

	/**
	 * This test checks successfully registering CONSTRUCT query results into
	 * the memory graph which holds most commonly used query results in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void registerIntoMemoryGraphWithConstructQuery() throws Exception {
		// check given query is not contained in the cache
		assertFalse(querier.checkContained(Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT));
		// send a construct query to the related endpoint
		long before = System.currentTimeMillis();
		Model firstExecModel = querier.construct(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		long after = System.currentTimeMillis();
		// check execution times and result
		assertNotNull(firstExecModel);
		assertFalse(firstExecModel.isEmpty());
		logger.info(MessageFormat
				.format("First execution of the CONSTRUCT query has been longed \"{0}\" miliseconds.",
						after - before));
		// check given query contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT));

		// send the same construct query again to the same endpoint
		before = System.currentTimeMillis();
		Model secondExecModel = querier.construct(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		after = System.currentTimeMillis();
		// check execution times and result
		assertNotNull(secondExecModel);
		assertFalse(secondExecModel.isEmpty());
		logger.info(MessageFormat
				.format("Second execution of the CONTSRUCT query has been longed \"{0}\" miliseconds.",
						after - before));
		checkModelsEquals(firstExecModel, secondExecModel);
		// check given query still contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT));

	}

	/**
	 * This test is unused because of describe query has no consistent syntax at
	 * federated apporach
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void registerIntoMemoryGraphWithFederatedDescribeQuery()
			throws Exception {

	}

	/**
	 * This test checks successfully registering CONSTRUCT query results into
	 * the memory graph which holds most commonly used query results in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void registerIntoMemoryGraphWithDescribeQuery() throws Exception {
		// check given query is not contained in the cache
		assertFalse(querier.checkContained(Constants.SAMPLE_DESCRIBE_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));
		// send a describe query to the related endpoint
		long before = System.currentTimeMillis();
		Model firstExecModel = querier.describe(
				Constants.SAMPLE_DESCRIBE_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		long after = System.currentTimeMillis();
		// check execution times and result
		assertNotNull(firstExecModel);
		assertFalse(firstExecModel.isEmpty());
		logger.info(MessageFormat
				.format("First execution of the DESCRIBE query has been longed \"{0}\" miliseconds.",
						after - before));
		// check given query contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_DESCRIBE_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));

		// send the same describe query again to the same endpoint
		before = System.currentTimeMillis();
		Model secondExecModel = querier.construct(
				Constants.SAMPLE_DESCRIBE_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		after = System.currentTimeMillis();
		// check execution times and result
		assertNotNull(secondExecModel);
		assertFalse(secondExecModel.isEmpty());
		logger.info(MessageFormat
				.format("Second execution of the DESCRIBE query has been longed \"{0}\" miliseconds.",
						after - before));
		checkModelsEquals(firstExecModel, secondExecModel);
		// check given query still contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_DESCRIBE_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));

	}

	/**
	 * This test checks successfully registering SELECT query results into the
	 * memory graph which holds most commonly used query results in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void registerIntoMemoryGraphWithSelectQuery() throws Exception {
		// check given query is not contained in the cache
		assertFalse(querier.checkContained(Constants.SAMPLE_SELECT_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));
		// send a select query to the related endpoint
		long before = System.currentTimeMillis();
		// get result set from endpoint
		ResultSet firstResultSet = querier.select(
				Constants.SAMPLE_SELECT_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		// get query solutions from the result set
		List<QuerySolution> firstQuerySolutions = ResultRetriever
				.getQuerySolutions(firstResultSet);
		assertNotNull(firstResultSet);
		long after = System.currentTimeMillis();
		// check execution times and solution list
		assertFalse(firstQuerySolutions.isEmpty());
		logger.info(MessageFormat
				.format("First execution of the SELECT query has been longed \"{0}\" miliseconds.",
						after - before));
		// check given query contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_SELECT_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph
		ResultSet secondResultSet = querier.select(
				Constants.SAMPLE_SELECT_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		// get query solutions from the result set
		List<QuerySolution> secondQuerySolutions = ResultRetriever
				.getQuerySolutions(secondResultSet);
		assertNotNull(secondResultSet);
		after = System.currentTimeMillis();
		// check execution times and solution list
		assertFalse(secondQuerySolutions.isEmpty());
		logger.info(MessageFormat
				.format("Second execution of the SELECT query has been longed \"{0}\" miliseconds.",
						after - before));
		checkSolutionsEquals(firstQuerySolutions, secondQuerySolutions, "s",
				"p", "o");
		// check given query still contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_SELECT_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));

	}

	/**
	 * This test checks successfully registering SELECT query results into the
	 * memory graph which holds most commonly used query results in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void registerIntoMemoryGraphWithFederatedSelectQuery()
			throws Exception {

		// create empty dataset
		Dataset dataset = new DatasetImpl(ModelFactory.createDefaultModel());
		// create federated cache query
		FederatedCacheQuery federatedCacheQuery = new FederatedCacheQuery(
				new QueryBuilderTool(Constants.FEDERATED_LIFE_SCIENCES_2_SELECT)
						.standardize());
		// check given query is not contained in the cache
		assertFalse(querier.checkContained(federatedCacheQuery));
		// send a select query to the related endpoint
		long before = System.currentTimeMillis();
		// get result set from endpoint
		ResultSet firstResultSet = querier.select(
				Constants.FEDERATED_LIFE_SCIENCES_2_SELECT, dataset);
		// get query solutions from the result set
		List<QuerySolution> firstQuerySolutions = ResultRetriever
				.getQuerySolutions(firstResultSet);
		assertNotNull(firstResultSet);
		long after = System.currentTimeMillis();
		// check execution times and solution list
		assertFalse(firstQuerySolutions.isEmpty());
		logger.info(MessageFormat
				.format("First execution of the SELECT query has been longed \"{0}\" miliseconds.",
						after - before));
		// check given query contained in the cache now
		assertTrue(querier.checkContained(federatedCacheQuery));

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph
		ResultSet secondResultSet = querier.select(
				Constants.FEDERATED_LIFE_SCIENCES_2_SELECT, dataset);
		// get query solutions from the result set
		List<QuerySolution> secondQuerySolutions = ResultRetriever
				.getQuerySolutions(secondResultSet);
		assertNotNull(secondResultSet);
		after = System.currentTimeMillis();
		// check execution times and solution list
		assertFalse(secondQuerySolutions.isEmpty());
		logger.info(MessageFormat
				.format("Second execution of the SELECT query has been longed \"{0}\" miliseconds.",
						after - before));
		checkSolutionsEquals(firstQuerySolutions, secondQuerySolutions,
				"predicate", "object");
		// check given query still contained in the cache now
		assertTrue(querier.checkContained(federatedCacheQuery));

	}

	/**
	 * This test checks successfully registering CONSTRUCT query results into
	 * the memory graph which holds most commonly used query results in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void registerIntoMemoryGraphWithFederatedConstructQuery()
			throws Exception {
		// create empty dataset
		Dataset dataset = new DatasetImpl(ModelFactory.createDefaultModel());
		// create federated cache query
		FederatedCacheQuery federatedCacheQuery = new FederatedCacheQuery(
				new QueryBuilderTool(
						Constants.FEDERATED_CONSTRUCT_LIFE_SCIENCES_2)
						.standardize());
		// check given query is not contained in the cache
		assertFalse(querier.checkContained(federatedCacheQuery));
		// send a construct query to the related endpoint
		long before = System.currentTimeMillis();
		Model firstExecModel = querier.construct(
				Constants.FEDERATED_CONSTRUCT_LIFE_SCIENCES_2, dataset);
		long after = System.currentTimeMillis();
		// check execution times and result
		assertNotNull(firstExecModel);
		assertFalse(firstExecModel.isEmpty());
		logger.info(MessageFormat
				.format("First execution of the CONSTRUCT query has been longed \"{0}\" miliseconds.",
						after - before));
		// check given query contained in the cache now
		assertTrue(querier.checkContained(federatedCacheQuery));

		// send the same construct query again to the same endpoint
		before = System.currentTimeMillis();
		Model secondExecModel = querier.construct(
				Constants.FEDERATED_CONSTRUCT_LIFE_SCIENCES_2, dataset);
		after = System.currentTimeMillis();
		// check execution times and result
		assertNotNull(secondExecModel);
		assertFalse(secondExecModel.isEmpty());
		logger.info(MessageFormat
				.format("Second execution of the CONTSRUCT query has been longed \"{0}\" miliseconds.",
						after - before));
		checkModelsEquals(firstExecModel, secondExecModel);
		// check given query still contained in the cache now
		assertTrue(querier.checkContained(federatedCacheQuery));

	}

	/**
	 * This test checks successfully registering ASK query result which is
	 * positive, into the memory graph which holds most commonly used query
	 * results in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void registerIntoMemoryGraphWithPositiveAskQuery() throws Exception {
		// check given query is not contained in the cache
		assertFalse(querier.checkContained(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));
		// send a select query to the related endpoint
		long before = System.currentTimeMillis();
		// get result set from endpoint
		Boolean firstResult = querier.ask(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		long after = System.currentTimeMillis();
		// check execution times and solution list
		assertTrue(firstResult);
		logger.info(MessageFormat
				.format("First execution of the POSITIVE ASK query has been longed \"{0}\" miliseconds.",
						after - before));
		// check given query contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph
		Boolean secondResult = querier.ask(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		after = System.currentTimeMillis();
		// check execution times and solution list
		assertTrue(secondResult);
		logger.info(MessageFormat
				.format("Second execution of the POSITIVE ASK query has been longed \"{0}\" miliseconds.",
						after - before));
		assertEquals(firstResult, secondResult);
		// check given query still contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));

	}

	/**
	 * This test checks successfully registering ASK query result which is
	 * positive, into the memory graph which holds most commonly used query
	 * results in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void registerIntoMemoryGraphWithFederatedAskQuery() throws Exception {
		// create empty dataset
		Dataset dataset = new DatasetImpl(ModelFactory.createDefaultModel());
		// create federated cache query
		FederatedCacheQuery federatedCacheQuery = new FederatedCacheQuery(
				new QueryBuilderTool(Constants.FEDERATED_ASK_LIFE_SCIENCES_2)
						.standardize());
		// check given query is not contained in the cache
		assertFalse(querier.checkContained(federatedCacheQuery));
		// send a select query to the related endpoint
		long before = System.currentTimeMillis();
		// get result set from endpoint
		Boolean firstResult = querier.ask(
				Constants.FEDERATED_ASK_LIFE_SCIENCES_2, dataset);
		long after = System.currentTimeMillis();
		// check execution times and solution list
		assertTrue(firstResult);
		logger.info(MessageFormat
				.format("First execution of the POSITIVE ASK query has been longed \"{0}\" miliseconds.",
						after - before));
		// check given query contained in the cache now
		assertTrue(querier.checkContained(federatedCacheQuery));

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph
		Boolean secondResult = querier.ask(
				Constants.FEDERATED_ASK_LIFE_SCIENCES_2, dataset);
		after = System.currentTimeMillis();
		// check execution times and solution list
		assertTrue(secondResult);
		logger.info(MessageFormat
				.format("Second execution of the POSITIVE ASK query has been longed \"{0}\" miliseconds.",
						after - before));
		assertEquals(firstResult, secondResult);
		// check given query still contained in the cache now
		assertTrue(querier.checkContained(federatedCacheQuery));

	}

	/**
	 * This test checks successfully registering ASK query which is negative,
	 * into the memory graph which holds most commonly used query results in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void registerIntoMemoryGraphWithNegativeAskQuery() throws Exception {
		// check given query is not contained in the cache
		assertFalse(querier.checkContained(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// send a select query to the related endpoint
		long before = System.currentTimeMillis();
		// get result set from endpoint
		Boolean firstResult = querier.ask(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		long after = System.currentTimeMillis();
		// check execution times and solution list
		assertFalse(firstResult);
		logger.info(MessageFormat
				.format("First execution of the NEGATIVE ASK query has been longed \"{0}\" miliseconds.",
						after - before));
		// check given query contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_NYTIMES_ENDPOINT));

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph
		Boolean secondResult = querier.ask(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		after = System.currentTimeMillis();
		// check execution times and solution list
		assertFalse(secondResult);
		logger.info(MessageFormat
				.format("Second execution of the NEGATIVE ASK query has been longed \"{0}\" miliseconds.",
						after - before));
		assertEquals(firstResult, secondResult);
		// check given query still contained in the cache now
		assertTrue(querier.checkContained(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_NYTIMES_ENDPOINT));

	}

	/**
	 * This method checks whether given two models are logically equals
	 * 
	 * @param firstModel
	 *            first {@link Model} instance to compare.
	 * @param secondModel
	 *            second {@link Model} instance to compare.
	 * @return {@link Boolean} state of equality.
	 */
	private void checkModelsEquals(Model firstModel, Model secondModel) {
		List<Statement> statementsOfFirstModel = firstModel.listStatements()
				.toList();
		List<Statement> statementsOfSecondModel = secondModel.listStatements()
				.toList();
		// check statement size
		assertEquals(statementsOfFirstModel.size(),
				statementsOfSecondModel.size());
		// check first model contains the statements of second model
		for (Statement statementOfSecondModel : statementsOfSecondModel) {
			assertTrue(statementsOfFirstModel.contains(statementOfSecondModel));
		}
		// check second model contains the statements of first model
		for (Statement statementOfFirstModel : statementsOfFirstModel) {
			assertTrue(statementsOfSecondModel.contains(statementOfFirstModel));
		}
	}

	/**
	 * This method compares two {@link QuerySolution} list contains same
	 * elements.
	 * 
	 * @param firstSolutions
	 * @param secondSolutions
	 * @param labels
	 */
	private void checkSolutionsEquals(List<QuerySolution> firstSolutions,
			List<QuerySolution> secondSolutions, String... labels) {
		int solutionSize = firstSolutions.size();
		// check solution sizes are equal
		assertEquals(solutionSize, secondSolutions.size());
		// iterate on each label
		for (String label : labels) {
			// define node lists for current label
			List<RDFNode> arqNodes = new ArrayList<RDFNode>();
			List<RDFNode> boundNodes = new ArrayList<RDFNode>();
			// construct node lists with node label values
			for (int i = 0; i < solutionSize; i++) {
				// fill arq solution
				QuerySolution arqSolution = firstSolutions.get(i);
				arqNodes.add(arqSolution.get(label));
				// fill bound solution
				QuerySolution boundJoinSolution = secondSolutions.get(i);
				boundNodes.add(boundJoinSolution.get(label));
			}
			// control lists are equal
			for (int i = 0; i < solutionSize; i++) {
				// check two node list contains each other nodes.
				assertTrue(arqNodes.contains(boundNodes.get(i)));
				assertTrue(boundNodes.contains(arqNodes.get(i)));
			}
		}
	}

}
