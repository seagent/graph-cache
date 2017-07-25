package core;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class MemoryGraphConstants {

	public static final String QUERY_RSC_PREFIX = "http://seagent.ege.edu.tr/memorygraph/cachequery/";
	public static final String MEMORY_GRAPH_PREFIX = "http://graph.memory.com/ontology/";
	public static final String HAS_RESULT = MEMORY_GRAPH_PREFIX + "hasResult";
	public static final Property CONTAINS_PRP = ResourceFactory
			.createProperty(HAS_RESULT);
	public static final String DOES_NOT_CONTAIN_URI = MEMORY_GRAPH_PREFIX
			+ "doesNotContain";
	public static final Property DOES_NOT_CONTAIN_PRP = ResourceFactory
			.createProperty(DOES_NOT_CONTAIN_URI);

}
