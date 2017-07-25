package test.mock;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Element;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

import core.CachedTriple;
import core.MemoryGraph;
import core.QueryCachable;

public class ClassicCache extends MemoryGraph {

	@Override
	public void store(QueryCachable queryCachable, Model model) {

		// try to get query from the query cache
		if (queryCache.get(queryCachable) == null) {
			// if there is no such query get the statements of result model
			List<Statement> statements = model.listStatements().toList();
			// create a new CachedStatement list
			List<CachedTriple> cachedTriplesOfQuery = new ArrayList<CachedTriple>();
			// iterate on each solution statement and add it to the statement
			// cache and to the cached statements of query
			for (Statement statement : statements) {
				cachedTriplesOfQuery.add(new CachedTriple(statement.asTriple(),
						queryCachable));
			}
			// put the query and its solutions to the query cache
			queryCache.put(new Element(queryCachable, cachedTriplesOfQuery));
		}
	}
	
}
