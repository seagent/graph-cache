package core;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class DirectedCacheQuery implements QueryCachable {

	private final Query query;
	private final String endpoint;

	public DirectedCacheQuery(Query query, String endpoint) {
		this.query = query;
		this.endpoint = endpoint;
	}

	public DirectedCacheQuery(String query, String endpoint) {
		this(QueryFactory.create(query), endpoint);
	}

	public Query getQuery() {
		return query;
	}

	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endpoint == null) ? 0 : endpoint.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
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
		DirectedCacheQuery other = (DirectedCacheQuery) obj;
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.equals(other.endpoint))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		return true;
	}

}
