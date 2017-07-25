package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import query.ResultRetriever;
import test.mock.ClassicCache;
import test.mock.ClassicCacheWithSize;
import test.mock.MemoryGraphWithMemorySize;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

import core.CachedTriple;
import core.DirectedCacheQuery;
import core.MemoryGraph;

public class MemoryUsageTest {

	private Logger logger = Logger.getLogger(MemoryUsageTest.class);

	@Test
	public void useNodeCache() throws Exception {

		// create memory graph
		MemoryGraph memoryGraph = new MemoryGraph();

		// create a default model to add sample statement to the memory graph
		// using it
		Model modelFirst = ModelFactory.createDefaultModel();

		// create first Burak resource object
		Resource burakRscFirst = ResourceFactory
				.createResource("http://seagent.ege.edu.tr/resource/burakyonyul");

		// create name of Burak literal object
		Literal nameOfBurak = ResourceFactory
				.createPlainLiteral("Burak Yönyül");
		// create foaf name object
		Property foafNamePrp = FOAF.name;
		// create first sample statement to store in model
		Statement statementName = new StatementImpl(burakRscFirst, foafNamePrp,
				nameOfBurak, (ModelCom) modelFirst);
		// add statement to the first model to store it
		modelFirst.add(statementName);
		// store first model in the memory graph
		memoryGraph.store(new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT), modelFirst);

		// create a default model again to add sample statement to the memory
		// graph using it
		Model modelSecond = ModelFactory.createDefaultModel();

		// create second Burak resource object
		Resource burakRscSecond = ResourceFactory
				.createResource("http://seagent.ege.edu.tr/resource/burakyonyul");

