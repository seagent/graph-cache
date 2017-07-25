package test.mock;

import net.sf.ehcache.CacheManager;
import test.Constants;

public class ClassicCacheWithSize extends ClassicCache {

	@Override
	protected void initCache() {

		// create byte sized cache
		CacheManager cacheManager = Constants.getByteSizedCache();
		// set the query cache
		queryCache = cacheManager.getCache("byteCache");
	}

}
