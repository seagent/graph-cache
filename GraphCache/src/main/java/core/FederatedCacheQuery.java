package core;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class FederatedCacheQuery implements QueryCachable {

	private Query federatedQuery;

	public FederatedCacheQuery(Query federatedQuery) {
		super();
		this.federatedQuery = federatedQuery;
	}

	public FederatedCacheQuery(String federatedQueryText) {
		this(QueryFactory.create(federatedQueryText));
	}

	public Query getFederatedQuery() {
		return federatedQuery;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((federatedQuery == null) ? 0 : federatedQuery.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FederatedCacheQuery other = (FederatedCacheQuery) obj;
		if (federatedQuery == null) {
			if (other.federatedQuery != null)
				return false;
		} else if (!federatedQuery.equals(other.federatedQuery))
			return false;
		return true;
	}

}
