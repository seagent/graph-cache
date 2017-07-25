package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.ehcache.CacheEvictionTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CacheEvictionTest.class,
		EfficiencyOfMemoryGraphApproachTest.class, MemoryGraphTest.class,
		MemoryUsageTest.class, QueryBuilderToolTest.class,
		RegisterIntoMemoryGraphTest.class, EvictionFromMemoryGraphTest.class })
public class AllTest {

}
