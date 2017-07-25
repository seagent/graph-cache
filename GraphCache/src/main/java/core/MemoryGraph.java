package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class MemoryGraph {

	private HashMap<Node, CachedNode> nodeCache;

	/**
	 * the {@link HashMap} instance that holds queries and their solutions as
	 * mappings
	 */
	protected Cache queryCache;

	/**
	 * the {@link HashMap} instance that holds triples and their cached triple
	 * mappings
	 */
	private HashMap<Triple, CachedTriple> tripleCache;

	public MemoryGraph() {
		super();
		nodeCache = new HashMap<Node, CachedNode>();
		tripleCache = new HashMap<Triple, CachedTriple>();
		initCache();
		queryCache.removeAll();
	}

	public Collection<CachedTriple> getAllTriples() {
		return this.tripleCache.values();
	}

	public HashMap<Node, CachedNode> getNodeCache() {
		return nodeCache;
	}

	public CachedNode getNode(Node node) {
		return nodeCache.get(node);
	}

	@SuppressWarnings("unchecked")
	public List<CachedTriple> getTriplesOfQuery(Query query, String endpoint) {

		DirectedCacheQuery directedCacheQuery = new DirectedCacheQuery(query,
				endpoint);
		Element element = this.queryCache.get(directedCacheQuery);
		return element == null ? null : (List<CachedTriple>) element
				.getObjectValue();
	}

	/**
	 * This method removes given query and its solutions from the cache. While
	 * removing query solutions relevance of statements with other queries is
	 * taken into account too, thus is a statement is relevant with another
	 * query, it remains in the cache and is not removed.
	 * 
	 * @param query
	 */
	public void release(Query query, String endpoint) {

		DirectedCacheQuery directedCacheQuery = new DirectedCacheQuery(query,
				endpoint);
		// get solution statements of given query
		Element element = queryCache.get(directedCacheQuery);
		@SuppressWarnings("unchecked")
		List<CachedTriple> cachedTriples = (List<CachedTriple>) element
				.getObjectValue();
		// check whether the given query remains in the cache and has some
		// solutions
		if (cachedTriples != null) {
			// iterate on each statement and remove it from the statement cache
			for (CachedTriple cachedTriple : cachedTriples) {
				releaseTriple(cachedTriple, directedCacheQuery);
			}
			// finally remove the query from the query cache
			queryCache.remove(directedCacheQuery);
		}

	}

	/**
	 * This method adds solution model of the given query
	 * 
	 * @param query
	 *            {@link Query} instance whose solutions will be cached.
	 * @param endpoint
	 * @param model
	 *            {@link Model} instance that holds solutions of given query.
	 */
	public void store(QueryCachable cachableQuery, Model model) {
		store(cachableQuery, model.listStatements().toList());
	}

	/**
	 * This method adds solution model of the given query
	 * 
	 * @param query
	 *            {@link Query} instance whose solutions will be cached.
	 * @param endpoint
	 * @param solution
	 *            solution of the query
	 */
	@SuppressWarnings("unchecked")
	public void store(QueryCachable cachableQuery, Object solution) {
		if (solution instanceof Model) {
			store(cachableQuery, (Model) solution);
		} else if (solution instanceof List<?>) {
			store(cachableQuery, (List<Statement>) solution);
		} else {
			store(cachableQuery, (Triple) solution);
		}
	}

	/**
	 * This method adds solution statements of the given query
	 * 
	 * @param query
	 *            {@link Query} instance whose solutions will be cached.
	 * @param endpoint
	 * @param statements
	 *            {@link Statement} list to be stored in the cache
	 */
	public void store(QueryCachable cachableQuery, List<Statement> statements) {
		// create a new CachedStatement list
		List<CachedTriple> cachedTriplesOfQuery = new ArrayList<CachedTriple>();
		// iterate on each solution statement and add it to the statement
		// cache and to the cached statements of query
		for (Statement statement : statements) {
			storeTriple(cachableQuery, statement.asTriple(),
					cachedTriplesOfQuery);
		}
		// put the query and its solutions to the query cache
		queryCache.put(new Element(cachableQuery, cachedTriplesOfQuery));
	}

	/**
	 * This method adds solution statements of the given query
	 * 
	 * @param query
	 *            {@link Query} instance whose solutions will be cached.
	 * @param endpoint
	 * @param triple
	 */
	public void store(QueryCachable cachableQuery, Triple triple) {

		// create a new CachedStatement list
		List<CachedTriple> cachedTriplesOfQuery = new ArrayList<CachedTriple>();
		// add given triple to the triple cache
		storeTriple(cachableQuery, triple, cachedTriplesOfQuery);
		// put the query and its solutions to the query cache
		queryCache.put(new Element(cachableQuery, cachedTriplesOfQuery));
	}

	protected void initCache() {
		// Initialize the CacheManager
		CacheManager cacheManager = CacheManager.create();
		// Get the Cache to store device information
		queryCache = cacheManager.getCache("queryCache");
	}

	/**
	 * This method releases the given cached triple if it is relevant only the
	 * given query
	 * 
	 * @param cachedTriple
	 *            {@link CachedTriple} instance which may be removed from the
	 *            triple cache
	 * @param directedCacheQuery
	 *            {@link Query} instance which may be removed from the related
	 *            queries of given triple
	 */
	private void releaseTriple(CachedTriple cachedTriple,
			DirectedCacheQuery directedCacheQuery) {
		// check the related query size of the cached triple and remove if it
		// is only relevant with one query
		if (cachedTriple.getSizeOfQueryMap() == 1) {
			tripleCache.remove(cachedTriple.getTriple());
		} else {
			// if the triple relevant more than one query then remove the
			// given query from its relevant query list.
			cachedTriple.removeQuery(directedCacheQuery);
		}
	}

	/**
	 * This method checks whether triple contained in the cache, if so adds the
	 * new query its related queries, otherwise it stores the triple in the
	 * cache.
	 * 
	 * @param cachableQuery
	 * @param tripleFromEndpoint
	 * @param cachedTriplesOfQuery
	 */
	private void storeTriple(QueryCachable cachableQuery,
			Triple tripleFromEndpoint, List<CachedTriple> cachedTriplesOfQuery) {
		// create a triple with new nodes
		Triple tripleForReuse = createTripleForReuse(tripleFromEndpoint,
				cachableQuery);
		// try to get triple from cache
		CachedTriple cachedTriple = tripleCache.get(tripleForReuse);
		// if it is not contained in the cache create new cached triple
		if (cachedTriple == null) {
			cachedTriple = new CachedTriple(tripleForReuse, cachableQuery);
		} else {
			// otherwise add the given query to the related queries of triple
			cachedTriple.addDefinedQuery(cachableQuery);
		}
		// put cached triple to the cache
		tripleCache.put(tripleForReuse, cachedTriple);
		// add cached triple to the cached triple of the given query
		cachedTriplesOfQuery.add(cachedTriple);
	}

	/**
	 * This method creates
	 * 
	 * @param tripleFromEndpoint
	 * @param cachableQuery
	 * @return
	 */
	private Triple createTripleForReuse(Triple tripleFromEndpoint,
			QueryCachable cachableQuery) {

		// try to get subject from cache
		Node subject = getNodeFromMemory(tripleFromEndpoint.getSubject(),
				cachableQuery).getNode();
		// try to get predicate from cache
		Node predicate = getNodeFromMemory(tripleFromEndpoint.getPredicate(),
				cachableQuery).getNode();
		// try to get object from cache
		Node object = getNodeFromMemory(tripleFromEndpoint.getObject(),
				cachableQuery).getNode();
		// create triple and return
		return Triple.create(subject, predicate, object);
	}

	/**
	 * This method retrieves the node from cache if there is exist any,
	 * otherwise it adds the given node to the cache
	 * 
	 * @param node
	 *            {@link Node} instance which is checked for containing in the
	 *            cache
	 * @param cachableQuery
	 * @return contained {@link Node} instance
	 */
	private CachedNode getNodeFromMemory(Node node, QueryCachable cachableQuery) {
		CachedNode nodeInTheMemory = nodeCache.get(node);
		if (nodeInTheMemory != null) {
			nodeInTheMemory.addDefinedQuery(cachableQuery);
			return nodeInTheMemory;
		} else {
			// store node in the node cache
			CachedNode cachedNode = new CachedNode(node, cachableQuery);
			nodeCache.put(node, cachedNode);
			return cachedNode;
		}
	}

	public Long calculateMemoryUsage() {
		return queryCache.getStatistics().getLocalHeapSizeInBytes();
	}

	/**
	 * It clears the query cache
	 */
	public void clearCache() {
		queryCache.dispose();
		CacheManager.create().shutdown();
	}

	public int getSize() {
		return queryCache.getSize();
	}

	public boolean isQueryInCache(DirectedCacheQuery directedCacheQuery) {
		return queryCache.isKeyInCache(directedCacheQuery);
	}

	public boolean isTripleInCache(Triple triple) {
		return tripleCache.get(triple) == null ? false : true;
	}

	public boolean isNodeInCache(Node node) {
		return nodeCache.get(node) == null ? false : true;
	}

	public HashMap<Triple, CachedTriple> getTripleCache() {
		return tripleCache;
	}

	@SuppressWarnings("unchecked")
	public List<CachedTriple> getTriplesOfQuery(QueryCachable cacheQuery) {
		Element element = this.queryCache.get(cacheQuery);
		return element == null ? null : (List<CachedTriple>) element
				.getObjectValue();
	}
}
