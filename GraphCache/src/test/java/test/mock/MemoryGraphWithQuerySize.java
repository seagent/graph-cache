package test.mock;

import net.sf.ehcache.CacheManager;
import test.Constants;
import core.MemoryGraph;

public class MemoryGraphWithQuerySize extends MemoryGraph {

	@Override
	protected void initCache() {
		// Initialize the CacheManager
		CacheManager cacheManager = Constants.getQuerySizedCache(this);
		// Get the Cache to store device information
		queryCache = cacheManager.getCache(Constants.RESTRICTED_QUERY_CACHE);
	}

}
