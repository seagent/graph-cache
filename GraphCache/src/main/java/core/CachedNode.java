package core;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;

public class CachedNode {

	private Node node;
	private HashMap<Integer, QueryCachable> queryMap;

	public CachedNode(Node node, QueryCachable queryCachable) {
		this(node);
		addDefinedQuery(queryCachable);
	}

	public CachedNode(Node node) {
		super();
		this.node = node;
		this.queryMap = new HashMap<Integer, QueryCachable>();
	}

	/**
	 * This method adds query with its endpoint to the query map which contains
	 * relevant query-endpoint pairs.
	 * 
	 * @param directedCacheQuery
	 *            {@link Query} instance which is executed.
	 * @param endpoint
	 *            endpoint which the query executed on.
	 */
	public void addDefinedQuery(QueryCachable directedCacheQuery) {
		// get its hash code
		int pairCode = directedCacheQuery.hashCode();
		// get pair value from query map if there is any
		QueryCachable containedDQ = queryMap.get(pairCode);
		// check whether the contained pair is null or not
		if (containedDQ == null) {
			// if so put the pair to the related key value.
			queryMap.put(pairCode, directedCacheQuery);
		}
	}

	public Node getNode() {
		return node;
	}

	public HashMap<Integer, QueryCachable> getQueryMap() {
		return queryMap;
	}

	public void removeQuery(DirectedCacheQuery directedCacheQuery) {
		queryMap.remove(directedCacheQuery.hashCode());
	}

	public int getSizeOfQueryMap() {
		return queryMap.values().size();
	}

	public QueryCachable getRelatedQuery(int key) {
		return queryMap.get(key);
	}

	public void removeQuery(int key) {
		queryMap.remove(key);
	}

}
