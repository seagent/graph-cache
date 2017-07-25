package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

import core.CachedTriple;
import core.DirectedCacheQuery;
import core.FederatedCacheQuery;
import core.MemoryGraph;
import core.QueryCachable;

public class MemoryGraphTest {

	@Test
	public void addToMemoryGraph() throws Exception {

		// create new MemoryGraph instance
		MemoryGraph memoryGraph = new MemoryGraph();

		// execute Sample Query
		Query firstQuery = QueryFactory
				.create(Constants.SAMPLE_CONSTRUCT_QUERY_1);
		String nytimesEndpoint = Constants.DUMPED_NYTIMES_ENDPOINT;
		Model model = QueryExecutionFactory.sparqlService(nytimesEndpoint,
				firstQuery).execConstruct();

		// add sample query results to the MemoryGraph
		memoryGraph.store(new DirectedCacheQuery(firstQuery, nytimesEndpoint),
				model);

		// control MemoryGraph contains right results
		Collection<CachedTriple> cachedTriples = memoryGraph.getAllTriples();
		checkTriplesAreContained(getTriples(model), cachedTriples, 8);
		// check related queries
		checkRelatedQueries(cachedTriples, firstQuery);

		// check triples of query
		List<CachedTriple> triplesOfFirstQueryFromCache = memoryGraph
				.getTriplesOfQuery(new DirectedCacheQuery(firstQuery,
						nytimesEndpoint));

		// check result triples of first query is correct
		checkTriplesAreEqual(getTriples(model), triplesOfFirstQueryFromCache);

		// exeute another sample query that retrieves first 20 triples in a
		// dataset
		Query secondQuery = QueryFactory
				.create(Constants.SAMPLE_CONSTRUCT_QUERY_2);
		Model model2 = QueryExecutionFactory.sparqlService(nytimesEndpoint,
				Constants.SAMPLE_CONSTRUCT_QUERY_2).execConstruct();

		List<CachedTriple> cloneCachedTriplesFirst = new ArrayList<CachedTriple>();
		cloneCachedTriplesFirst.addAll(cachedTriples);

		// add sample query results to the MemoryGraph
		memoryGraph.store(new DirectedCacheQuery(secondQuery, nytimesEndpoint),
				model2);

		// control MemoryGraph contains right results
		Collection<CachedTriple> cachedTriplesSecond = memoryGraph
				.getAllTriples();
		checkTriplesAreContained(getTriples(model2), cachedTriplesSecond, 18);

		// remove first cached triples
		cachedTriplesSecond.removeAll(cloneCachedTriplesFirst);

		// check related queries for the triples that are common with first
		// and second query
		checkRelatedQueries(cloneCachedTriplesFirst, firstQuery, secondQuery);

		// check related queries for the triples of second query
		checkRelatedQueries(cachedTriplesSecond, secondQuery);

		// check triples of query
		List<CachedTriple> triplesOfSecondQueryFromCache = memoryGraph
				.getTriplesOfQuery(new DirectedCacheQuery(secondQuery,
						nytimesEndpoint));

		// check result triples of first query is correct
		checkTriplesAreEqual(getTriples(model2), triplesOfSecondQueryFromCache);

	}

	private List<Triple> getTriples(Model model) {
		List<Triple> triples = new ArrayList<Triple>();
		List<Statement> statements = model.listStatements().toList();
		for (Statement statement : statements) {
			triples.add(statement.asTriple());
		}
		return triples;
	}

	@Test
	public void removeFromMemoryGraph() throws Exception {

		// create new MemoryGraph instance
		MemoryGraph memoryGraph = new MemoryGraph();

		/**
		 * store results of two query in the memory graph
		 */

		// execute a sample query that retrieves first 10 triples in a dataset
		Query firstQuery = QueryFactory
				.create(Constants.SAMPLE_CONSTRUCT_QUERY_1);
		String nytimesEndpoint = Constants.DUMPED_NYTIMES_ENDPOINT;
		Model model = QueryExecutionFactory.sparqlService(nytimesEndpoint,
				firstQuery).execConstruct();
		// add query results to the memory graph
		memoryGraph.store(new DirectedCacheQuery(firstQuery, nytimesEndpoint),
				model);

		// get current cloned triples
		List<CachedTriple> cloneCachedTriplesFirst = new ArrayList<CachedTriple>();
		cloneCachedTriplesFirst.addAll(memoryGraph.getAllTriples());

		// exeute another sample query that retrieves first 20 triples in a
		// dataset
		Query secondQuery = QueryFactory
				.create(Constants.SAMPLE_CONSTRUCT_QUERY_2);
		Model model2 = QueryExecutionFactory.sparqlService(nytimesEndpoint,
				secondQuery).execConstruct();
		// add second query results to the listener model which holds some same
		// results with second query results
		memoryGraph.store(new DirectedCacheQuery(secondQuery, nytimesEndpoint),
				model2);

		// exeute another sample query that retrieves first 30 triples in a
		// dataset
		Query thirdQuery = QueryFactory
				.create(Constants.SAMPLE_CONSTRUCT_QUERY_3);
		Model model3 = QueryExecutionFactory.sparqlService(nytimesEndpoint,
				thirdQuery).execConstruct();
		// add second query results to the listener model which holds some same
		// results with second query results
		memoryGraph.store(new DirectedCacheQuery(thirdQuery, nytimesEndpoint),
				model3);

		// remove a query that is decided to be evicted from the cache.
		memoryGraph.release(secondQuery, nytimesEndpoint);

		// control MemoryGraph contains right results
		Collection<CachedTriple> cachedTriples = memoryGraph.getAllTriples();
		checkTriplesAreContained(getTriples(model3), cachedTriples, 26);

		// remove first triples
		cachedTriples.removeAll(cloneCachedTriplesFirst);

		// check related queries for the triples that are common with first
		// and third query
		checkRelatedQueries(cloneCachedTriplesFirst, firstQuery, thirdQuery);

		// check related queries for the triples of third query
		checkRelatedQueries(cachedTriples, thirdQuery);

		// check triples of query
		List<CachedTriple> triplesOfThirdQueryFromCache = memoryGraph
				.getTriplesOfQuery(new DirectedCacheQuery(thirdQuery,
						nytimesEndpoint));

		// check result triples of first query is correct
		checkTriplesAreEqual(getTriples(model3), triplesOfThirdQueryFromCache);

		// check there is no result about query 2 is contained in the query
		// cache
		List<CachedTriple> triplesOfSecondQuery = memoryGraph
				.getTriplesOfQuery(new DirectedCacheQuery(secondQuery,
						nytimesEndpoint));
		assertNull(triplesOfSecondQuery);

	}

