package test.mock;

import net.sf.ehcache.CacheManager;
import test.Constants;
import core.MemoryGraph;

public class MemoryGraphWithMemorySize extends MemoryGraph {

	@Override
	protected void initCache() {
		// Initialize the CacheManager
		CacheManager cacheManager = Constants.getByteSizedCache();
		// Get the Cache to store device information
		queryCache = cacheManager.getCache("byteCache");
	}

}
