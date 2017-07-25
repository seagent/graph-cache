package eviction;

import java.util.HashMap;
import java.util.List;

import net.sf.ehcache.Element;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import core.CachedNode;
import core.CachedTriple;
import core.DirectedCacheQuery;
import core.QueryCachable;

public class Evicter implements Runnable {

	private Element element;

	private HashMap<Triple, CachedTriple> tripleMap;

	private HashMap<Node, CachedNode> nodeMap;

	public Evicter(Element element, HashMap<Triple, CachedTriple> tripleMap,
			HashMap<Node, CachedNode> nodeMap) {
		super();
		this.element = element;
		this.tripleMap = tripleMap;
		this.nodeMap = nodeMap;
	}

	public void run() {
		QueryCachable key = (QueryCachable) element.getObjectKey();
		@SuppressWarnings("unchecked")
		List<CachedTriple> cachedTriples = (List<CachedTriple>) element
				.getObjectValue();
		HashMap<Integer, Node> nodeMap = evictTriples(cachedTriples, key);
		evictNodes(nodeMap, key);
	}

	private void evictNodes(HashMap<Integer, Node> nodes, QueryCachable key) {
		for (Node node : nodes.values()) {
			CachedNode cachedNode = nodeMap.get(node);
			// control the size of
			if (cachedNode != null) {
				if (cachedNode.getSizeOfQueryMap() == 1) {
					checkToRemoveNode(key, cachedNode);
				} else {
					cachedNode.removeQuery(key.hashCode());
				}
			}
		}
	}

	private void checkToRemoveNode(QueryCachable key, CachedNode cachedNode) {
		QueryCachable relatedQuery = cachedNode.getRelatedQuery(key.hashCode());
		if (relatedQuery != null && relatedQuery.equals(key)) {
			nodeMap.remove(cachedNode.getNode());
		}
	}

	private HashMap<Integer, Node> evictTriples(
			List<CachedTriple> cachedTriples, QueryCachable key) {
		HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		for (CachedTriple cachedTriple : cachedTriples) {
			fillNodeList(nodeMap, cachedTriple);
			// control the size of
			if (cachedTriple.getSizeOfQueryMap() == 1) {
				checkToRemoveTriple(key, cachedTriple);
			} else {
				cachedTriple.removeQuery(key.hashCode());
			}
		}
		return nodeMap;
	}

	private void fillNodeList(HashMap<Integer, Node> nodeMap,
			CachedTriple cachedTriple) {
		addNode(nodeMap, cachedTriple.getTriple().getSubject());
		addNode(nodeMap, cachedTriple.getTriple().getPredicate());
		addNode(nodeMap, cachedTriple.getTriple().getObject());
	}

	private void addNode(HashMap<Integer, Node> nodeMap, Node node) {
		int nodeKey = node.hashCode();
		if (nodeMap.get(nodeKey) == null) {
			nodeMap.put(nodeKey, node);
		}
	}

	private void checkToRemoveTriple(QueryCachable key,
			CachedTriple cachedTriple) {
		QueryCachable relatedQuery = cachedTriple.getRelatedQuery(key
				.hashCode());
		if (relatedQuery != null && relatedQuery.equals(key)) {
			tripleMap.remove(cachedTriple.getTriple());
		}
	}

}
