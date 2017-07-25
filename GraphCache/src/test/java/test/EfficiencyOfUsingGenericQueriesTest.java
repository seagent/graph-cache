package test;

import static org.junit.Assert.*;

import org.junit.Test;

import test.mock.QuerierWithNoStandardizedQuery;

import core.Querier;

public class EfficiencyOfUsingGenericQueriesTest {

	@Test
	public void efficiencyForGenericSelectQuery() throws Exception {
		// create not standardized querier
		Querier notStandardizedQuerier = new QuerierWithNoStandardizedQuery();
		// execute three similar query
		notStandardizedQuerier.select(Constants.SIMILAR_SELECT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		// check a similar query is not stored in the cache
		assertFalse(notStandardizedQuerier.isQueryInMemoryGraph(
				Constants.SIMILAR_SELECT_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// clear memory graph
		notStandardizedQuerier.clearMemoryGraph();
		// create standardized querier
		Querier standardizedQuerier = new Querier();
		// execute three similar query
		standardizedQuerier.select(Constants.SIMILAR_SELECT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		// check a similar query is stored in the cache
		assertTrue(standardizedQuerier.isQueryInMemoryGraph(
				Constants.SIMILAR_SELECT_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// clear memory graph
		standardizedQuerier.clearMemoryGraph();
	}

	@Test
	public void efficiencyForGenericConstructQuery() throws Exception {
		// create not standardized querier
		Querier notStandardizedQuerier = new QuerierWithNoStandardizedQuery();
		// execute a construct query
		notStandardizedQuerier.construct(Constants.SIMILAR_CONSTRUCT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		// check a similar query is not stored in the cache
		assertFalse(notStandardizedQuerier.isQueryInMemoryGraph(
				Constants.SIMILAR_CONSTRUCT_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// clear memory graph
		notStandardizedQuerier.clearMemoryGraph();
		// create standardized querier
		Querier standardizedQuerier = new Querier();
		// execute a construct query
		standardizedQuerier.construct(Constants.SIMILAR_CONSTRUCT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		// check a similar query is stored in the cache
		assertTrue(standardizedQuerier.isQueryInMemoryGraph(
				Constants.SIMILAR_CONSTRUCT_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// clear memory graph
		standardizedQuerier.clearMemoryGraph();
	}

	@Test
	public void efficiencyForGenericDescribeQuery() throws Exception {
		// create not standardized querier
		Querier notStandardizedQuerier = new QuerierWithNoStandardizedQuery();
		// execute a describe query
		notStandardizedQuerier.describe(Constants.SIMILAR_DESCRIBE_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		// check a similar query is not stored in the cache
		assertFalse(notStandardizedQuerier.isQueryInMemoryGraph(
				Constants.SIMILAR_DESCRIBE_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// clear memory graph
		notStandardizedQuerier.clearMemoryGraph();
		// create standardized querier
		Querier standardizedQuerier = new Querier();
		// execute a describe query
		standardizedQuerier.describe(Constants.SIMILAR_DESCRIBE_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		// check a similar query is stored in the cache
		assertTrue(standardizedQuerier.isQueryInMemoryGraph(
				Constants.SIMILAR_DESCRIBE_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// clear memory graph
		standardizedQuerier.clearMemoryGraph();
	}

	@Test
	public void efficiencyForGenericAskQuery() throws Exception {
		// create not standardized querier
		Querier notStandardizedQuerier = new QuerierWithNoStandardizedQuery();
		// execute an ask query
		notStandardizedQuerier.ask(Constants.SIMILAR_ASK_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		// check a similar query is not stored in the cache
		assertFalse(notStandardizedQuerier.isQueryInMemoryGraph(
				Constants.SIMILAR_ASK_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// clear memory graph
		notStandardizedQuerier.clearMemoryGraph();
		// create standardized querier
		Querier standardizedQuerier = new Querier();
		// execute an ask query
		standardizedQuerier.ask(Constants.SIMILAR_ASK_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		// check a similar query is stored in the cache
		assertTrue(standardizedQuerier.isQueryInMemoryGraph(
				Constants.SIMILAR_ASK_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT));
		// clear memory graph
		standardizedQuerier.clearMemoryGraph();
	}

}
