package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import query.ResultRetriever;
import test.mock.QuerierWithQuerySizeAndListener;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import core.DirectedCacheQuery;
import core.MemoryGraphConstants;
import core.Querier;
import core.QueryBuilderTool;
import core.QueryCachable;

public class EvictionFromMemoryGraphTest {

	@Test
	public void evictionOfConstructQuery() throws Exception {

		/**
		 * Send three queries those have intersected results and which one of
		 * them will be evicted
		 */

		// create querier object
		Querier querier = new QuerierWithQuerySizeAndListener();

		// send first query
		Model model1 = querier.construct(
				Constants.DBPEDIA_CONSTRUCT_OFFSET_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		// send second query
		Model model2 = querier.construct(
				Constants.DBPEDIA_CONSTRUCT_OFFSET_QUERY_2,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		model2 = querier.construct(Constants.DBPEDIA_CONSTRUCT_OFFSET_QUERY_2,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		// send third query
		Model model3 = querier.construct(
				Constants.DBPEDIA_CONSTRUCT_OFFSET_QUERY_3,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		/**
		 * According to these first query is evicted and second and third
		 * queries are in cache now.
		 */

		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.DBPEDIA_CONSTRUCT_OFFSET_QUERY_1);

		// check that first query is evicted
		assertFalse(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));
		List<Triple> triplesOfFirstModel = ResultRetriever.getTriples(model1);
		Thread.sleep(1000);
		// check that two triple are evicted from triple cache
		verifyEvictionOfTriple(querier, triplesOfFirstModel.get(0));
		verifyEvictionOfTriple(querier, triplesOfFirstModel.get(3));
		// check that second five triple those are intersected with other
		// queries still remain in the cache.
		verifyInclusionOfTriple(querier, triplesOfFirstModel.get(1));
		verifyInclusionOfTriple(querier, triplesOfFirstModel.get(2));
		// get subjects of triples

		queryBuilderTool.setQuery(Constants.DBPEDIA_CONSTRUCT_OFFSET_QUERY_2);
		// check that second query is in the cache
		assertTrue(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));
		List<Triple> triplesOfSecondModel = ResultRetriever.getTriples(model2);
		// check triples of second query remain in the cache
		verifyInclusionOfTriple(querier, triplesOfSecondModel.get(0));
		verifyInclusionOfTriple(querier, triplesOfSecondModel.get(1));
		verifyInclusionOfTriple(querier, triplesOfSecondModel.get(2));
		verifyInclusionOfTriple(querier, triplesOfSecondModel.get(3));

		queryBuilderTool.setQuery(Constants.DBPEDIA_CONSTRUCT_OFFSET_QUERY_3);
		// check that third query is in the cache
		assertTrue(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));
		List<Triple> triplesOfThirdModel = ResultRetriever.getTriples(model3);
		// check triples of third query remain in the cache
		verifyInclusionOfTriple(querier, triplesOfThirdModel.get(0));
		verifyInclusionOfTriple(querier, triplesOfThirdModel.get(1));
		verifyInclusionOfTriple(querier, triplesOfThirdModel.get(2));
		verifyInclusionOfTriple(querier, triplesOfThirdModel.get(3));

		// clear query cache
		querier.clearMemoryGraph();

	}