	/**
	 * Checks whether given two triple list are equal or not.
	 * 
	 * @param firstTriples
	 * @param secondTriples
	 */
	private void checkTriplesAreEqual(List<Triple> firstTriples,
			List<CachedTriple> secondTriples) {
		assertEquals(firstTriples.size(), secondTriples.size());
		for (CachedTriple secondTriple : secondTriples) {
			assertTrue(firstTriples.contains(secondTriple.getTriple()));
		}
	}

	/**
	 * This method checks related queries of cached triples
	 * 
	 * @param cachedTriples
	 * @param firstQuery
	 */
	private void checkRelatedQueries(Collection<CachedTriple> cachedTriples,
			Query... queries) {
		// check availability of comparing objects
		assertNotNull(queries);
		assertNotNull(cachedTriples);
		assertFalse(cachedTriples.isEmpty());
		// get each cached triple and check related queries of them with
		// given control queries
		for (CachedTriple cachedTriple : cachedTriples) {
			Collection<QueryCachable> directedQueriesOfCachedTriple = cachedTriple
					.getQueryMap().values();
			List<Query> queriesOfCachedTriple = turnDirectedQueriesIntoQueries(directedQueriesOfCachedTriple);
			assertEquals(queries.length, directedQueriesOfCachedTriple.size());
			for (Query query : queries) {
				assertTrue(queriesOfCachedTriple.contains(query));
			}
		}
	}

	/**
	 * This method gets {@link Query}s contained in the given
	 * {@link DirectedCacheQuery} list and creates new {@link Query}
	 * {@link List} using them.
	 * 
	 * @param directedQueriesOfCachedTriple
	 *            {@link DirectedCacheQuery} instances that contains query and
	 *            its endpoint
	 * @return {@link Query} {@link List} instance
	 */
	private List<Query> turnDirectedQueriesIntoQueries(
			Collection<QueryCachable> directedQueriesOfCachedTriple) {
		// define query list
		List<Query> queries = new ArrayList<Query>();
		for (QueryCachable cachableQuery : directedQueriesOfCachedTriple) {
			// add query of directed query
			if (cachableQuery instanceof DirectedCacheQuery) {
				DirectedCacheQuery directedCachableQuery = (DirectedCacheQuery) cachableQuery;
				queries.add(directedCachableQuery.getQuery());
			} else {
				FederatedCacheQuery federatedCachableQuery = (FederatedCacheQuery) cachableQuery;
				queries.add(federatedCachableQuery.getFederatedQuery());
			}
		}
		return queries;
	}

	/**
	 * This method checks cached triples contains given triples.
	 * 
	 * @param triples
	 * @param cachedTriples
	 * @param expectedSize
	 * @param query
	 */
	private void checkTriplesAreContained(List<Triple> triples,
			Collection<CachedTriple> cachedTriples, int expectedSize) {
		// control sizes
		assertEquals(expectedSize, cachedTriples.size());

		// generate new triple list from cached triples
		List<Triple> triplesFromCache = generateTripleListFromCachedTriples(cachedTriples);

		// check endpoint triples are contained by cached triples
		for (Triple tripleFromEndpoint : triples) {
			assertTrue(triplesFromCache.contains(tripleFromEndpoint));
		}
	}

	/**
	 * Generates {@link Triple} {@link List} from given {@link CachedTriple}
	 * {@link List}.
	 * 
	 * @param cachedTriples
	 *            {@link CachedTriple} {@link List}
	 * @return new {@link Triple} {@link List}
	 */
	private List<Triple> generateTripleListFromCachedTriples(
			Collection<CachedTriple> cachedTriples) {
		List<Triple> triples = new ArrayList<Triple>();
		for (CachedTriple cachedTriple : cachedTriples) {
			triples.add(cachedTriple.getTriple());
		}
		return triples;
	}
}
