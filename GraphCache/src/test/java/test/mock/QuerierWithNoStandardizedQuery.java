package test.mock;

import com.hp.hpl.jena.query.Query;

import core.Querier;

public class QuerierWithNoStandardizedQuery extends Querier {

	@Override
	protected Query standardizeQuery(Query query) {
		// don't standardize the query and return the same query instance
		return query;
	}

}