	@Test
	public void evictionOfDescribeQuery() throws Exception {

		/**
		 * Send three queries those have intersected results and which one of
		 * them will be evicted
		 */

		// create querier object
		Querier querier = new QuerierWithQuerySizeAndListener();

		// send first query
		Model model1 = querier.describe(
				Constants.DBPEDIA_DESCRIBE_ROGER_FEDERER,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		// send second query
		Model model2 = querier.describe(
				Constants.DBPEDIA_DESCRIBE_LIONEL_MESSI,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		model2 = querier.describe(Constants.DBPEDIA_DESCRIBE_LIONEL_MESSI,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		// send third query
		Model model3 = querier.describe(Constants.DBPEDIA_DESCRIBE_KOBE_BRYANT,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		/**
		 * According to these first query is evicted and second and third
		 * queries are in cache now.
		 */

		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.DBPEDIA_DESCRIBE_ROGER_FEDERER);

		// check that first query is evicted
		assertFalse(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));
		List<Triple> triplesOfFirstModel = ResultRetriever.getTriples(model1);
		Thread.sleep(1000);

		// check all triples and subjects of first query are evicted
		verifyEvictionOfTriples(querier, triplesOfFirstModel);

		queryBuilderTool.setQuery(Constants.DBPEDIA_DESCRIBE_LIONEL_MESSI);
		// check that second query is in the cache
		assertTrue(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));
		List<Triple> triplesOfSecondModel = ResultRetriever.getTriples(model2);
		// check triples of second query remain in the cache
		verifyInclusionOfTriples(querier, triplesOfSecondModel);

		queryBuilderTool.setQuery(Constants.DBPEDIA_DESCRIBE_KOBE_BRYANT);
		// check that third query is in the cache
		assertTrue(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));
		List<Triple> triplesOfThirdModel = ResultRetriever.getTriples(model3);
		// check triples of third query remain in the cache
		verifyInclusionOfTriples(querier, triplesOfThirdModel);

		// clear query cache
		querier.clearMemoryGraph();

	}

