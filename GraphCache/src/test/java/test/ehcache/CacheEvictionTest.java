package test.ehcache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.junit.Test;

import query.ResultRetriever;
import test.Constants;
import core.DirectedCacheQuery;

public class CacheEvictionTest {

	@Test
	public void executeTTLScenario() throws Exception {
		// make the configuration to execute TTL scenario
		CacheConfiguration cacheConfiguration = new CacheConfiguration(
				"ttlCache", 2);
		// set FIFO eviction policy
		cacheConfiguration
				.setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy.FIFO);
		// set time to live time to 3 seconds
		cacheConfiguration.setTimeToLiveSeconds(3);

		CacheManager cacheManager = CacheManager.create();
		// create a new cache
		Cache memoryCache = new Cache(cacheConfiguration);
		cacheManager.addCache(memoryCache);
		Cache ttlCache = cacheManager.getCache("ttlCache");
		ttlCache.removeAll();

		// save a query result to the cache
		ResultRetriever resultRetriever = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_1);
		DirectedCacheQuery directedCacheQuery = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element firstElement = new Element(directedCacheQuery,
				resultRetriever.retrieve());
		ttlCache.put(firstElement);

		// get result immediately after saving it
		assertNotNull(ttlCache.get(directedCacheQuery));

		// sleep 3.5 seconds
		Thread.sleep(3500);

		// get the query result after waiting for TTL value
		assertNull(ttlCache.get(directedCacheQuery));

		// free the cache
		ttlCache.dispose();
		CacheManager.create().shutdown();

		Thread.sleep(1000);
	}

	@Test
	public void executeTTIScenario() throws Exception {
		// make the configuration to execute TTL scenario
		CacheConfiguration cacheConfiguration = new CacheConfiguration(
				"ttlCache", 2);
		// set FIFO eviction policy
		cacheConfiguration
				.setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy.FIFO);
		// set time to idle time to 3 seconds
		cacheConfiguration.setTimeToIdleSeconds(3);

		CacheManager cacheManager = CacheManager.create();
		// create a new cache
		Cache memoryCache = new Cache(cacheConfiguration);
		cacheManager.addCache(memoryCache);
		Cache ttiCache = cacheManager.getCache("ttlCache");
		ttiCache.removeAll();

		// save a query result to the cache
		ResultRetriever resultRetriever = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_1);
		DirectedCacheQuery directedCacheQuery = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element firstElement = new Element(directedCacheQuery,
				resultRetriever.retrieve());
		ttiCache.put(firstElement);

		// get result immediately after saving it
		assertNotNull(ttiCache.get(directedCacheQuery));

		// sleep 3.5 seconds
		Thread.sleep(3500);

		// get the query result after waiting for TTI value
		assertNull(ttiCache.get(directedCacheQuery));

		// free the cache
		ttiCache.dispose();
		CacheManager.create().shutdown();

		Thread.sleep(1000);
	}

	@Test
	public void executeFIFOEvictionPolicy() throws Exception {
		Cache fifoCache = configureCache(MemoryStoreEvictionPolicy.FIFO);

		// save first query result to the cache
		ResultRetriever resultRetriever1 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_1);
		DirectedCacheQuery directedQuery1 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element firstElement = new Element(directedQuery1,
				resultRetriever1.retrieve());
		fifoCache.put(firstElement);

		// save second query result to the cache
		ResultRetriever resultRetriever2 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_2);
		DirectedCacheQuery directedQuery2 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element secondElement = new Element(directedQuery2,
				resultRetriever2.retrieve());
		fifoCache.put(secondElement);

		// save third query result to the cache
		ResultRetriever resultRetriever3 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_3);
		DirectedCacheQuery directedQuery3 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_3,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element thirdElement = new Element(directedQuery3,
				resultRetriever3.retrieve());
		fifoCache.put(thirdElement);

		// first query should not be contained in the cache anymore
		assertNull(fifoCache.get(directedQuery1));

		// second and third queries should remain in the cache
		assertNotNull(fifoCache.get(directedQuery2));

		assertNotNull(fifoCache.get(directedQuery3));

		// free the cache
		fifoCache.dispose();
		CacheManager.create().shutdown();

		Thread.sleep(1000);

	}

	@Test
	public void executeLFUEvictionPolicy() throws Exception {
		Cache lfuCache = configureCache(MemoryStoreEvictionPolicy.LFU);

		// save first query result to the cache
		ResultRetriever resultRetriever1 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_1);
		DirectedCacheQuery directedQuery1 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element firstElement = new Element(directedQuery1,
				resultRetriever1.retrieve());
		lfuCache.put(firstElement);

		// save second query result to the cache
		ResultRetriever resultRetriever2 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_2);
		DirectedCacheQuery directedQuery2 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element secondElement = new Element(directedQuery2,
				resultRetriever2.retrieve());
		lfuCache.put(secondElement);

		// get result of query 1
		lfuCache.get(directedQuery1);

		Thread.sleep(1000);

		// get result of query 2
		lfuCache.get(directedQuery2);

		Thread.sleep(1000);

		// and secondly get result of query 1 to make the query 1 least
		// frequently used
		lfuCache.get(directedQuery1);

		Thread.sleep(1000);

		// save third query result to the cache
		ResultRetriever resultRetriever3 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_3);
		DirectedCacheQuery directedQuery3 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_3,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element thirdElement = new Element(directedQuery3,
				resultRetriever3.retrieve());
		lfuCache.put(thirdElement);

		// second query should not be contained in the cache anymore
		assertNull(lfuCache.get(directedQuery2));

		// first and third queries should remain in the cache
		assertNotNull(lfuCache.get(directedQuery1));

		assertNotNull(lfuCache.get(directedQuery3));

		// free the cache
		lfuCache.dispose();
		CacheManager.create().shutdown();

		Thread.sleep(1000);

	}

	@Test
	public void executeLRUEvictionPolicy() throws Exception {
		Cache lruCache = configureCache(MemoryStoreEvictionPolicy.LRU);

		// save first query result to the cache
		ResultRetriever resultRetriever1 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_1);
		DirectedCacheQuery directedQuery1 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_1,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element firstElement = new Element(directedQuery1,
				resultRetriever1.retrieve());
		lruCache.put(firstElement);

		// save second query result to the cache
		ResultRetriever resultRetriever2 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_2);
		DirectedCacheQuery directedQuery2 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_2,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element secondElement = new Element(directedQuery2,
				resultRetriever2.retrieve());
		lruCache.put(secondElement);

		// get result of query 2
		lruCache.get(directedQuery2);

		Thread.sleep(1000);

		// get result of query 1 to make query 2 least recently used
		lruCache.get(directedQuery1);

		Thread.sleep(1000);

		// save third query result to the cache
		ResultRetriever resultRetriever3 = new ResultRetriever(
				Constants.DUMPED_NYTIMES_ENDPOINT,
				Constants.SAMPLE_CONSTRUCT_QUERY_3);
		DirectedCacheQuery directedQuery3 = new DirectedCacheQuery(
				Constants.SAMPLE_CONSTRUCT_QUERY_3,
				Constants.DUMPED_NYTIMES_ENDPOINT);
		Element thirdElement = new Element(directedQuery3,
				resultRetriever3.retrieve());
		lruCache.put(thirdElement);

		// second query should not be contained in the cache anymore
		assertNull(lruCache.get(directedQuery2));

		// first and third queries should remain in the cache
		assertNotNull(lruCache.get(directedQuery1));

		assertNotNull(lruCache.get(directedQuery3));

		// free the cache
		lruCache.dispose();
		CacheManager.create().shutdown();

		Thread.sleep(1000);

	}

	private Cache configureCache(MemoryStoreEvictionPolicy evictionPolicy) {
		// make a configuration that will be executed as FIFO overflow scenario
		CacheConfiguration cacheConfiguration = new CacheConfiguration(
				"evictionCache", 2);
		// set FIFO eviction policy
		cacheConfiguration
				.setMemoryStoreEvictionPolicyFromObject(evictionPolicy);
		// set time to idle time
		cacheConfiguration.setTimeToIdleSeconds(300);
		// set time to live time
		cacheConfiguration.setTimeToLiveSeconds(600);

		CacheManager cacheManager = CacheManager.create();
		// create a new cache
		Cache memoryCache = new Cache(cacheConfiguration);
		cacheManager.addCache(memoryCache);
		Cache evictionCache = cacheManager.getCache("evictionCache");
		evictionCache.removeAll();
		return evictionCache;
	}

}
