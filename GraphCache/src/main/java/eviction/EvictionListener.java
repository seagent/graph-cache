package eviction;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import core.MemoryGraph;

public class EvictionListener implements CacheEventListener {

	private MemoryGraph memoryGraph;

	public EvictionListener(MemoryGraph memoryGraph) {
		super();
		this.memoryGraph = memoryGraph;
	}

	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
		// nothing to do
	}

	public void notifyElementPut(Ehcache cache, Element element)
			throws CacheException {
		// nothing to do
	}

	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {
		// nothing to do
	}

	public void notifyElementExpired(Ehcache cache, Element element) {
		// nothing to do
	}

	public void notifyElementEvicted(Ehcache cache, Element element) {
		EvictionManager evictionManager = new EvictionManager(new Evicter(
				element, memoryGraph.getTripleCache(),
				memoryGraph.getNodeCache()));
		evictionManager.evict();
	}

	public void notifyRemoveAll(Ehcache cache) {
		// nothing to do
	}

	public void dispose() {
		// nothing to do
	}

	public Object clone() throws CloneNotSupportedException {
		return new EvictionListener(memoryGraph);
	}

}