	@Test
	public void evictionOfSelectQuery() throws Exception {

		/**
		 * Send three queries those have intersected results and which one of
		 * them will be evicted
		 */

		// create querier object
		Querier querier = new QuerierWithQuerySizeAndListener();

		// send first query
		ResultSet resultSet1 = querier.select(
				Constants.DBPEDIA_SELECT_OFFSET_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		// send second query
		ResultSet resultSet2 = querier.select(
				Constants.DBPEDIA_SELECT_OFFSET_QUERY_2,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		resultSet2 = querier.select(Constants.DBPEDIA_SELECT_OFFSET_QUERY_2,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		// send third query
		ResultSet resultSet3 = querier.select(
				Constants.DBPEDIA_SELECT_OFFSET_QUERY_3,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		/**
		 * According to these first query is evicted and second and third
		 * queries are in cache now.
		 */

		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.DBPEDIA_SELECT_OFFSET_QUERY_1);

		// check that first query is evicted
		assertFalse(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));

		List<QuerySolution> firstQuerySolutions = ResultRetriever
				.getQuerySolutions(resultSet1);
		assertEquals(4, firstQuerySolutions.size());
		List<Triple> triplesOfFirstModel = turnSolutionsIntoTriples(firstQuerySolutions);
		Thread.sleep(1000);
		// check that two triple are evicted from triple cache
		verifyEvictionOfTriple(querier, triplesOfFirstModel.get(1));
		verifyEvictionOfTriple(querier, triplesOfFirstModel.get(3));
		// check that second five triple those are intersected with other
		// queries still remain in the cache.
		verifyInclusionOfTriple(querier, triplesOfFirstModel.get(0));
		verifyInclusionOfTriple(querier, triplesOfFirstModel.get(2));
		// get subjects of triples
		// check that second query is in the cache
		queryBuilderTool.setQuery(Constants.DBPEDIA_SELECT_OFFSET_QUERY_2);
		assertTrue(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));
		List<QuerySolution> secondQuerySolutions = ResultRetriever
				.getQuerySolutions(resultSet2);
		assertEquals(4, secondQuerySolutions.size());
		List<Triple> triplesOfSecondModel = turnSolutionsIntoTriples(secondQuerySolutions);
		// check triples of second query remain in the cache
		verifyInclusionOfTriple(querier, triplesOfSecondModel.get(0));
		verifyInclusionOfTriple(querier, triplesOfSecondModel.get(1));
		verifyInclusionOfTriple(querier, triplesOfSecondModel.get(2));
		verifyInclusionOfTriple(querier, triplesOfSecondModel.get(3));

		// check that third query is in the cache
		queryBuilderTool.setQuery(Constants.DBPEDIA_SELECT_OFFSET_QUERY_3);
		assertTrue(querier.checkContained(new DirectedCacheQuery(
				queryBuilderTool.standardize(),
				Constants.DUMPED_DBPEDIA_ENDPOINT)));
		List<QuerySolution> thirdQuerySolutions = ResultRetriever
				.getQuerySolutions(resultSet3);
		assertEquals(4, thirdQuerySolutions.size());
		List<Triple> triplesOfThirdModel = turnSolutionsIntoTriples(thirdQuerySolutions);
		// check triples of third query remain in the cache
		verifyInclusionOfTriple(querier, triplesOfThirdModel.get(0));
		verifyInclusionOfTriple(querier, triplesOfThirdModel.get(1));
		verifyInclusionOfTriple(querier, triplesOfThirdModel.get(2));
		verifyInclusionOfTriple(querier, triplesOfThirdModel.get(3));

		// clear query cache
		querier.clearMemoryGraph();

	}

	private List<Triple> turnSolutionsIntoTriples(
			List<QuerySolution> querySolutions) {
		List<Triple> triples = new ArrayList<Triple>();
		for (QuerySolution querySolution : querySolutions) {
			Resource bandRsc = querySolution.get("band").asResource();
			Node bandNode = bandRsc.asNode();
			Node typeNode = RDF.type.asNode();
			Node bandClsNode = Constants.DBPEDIA_BAND_CLS.asNode();
			triples.add(Triple.create(bandNode, typeNode, bandClsNode));
		}
		return triples;
	}

	@Test
	public void evictionOfAskQuery() throws Exception {

		/**
		 * Send three queries those have intersected results and which one of
		 * them will be evicted
		 */

		// create querier object
		Querier querier = new QuerierWithQuerySizeAndListener();

		// send first query
		querier.ask(Constants.DBPEDIA_ASK_ROGER_FEDERER,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		// send second query
		querier.ask(Constants.DBPEDIA_ASK_BURAK_YONYUL,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		querier.ask(Constants.DBPEDIA_ASK_BURAK_YONYUL,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		// send third query
		querier.ask(Constants.DBPEDIA_ASK_KOBE_BRYANT,
				Constants.DUMPED_DBPEDIA_ENDPOINT);

		/**
		 * According to these first query is evicted and second and third
		 * queries are in cache now.
		 */

		// define query builder tool and set query Roger Federer query as first
		// query.
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.DBPEDIA_ASK_ROGER_FEDERER);
		// standardize query
		Query standardizedRogerFedererQuery = queryBuilderTool.standardize();
		// define directed query
		DirectedCacheQuery directedRogerFedererQuery = new DirectedCacheQuery(
				standardizedRogerFedererQuery,
				Constants.DUMPED_DBPEDIA_ENDPOINT);
		// check that first query is evicted
		assertFalse(querier.checkContained(directedRogerFedererQuery));
		Thread.sleep(1000);
		Triple evictedFedererTriple = createTriple(directedRogerFedererQuery,
				MemoryGraphConstants.HAS_RESULT, true);
		// check that two triple are evicted from triple cache
		assertFalse(querier.isTripleInMemoryGraph(evictedFedererTriple));
		assertFalse(querier.isNodeInMemoryGraph(evictedFedererTriple
				.getSubject()));
		assertTrue(querier.isNodeInMemoryGraph(evictedFedererTriple
				.getPredicate()));
		assertTrue(querier
				.isNodeInMemoryGraph(evictedFedererTriple.getObject()));
		// set Burak Yönyül query as the query of query builder tool
		queryBuilderTool.setQuery(Constants.DBPEDIA_ASK_BURAK_YONYUL);
		// standardize query
		Query standardizedBurakYonyulQuery = queryBuilderTool.standardize();
		// create directed query
		DirectedCacheQuery directedBurakYonyulQuery = new DirectedCacheQuery(
				standardizedBurakYonyulQuery, Constants.DUMPED_DBPEDIA_ENDPOINT);
		// check that second query is in the cache
		assertTrue(querier.checkContained(directedBurakYonyulQuery));
		Triple includedBurakTriple = createTriple(directedBurakYonyulQuery,
				MemoryGraphConstants.HAS_RESULT, false);
		// check triples of second query remain in the cache
		assertTrue(querier.isTripleInMemoryGraph(includedBurakTriple));
		assertTrue(querier
				.isNodeInMemoryGraph(includedBurakTriple.getSubject()));
		assertTrue(querier.isNodeInMemoryGraph(includedBurakTriple
				.getPredicate()));
		assertTrue(querier.isNodeInMemoryGraph(includedBurakTriple.getObject()));
		// set Kobe Bryant query as the query of query builder tool
		queryBuilderTool.setQuery(Constants.DBPEDIA_ASK_KOBE_BRYANT);
		// standardize query
		Query standardizedKobeBryantQuery = queryBuilderTool.standardize();
		// create directed query
		DirectedCacheQuery directedKobeBryantQuery = new DirectedCacheQuery(
				standardizedKobeBryantQuery, Constants.DUMPED_DBPEDIA_ENDPOINT);
		// check that third query is in the cache
		assertTrue(querier.checkContained(directedKobeBryantQuery));
		Triple includedKobeTriple = createTriple(directedKobeBryantQuery,
				MemoryGraphConstants.HAS_RESULT, true);
		// check triples of third query remain in the cache
		assertTrue(querier.isTripleInMemoryGraph(includedKobeTriple));
		assertTrue(querier.isNodeInMemoryGraph(includedKobeTriple.getSubject()));
		assertTrue(querier.isNodeInMemoryGraph(includedKobeTriple
				.getPredicate()));
		assertTrue(querier.isNodeInMemoryGraph(includedKobeTriple.getObject()));

		// clear query cache
		querier.clearMemoryGraph();
	}

	/**
	 * Create a triple using given subject and predicate URIs and given
	 * objectQuery
	 * 
	 * @param cachableSubjectQuery
	 * @param predicateURI
	 *            predicate URI
	 * @param objectResult
	 * @return new {@link Triple} instance
	 */
	private Triple createTriple(QueryCachable cachableSubjectQuery,
			String predicateURI, boolean objectResult) {
		Triple inclusionOfFedererTriple = Triple.create(
				ResourceFactory.createResource(
						MemoryGraphConstants.QUERY_RSC_PREFIX
								+ cachableSubjectQuery.hashCode()).asNode(),
				ResourceFactory.createProperty(predicateURI).asNode(),
				ResourceFactory.createTypedLiteral(
						String.valueOf(objectResult), XSDDatatype.XSDboolean)
						.asNode());
		return inclusionOfFedererTriple;
	}

	/**
	 * This method checks which triples in given list are evicted from the
	 * cache.
	 * 
	 * @param querier
	 * @param triples
	 * @param startingBound
	 * @param endingBound
	 */
	private void verifyEvictionOfTriple(Querier querier, Triple evictedTriple) {
		// check evicted triples
		assertFalse(querier.isTripleInMemoryGraph(evictedTriple));
		assertFalse(querier.isNodeInMemoryGraph(evictedTriple.getSubject()));
	}

	private void verifyEvictionOfTriples(Querier querier,
			List<Triple> evictedTriples) {
		for (Triple evictedTriple : evictedTriples) {
			verifyEvictionOfTriple(querier, evictedTriple);
		}
	}

	private void verifyInclusionOfTriples(Querier querier,
			List<Triple> includedTriples) {
		for (Triple includedTriple : includedTriples) {
			verifyInclusionOfTriple(querier, includedTriple);
		}
	}

	/**
	 * This method checks which triples in given list are evicted from the
	 * cache.
	 * 
	 * @param querier
	 * @param triples
	 * @param startingBound
	 * @param endingBound
	 */
	private void verifyInclusionOfTriple(Querier querier, Triple includedTriple) {
		// check included triples
		assertTrue(querier.isTripleInMemoryGraph(includedTriple));
		assertTrue(querier.isNodeInMemoryGraph(includedTriple.getSubject()));
		assertTrue(querier.isNodeInMemoryGraph(includedTriple.getPredicate()));
		assertTrue(querier.isNodeInMemoryGraph(includedTriple.getObject()));
	}

}