		// create sample Tayfun resource object
		Resource tayfunRsc = ResourceFactory
				.createResource("http://seagent.ege.edu.tr/resource/tayfunhalac");
		// create foaf knows property object
		Property foafKnowsPrp = FOAF.knows;
		// create second sample statement to store in model
		Statement statementKnows = new StatementImpl(burakRscSecond,
				foafKnowsPrp, tayfunRsc, (ModelCom) modelSecond);
		// add statement to the second model to store it
		modelSecond.add(statementKnows);
		// store second model in the memory graph
		memoryGraph.store(new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT), modelSecond);

		/**
		 * get previously created nodes and triples from cache and check whether
		 * the nodes of the triples are only firstly added ones.
		 */

		// get first statement
		CachedTriple cachedTripleFirst = memoryGraph.getTriplesOfQuery(
				new DirectedCacheQuery(Constants.SAMPLE_CONSTRUCT_QUERY_1,
						Constants.DUMPED_DBPEDIA_ENDPOINT)).get(0);

		// get second statement
		CachedTriple cachedTripleSecond = memoryGraph.getTriplesOfQuery(
				new DirectedCacheQuery(Constants.SAMPLE_CONSTRUCT_QUERY_1,
						Constants.DUMPED_NYTIMES_ENDPOINT)).get(0);

		// check Tayfun node
		Node tayfunNodeInMemoryGraph = memoryGraph.getNode(tayfunRsc.asNode())
				.getNode();
		assertNotNull(tayfunNodeInMemoryGraph);
		assertTrue(cachedTripleSecond.getTriple().getObject() == tayfunNodeInMemoryGraph);

		// check foaf name property
		Node foafNameInMemory = memoryGraph.getNode(FOAF.name.asNode())
				.getNode();
		assertNotNull(foafNameInMemory);
		assertTrue(cachedTripleFirst.getTriple().getPredicate() == foafNameInMemory);

		// check foaf knows property
		Node foafKnowsInMemory = memoryGraph.getNode(FOAF.knows.asNode())
				.getNode();
		assertNotNull(foafKnowsInMemory);
		assertTrue(cachedTripleSecond.getTriple().getPredicate() == foafKnowsInMemory);

		// check name literal of Burak
		Node nameOfBurakInMemory = memoryGraph.getNode(nameOfBurak.asNode())
				.getNode();
		assertNotNull(nameOfBurakInMemory);
		assertTrue(cachedTripleFirst.getTriple().getObject() == nameOfBurakInMemory);

		// check the instance of Burak stored in the cache is the firstly
		// created node
		Node burakRscInMemoryGraph = memoryGraph.getNode(
				burakRscSecond.asNode()).getNode();
		assertNotNull(burakRscInMemoryGraph);
		assertTrue(cachedTripleFirst.getTriple().getSubject() == burakRscInMemoryGraph);
		assertTrue(cachedTripleSecond.getTriple().getSubject() == burakRscInMemoryGraph);

	}

	@Test
	public void useStatementCache() throws Exception {
		// create memory graph
		MemoryGraph memoryGraph = new MemoryGraph();

		// get results of a sample query
		Model resultModelFirst = (Model) new ResultRetriever(
				Constants.DUMPED_DBPEDIA_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_1).retrieve();

		// store result model in memory graph
		memoryGraph.store(new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_DBPEDIA_ENDPOINT), resultModelFirst);

		// get results of a sample query
		Model resultModelSecond = (Model) new ResultRetriever(
				Constants.DUMPED_DBPEDIA_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_2).retrieve();

		// store result model in memory graph
		memoryGraph.store(new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_2,
				Constants.DUMPED_DBPEDIA_ENDPOINT), resultModelSecond);

		// get triples of firstly stored query
		List<CachedTriple> cachedTriplesOfQuery1 = memoryGraph
				.getTriplesOfQuery(new DirectedCacheQuery(
						Constants.SAMPLE_CONSTRUCT_QUERY_1,
						Constants.DUMPED_DBPEDIA_ENDPOINT));

		// get triples of secondly stored query
		List<CachedTriple> cachedTriplesOfQuery2 = memoryGraph
				.getTriplesOfQuery(new DirectedCacheQuery(
						Constants.SAMPLE_CONSTRUCT_QUERY_2,
						Constants.DUMPED_DBPEDIA_ENDPOINT));

		// get intersected triples of given two cached triple list
		List<Triple> intersectedTriples = getIntersectedTriples(
				cachedTriplesOfQuery1, cachedTriplesOfQuery2);

		// check sizes of list
		assertEquals(cachedTriplesOfQuery1.size(), intersectedTriples.size());

		// check references of given two triple to be sure that common results
		// of second query with first query are the exactly same objects
		for (int i = 0; i < intersectedTriples.size(); i++) {
			assertTrue(cachedTriplesOfQuery1.get(i).getTriple() == intersectedTriples
					.get(i));
		}
	}

	@Test
	public void comparingNormalCacheAndMemoryGraphApproachesByCacheSize()
			throws Exception {

		// store three query result using classic caching approach
		ClassicCache classicCache = new ClassicCache();
		storeThreeQueryResult(classicCache);
		// calculate memory usage
		Long sizeOfClassicalCache = classicCache.calculateMemoryUsage();
		logger.info(MessageFormat.format(
				"Size of the memory used by the classical cache: {0} bytes.",
				sizeOfClassicalCache));
		classicCache.clearCache();
		Thread.sleep(1000);

		// store three sample query result using memory graph approach
		MemoryGraph memoryGraph = new MemoryGraph();
		storeThreeQueryResult(memoryGraph);
		// calculate memory usage
		Long sizeOfMemoryGraph = memoryGraph.calculateMemoryUsage();
		logger.info(MessageFormat.format(
				"Size of the memory used by the memory graph: {0} bytes.",
				sizeOfMemoryGraph));
		memoryGraph.clearCache();
		Thread.sleep(1000);

		// check whether memory graph uses less memory than classic caching
		// approach
		assertTrue(sizeOfMemoryGraph < sizeOfClassicalCache);
	}

	@Test
	public void comparingNormalCacheAndMemoryGraphApproachesByElementCount()
			throws Exception {

		// store three query result using classic caching approach
		ClassicCacheWithSize classicCache = new ClassicCacheWithSize();
		storeThreeQueryResult(classicCache);
		// get size of the cache
		int sizeOfClassicalCache = classicCache.getSize();
		// Long sizeOfClassicalCache = classicCache.calculateMemoryUsage();
		logger.info(MessageFormat.format(
				"Count of the elements contained in the classical cache: {0}.",
				sizeOfClassicalCache));
		classicCache.clearCache();
		Thread.sleep(1000);

		// store three sample query result using memory graph approach
		MemoryGraphWithMemorySize memoryGraph = new MemoryGraphWithMemorySize();
		storeThreeQueryResult(memoryGraph);
		// calculate memory usage
		int sizeOfMemoryGraph = memoryGraph.getSize();
		logger.info(MessageFormat.format(
				"Count of the elements contained in the memory graph: {0}.",
				sizeOfMemoryGraph));
		memoryGraph.clearCache();
		Thread.sleep(1000);

		// check whether memory graph uses less memory than classic caching
		// approach
		assertTrue(sizeOfMemoryGraph > sizeOfClassicalCache);
	}

	private void storeThreeQueryResult(MemoryGraph memoryGraph) {

		/**
		 * store results of two query in the memory graph
		 */

		// execute a sample query that retrieves first 10 triples in a dataset
		Query firstQuery = QueryFactory
				.create(Constants.THOUSAND_RESULT_CONSTRUCT_QUERY_1);
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
				.create(Constants.THOUSAND_RESULT_CONSTRUCT_QUERY_2);
		Model model2 = QueryExecutionFactory.sparqlService(nytimesEndpoint,
				secondQuery).execConstruct();
		// add second query results to the listener model which holds some same
		// results with second query results
		memoryGraph.store(new DirectedCacheQuery(secondQuery, nytimesEndpoint),
				model2);

		// exeute another sample query that retrieves first 30 triples in a
		// dataset
		Query thirdQuery = QueryFactory
				.create(Constants.THOUSAND_RESULT_CONSTRUCT_QUERY_3);
		Model model3 = QueryExecutionFactory.sparqlService(nytimesEndpoint,
				thirdQuery).execConstruct();
		// add second query results to the listener model which holds some same
		// results with second query results
		memoryGraph.store(new DirectedCacheQuery(thirdQuery, nytimesEndpoint),
				model3);
	}

	/**
	 * This method return the common triples of given two {@link CachedTriple}
	 * list from the second query.
	 * 
	 * @param cachedTriplesOfQuery1
	 * @param cachedTriplesOfQuery2
	 * @return
	 */
	private List<Triple> getIntersectedTriples(
			List<CachedTriple> cachedTriplesOfQuery1,
			List<CachedTriple> cachedTriplesOfQuery2) {
		// define intersected triple list
		List<Triple> intersectedTriples = new ArrayList<Triple>();
		for (CachedTriple cachedTriple1 : cachedTriplesOfQuery1) {
			for (CachedTriple cachedTriple2 : cachedTriplesOfQuery2) {
				if (cachedTriple2.getTriple().equals(cachedTriple1.getTriple())) {
					intersectedTriples.add(cachedTriple2.getTriple());
				}
			}
		}
		return intersectedTriples;
	}

}
