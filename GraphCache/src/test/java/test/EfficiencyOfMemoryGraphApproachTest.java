package test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import query.ResultRetriever;
import core.Querier;

public class EfficiencyOfMemoryGraphApproachTest {

	private Querier querier;

	@Before
	public void before() {
		// initialize the querier
		querier = new Querier();
	}

	@After
	public void after() throws InterruptedException {
		// clear the memory graph
		querier.clearMemoryGraph();
		Thread.sleep(1000);
	}

	@Test
	public void efficencyOfSelectQuery() throws Exception {
		long before = System.currentTimeMillis();
		// get result set from endpoint and get query solutions from the result
		// set
		ResultRetriever.getQuerySolutions(querier.select(
				Constants.SAMPLE_SELECT_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));
		long after = System.currentTimeMillis();
		long executionTimeOnEndpoint = after - before;

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph and get query solutions from the
		// result set
		ResultRetriever.getQuerySolutions(querier.select(
				Constants.SAMPLE_SELECT_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT));
		after = System.currentTimeMillis();
		long executionTimeOnMemoryGraph = after - before;
		// check efficiency
		assertTrue(executionTimeOnMemoryGraph < executionTimeOnEndpoint);
	}

	@Test
	public void efficencyOfConstructQuery() throws Exception {
		long before = System.currentTimeMillis();
		// get result set from endpoint and get query solutions from the result
		// set
		querier.construct(Constants.SAMPLE_CONSTRUCT_QUERY_3,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		long after = System.currentTimeMillis();
		long executionTimeOnEndpoint = after - before;

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph and get query solutions from the
		// result set
		querier.construct(Constants.SAMPLE_CONSTRUCT_QUERY_3,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		after = System.currentTimeMillis();
		long executionTimeOnMemoryGraph = after - before;
		// check efficiency
		assertTrue(executionTimeOnMemoryGraph < executionTimeOnEndpoint);
	}

	@Test
	public void efficiencyOfDescribeQuery() throws Exception {
		long before = System.currentTimeMillis();
		// get result set from endpoint and get query solutions from the result
		// set
		querier.describe(Constants.SAMPLE_DESCRIBE_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		long after = System.currentTimeMillis();
		long executionTimeOnEndpoint = after - before;

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph and get query solutions from the
		// result set
		querier.describe(Constants.SAMPLE_DESCRIBE_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		after = System.currentTimeMillis();
		long executionTimeOnMemoryGraph = after - before;
		// check efficiency
		assertTrue(executionTimeOnMemoryGraph < executionTimeOnEndpoint);
	}

	@Test
	public void efficiencyOfAskQuery() throws Exception {
		long before = System.currentTimeMillis();
		// get result set from endpoint and get query solutions from the result
		// set
		querier.ask(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		long after = System.currentTimeMillis();
		long executionTimeOnEndpoint = after - before;

		// send the same select query again to the same endpoint
		before = System.currentTimeMillis();
		// get result set from the memory graph and get query solutions from the
		// result set
		querier.ask(Constants.SAMPLE_ASK_QUERY,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		after = System.currentTimeMillis();
		long executionTimeOnMemoryGraph = after - before;
		// check efficiency
		assertTrue(executionTimeOnMemoryGraph < executionTimeOnEndpoint);
	}

}
