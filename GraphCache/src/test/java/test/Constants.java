package test;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import test.mock.MemoryGraphWithQuerySize;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

import eviction.EvictionListener;

public class Constants {

	private static final String CHEBI_4STORE_ENDPOINT_URL = "http://155.223.25.212:3000/sparql/";
	private static final String KEGG_4STORE_ENDPOINT_URL = "http://155.223.25.212:4000/sparql/";
	private static final String DRUGBANK_4STORE_ENDPOINT_URL = "http://155.223.25.212:8000/sparql/";
	private static final String DBPEDIA_4STORE_ENDPOINT_URL = "http://155.223.25.212:7000/sparql/";

	private static final String CHEBI_ENDPOINT_URL = CHEBI_4STORE_ENDPOINT_URL;
	private static final String KEGG_ENDPOINT_URL = KEGG_4STORE_ENDPOINT_URL;
	private static final String DRUGBANK_ENDPOINT_URL = DRUGBANK_4STORE_ENDPOINT_URL;
	private static final String DBPEDIA_ENDPOINT_URL = DBPEDIA_4STORE_ENDPOINT_URL;

	private static final String DBPEDIA_BAND_QUERY = "select * "
			+ "where "
			+ "{?band <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Band>.}";
	public static final String BYTE_CACHE = "byteCache";
	public static final String RESTRICTED_QUERY_CACHE = "restrictedQueryCache";
	private static final String BASIC_SELECT_QUERY = "SELECT * WHERE {?s ?p ?o}";
	private static final String CONSTRUCT = "CONSTRUCT";
	private static final String GENERIC_GRAPH_PATTERN = " {?s ?p ?o}";
	private static final String GENERIC_MODEL_QUERY = GENERIC_GRAPH_PATTERN
			+ " WHERE" + GENERIC_GRAPH_PATTERN;
	public static final long RESTRICTED_CACHE_SIZE_IN_BYTES = 2700000;
	private static final String LIMIT = " LIMIT";
	private static final String GENERIC_CONSTRUCT_QUERY = CONSTRUCT
			+ GENERIC_MODEL_QUERY;
	public static final String DUMPED_NYTIMES_ENDPOINT = "http://155.223.24.47:8891/nytimes/sparql";
	public static final String DUMPED_DBPEDIA_ENDPOINT = "http://155.223.24.47:8897/dbpedia/sparql";
	public static final Query SAMPLE_CONSTRUCT_QUERY_3 = QueryFactory
			.create(GENERIC_CONSTRUCT_QUERY + LIMIT + "30");
	public static final Query SAMPLE_CONSTRUCT_QUERY_2 = QueryFactory
			.create(GENERIC_CONSTRUCT_QUERY + LIMIT + "20");
	public static final Query SAMPLE_CONSTRUCT_QUERY_1 = QueryFactory
			.create(GENERIC_CONSTRUCT_QUERY + LIMIT + "10");
	public static final Query THOUSAND_RESULT_CONSTRUCT_QUERY_1 = QueryFactory
			.create(GENERIC_CONSTRUCT_QUERY + LIMIT + "1000");
	public static final Query THOUSAND_RESULT_CONSTRUCT_QUERY_2 = QueryFactory
			.create(GENERIC_CONSTRUCT_QUERY + LIMIT + "2000");
	public static final Query THOUSAND_RESULT_CONSTRUCT_QUERY_3 = QueryFactory
			.create(GENERIC_CONSTRUCT_QUERY + LIMIT + "3000");
	private static final String SAMPLE_DESCRIBE_QUERY_TEXT = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> DESCRIBE ?person WHERE {?person foaf:name \"Pele\"@en}";
	public static final Query SAMPLE_DESCRIBE_QUERY = QueryFactory
			.create(SAMPLE_DESCRIBE_QUERY_TEXT);
	public static final Query SAMPLE_SELECT_QUERY = QueryFactory
			.create(BASIC_SELECT_QUERY + LIMIT + "10");
	public static final Query SAMPLE_ASK_QUERY = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ASK {?subject foaf:name \"Pele\"@en}");
	private static final String OFFSET = " OFFSET ";
	public static final Resource DBPEDIA_BAND_CLS = ResourceFactory
			.createResource("http://dbpedia.org/ontology/Band");
	private static final String CONSTRUCT_BAND_QUERY = "construct "
			+ "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Band>.} "
			+ "where "
			+ "{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Band>.}";
	public static final Query DBPEDIA_CONSTRUCT_OFFSET_QUERY_1 = QueryFactory
			.create(CONSTRUCT_BAND_QUERY + LIMIT + "4" + OFFSET + "0");
	public static final Query DBPEDIA_CONSTRUCT_OFFSET_QUERY_2 = QueryFactory
			.create(CONSTRUCT_BAND_QUERY + LIMIT + "4" + OFFSET + "2");
	public static final Query DBPEDIA_CONSTRUCT_OFFSET_QUERY_3 = QueryFactory
			.create(CONSTRUCT_BAND_QUERY + LIMIT + "4" + OFFSET + "4");
	public static final Query DBPEDIA_SELECT_OFFSET_QUERY_1 = QueryFactory
			.create(DBPEDIA_BAND_QUERY + LIMIT + "4" + OFFSET + "0");
	public static final Query DBPEDIA_SELECT_OFFSET_QUERY_2 = QueryFactory
			.create(DBPEDIA_BAND_QUERY + LIMIT + "4" + OFFSET + "2");
	public static final Query DBPEDIA_SELECT_OFFSET_QUERY_3 = QueryFactory
			.create(DBPEDIA_BAND_QUERY + LIMIT + "4" + OFFSET + "4");
	private static final long RESTRICTED_CACHE_SIZE_IN_QUERY = 2;
	public static final Query DBPEDIA_DESCRIBE_ROGER_FEDERER = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> DESCRIBE ?person WHERE {?person foaf:name \"Roger Federer\"@en}");
	public static final Query DBPEDIA_DESCRIBE_LIONEL_MESSI = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> DESCRIBE ?person WHERE {?person foaf:name \"Lionel Messi\"@en}");
	public static final Query DBPEDIA_DESCRIBE_KOBE_BRYANT = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> DESCRIBE ?person WHERE {?person foaf:name \"Kobe Bryant\"@en}");

	public static final Query DBPEDIA_ASK_ROGER_FEDERER = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ASK {?person foaf:name \"Roger Federer\"@en}");
	public static final Query DBPEDIA_ASK_BURAK_YONYUL = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ASK {?person foaf:name \"Burak Yönyül\"@en}");
	public static final Query DBPEDIA_ASK_KOBE_BRYANT = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ASK {?person foaf:name \"Kobe Bryant\"@en}");
	public static final Query SIMILAR_SELECT_QUERY_1 = QueryFactory
			.create("SELECT * WHERE {?s ?p ?o} LIMIT 1000");
	public static final Query SIMILAR_SELECT_QUERY_2 = QueryFactory
			.create("SELECT * WHERE {?x ?y ?z} LIMIT 1000");
	public static final Query SIMILAR_CONSTRUCT_QUERY_1 = QueryFactory
			.create("CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o} LIMIT 1000");
	public static final Query SIMILAR_CONSTRUCT_QUERY_2 = QueryFactory
			.create("CONSTRUCT {?x ?y ?z} WHERE {?x ?y ?z} LIMIT 1000");
	public static final Query SIMILAR_DESCRIBE_QUERY_1 = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> DESCRIBE ?person WHERE {?person foaf:name \"Lionel Messi\"@en}");
	public static final Query SIMILAR_DESCRIBE_QUERY_2 = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> DESCRIBE ?footballer WHERE {?footballer foaf:name \"Lionel Messi\"@en}");

	public static final Query SIMILAR_ASK_QUERY_1 = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ASK {?person foaf:name \"Lionel Messi\"@en}");
	public static final Query SIMILAR_ASK_QUERY_2 = QueryFactory
			.create("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ASK {?footballer foaf:name \"Lionel Messi\"@en}");

	public static final String FEDERATED_LIFE_SCIENCES_2_SELECT = "SELECT  ?predicate ?object WHERE {"
			+ "{SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. }} "
			+ "UNION {SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. }"
			+ "{ BIND(<"
			+ DBPEDIA_ENDPOINT_URL
			+ "> AS ?ser1)}  "
			+ "UNION { BIND(<"
			+ KEGG_ENDPOINT_URL
			+ "> AS ?ser1)}  "
			+ "UNION { BIND(<"
			+ CHEBI_ENDPOINT_URL
			+ "> AS ?ser1)} "
			+ "UNION { BIND(<"
			+ DRUGBANK_ENDPOINT_URL
			+ "> AS ?ser1)}  SERVICE ?ser1 " + "{?caff ?predicate ?object. }}}";

	public static final String RAW_LIFE_SCIENCES_2_SELECT = "SELECT  ?predicate ?object WHERE {"
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. } "
			+ "UNION {"
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. "
			+ "?caff ?predicate ?object. }}";

	public static final String FEDERATED_ASK_LIFE_SCIENCES_2 = "ASK  {"
			+ "{SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. }} "
			+ "UNION {SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. }"
			+ "{ BIND(<" + DBPEDIA_ENDPOINT_URL + "> AS ?ser998816)}  "
			+ "UNION { BIND(<" + KEGG_ENDPOINT_URL + "> AS ?ser998816)}  "
			+ "UNION { BIND(<" + CHEBI_ENDPOINT_URL + "> AS ?ser998816)} "
			+ "UNION { BIND(<" + DRUGBANK_ENDPOINT_URL
			+ "> AS ?ser998816)}  SERVICE ?ser998816 "
			+ "{?caff ?predicate ?object. }}}";

	public static final String RAW_ASK_LIFE_SCIENCES_2 = "ASK {"
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. } "
			+ "UNION {"
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. "
			+ "?caff ?predicate ?object. }}";

	public static final String FEDERATED_STANDARDIZED_LIFE_SCIENCES_2 = "SELECT  ?b0 ?c0 "
			+ "WHERE "
			+ "{   { SERVICE <http://155.223.25.212:8000/sparql/> "
			+ "{ <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?b0 ?c0 } "
			+ "} "
			+ "UNION "
			+ "{ SERVICE <http://155.223.25.212:8000/sparql/> "
			+ "{ <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?d0 } "
			+ "{ BIND(<http://155.223.25.212:7000/sparql/> AS ?a0) } "
			+ "UNION "
			+ "{ BIND(<http://155.223.25.212:4000/sparql/> AS ?a0) } "
			+ "UNION "
			+ "{ BIND(<http://155.223.25.212:3000/sparql/> AS ?a0) } "
			+ "UNION "
			+ "{ BIND(<http://155.223.25.212:8000/sparql/> AS ?a0) } "
			+ "SERVICE ?a0 " + "{ ?d0 ?b0 ?c0 } " + "} }";
	public static final String FEDERATED_CONSTRUCT_LIFE_SCIENCES_2 = "CONSTRUCT  { "
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object . "
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff . "
			+ "?caff ?predicate ?object . }"
			+ "WHERE "
			+ "{ "
			+ "{ SERVICE <http://155.223.25.212:8000/sparql/> "
			+ "{ <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object }} "
			+ "UNION "
			+ "{ SERVICE <http://155.223.25.212:8000/sparql/> "
			+ "{ <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff } "
			+ "{ BIND(<http://155.223.25.212:7000/sparql/> AS ?ser1) } "
			+ "UNION "
			+ "{ BIND(<http://155.223.25.212:4000/sparql/> AS ?ser1) } "
			+ "UNION "
			+ "{ BIND(<http://155.223.25.212:3000/sparql/> AS ?ser1) } "
			+ "UNION "
			+ "{ BIND(<http://155.223.25.212:8000/sparql/> AS ?ser1) } "
			+ "SERVICE ?ser1 " + "{ ?caff ?predicate ?object } " + "} }";

	public static final String RAW_CONSTRUCT_LIFE_SCIENCES_2 = "CONSTRUCT  { "
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object . "
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff . "
			+ "?caff ?predicate ?object . }"
			+ "WHERE {"
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. } "
			+ "UNION {"
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. "
			+ "?caff ?predicate ?object. }}";
	public static final String FEDERATED_LIFE_SCIENCES_1 = "SELECT  ?drug ?melt WHERE {{SERVICE <http://localhost:8000/sparql/> {?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/meltingPoint> ?melt. }} UNION { {?drug <http://dbpedia.org/ontology/Drug/meltingPoint> ?melt. }}}";
	public static String RAW_LIFE_SCIENCES_1 = "SELECT ?drug ?melt WHERE {"
			+ "{ ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/meltingPoint> ?melt . } "
			+ "UNION { ?drug <http://dbpedia.org/ontology/Drug/meltingPoint> ?melt . } }";
	public static String FEDERATED_LIFE_SCIENCES_3 = "SELECT  ?Drug ?IntDrug ?IntEffect WHERE {SERVICE <http://localhost:7000/sparql/> {?Drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug>. }SERVICE <http://localhost:8000/sparql/> {?y <http://www.w3.org/2002/07/owl#sameAs> ?Drug. ?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?y. ?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?IntDrug. ?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/text> ?IntEffect. }}";
	public static String RAW_LIFE_SCIENCES_3 = "SELECT  ?Drug ?IntDrug ?IntEffect WHERE {{?Drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug>.} {?y <http://www.w3.org/2002/07/owl#sameAs> ?Drug. ?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?y. ?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?IntDrug. ?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/text> ?IntEffect. }}";
	public static final String FEDERATED_LIFE_SCIENCES_4 = "SELECT  ?drugDesc ?cpd ?equation WHERE {SERVICE <http://localhost:8000/sparql/> {?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/cathartics>. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/description> ?drugDesc. }SERVICE <http://localhost:4000/sparql/> {?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme>. ?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd. ?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme. ?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation. }}";
	public static String RAW_LIFE_SCIENCES_4 = "SELECT  ?drugDesc ?cpd ?equation WHERE {{?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/cathartics>. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/description> ?drugDesc. } {?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme>. ?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd. ?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme. ?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation. }}";
	public static final String FEDERATED_LIFE_SCIENCES_5 = "SELECT  ?drug ?keggUrl ?chebiImage WHERE {SERVICE <http://localhost:8000/sparql/> {?drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugs>. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?keggDrug. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genericName> ?drugBankName. } SERVICE <http://localhost:4000/sparql/> {?keggDrug <http://bio2rdf.org/ns/bio2rdf#url> ?keggUrl. } SERVICE <http://localhost:3000/sparql/> {?chebiDrug <http://purl.org/dc/elements/1.1/title> ?drugBankName. ?chebiDrug <http://bio2rdf.org/ns/bio2rdf#image> ?chebiImage. }}";
	public static String RAW_LIFE_SCIENCES_5 = "SELECT  ?drug ?keggUrl ?chebiImage WHERE {{?drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugs>. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?keggDrug. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genericName> ?drugBankName. } {?keggDrug <http://bio2rdf.org/ns/bio2rdf#url> ?keggUrl. } {?chebiDrug <http://purl.org/dc/elements/1.1/title> ?drugBankName. ?chebiDrug <http://bio2rdf.org/ns/bio2rdf#image> ?chebiImage. }}";
	public static String FEDERATED_LIFE_SCIENCES_6 = "SELECT  ?drug ?title WHERE {SERVICE <http://localhost:8000/sparql/> {?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/micronutrient>. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?id. }SERVICE <http://localhost:4000/sparql/> {?keggDrug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Drug>. ?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?id. ?keggDrug <http://purl.org/dc/elements/1.1/title> ?title. }}";
	public static String RAW_LIFE_SCIENCES_6 = "SELECT  ?drug ?title WHERE {{?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/micronutrient>. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?id. } {?keggDrug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Drug>. ?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?id. ?keggDrug <http://purl.org/dc/elements/1.1/title> ?title. }}";
	public static String FEDERATED_LIFE_SCIENCES_7 = "SELECT  ?drug ?transform ?mass WHERE {SERVICE <http://localhost:8000/sparql/> {?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/affectedOrganism> \"Humans and other mammals\". ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?cas. OPTIONAL {?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/biotransformation> ?transform. }}SERVICE <http://localhost:4000/sparql/> {?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?cas.  { ?keggDrug <http://bio2rdf.org/ns/bio2rdf#mass> ?mass. FILTER ( ?mass > \"5\" ) } }}";
	public static String RAW_LIFE_SCIENCES_7 = "SELECT  ?drug ?transform ?mass WHERE {{?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/affectedOrganism> \"Humans and other mammals\". ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?cas. OPTIONAL {?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/biotransformation> ?transform. }} {?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?cas.  { ?keggDrug <http://bio2rdf.org/ns/bio2rdf#mass> ?mass. FILTER ( ?mass > \"5\" ) } }}";
	public static String FEDERATED_CROSS_DOMAIN_1 = "SELECT  ?predicate ?object WHERE {{SERVICE <http://localhost:7000/sparql/> {<http://dbpedia.org/resource/Barack_Obama> ?predicate ?object. }} UNION {SERVICE <http://localhost:9000/sparql/> {?subject <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama>. ?subject ?predicate ?object. }}}";
	public static String RAW_CROSS_DOMAIN_1 = "SELECT  ?predicate ?object WHERE {{{<http://dbpedia.org/resource/Barack_Obama> ?predicate ?object. }} UNION {{?subject <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama>. ?subject ?predicate ?object. }}}";
	public static String FEDERATED_CROSS_DOMAIN_2 = "SELECT  ?party ?page WHERE {SERVICE <http://localhost:7000/sparql/> {<http://dbpedia.org/resource/Barack_Obama> <http://dbpedia.org/ontology/party> ?party. }SERVICE <http://localhost:9000/sparql/> {?x <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama>. ?x <http://data.nytimes.com/elements/topicPage> ?page. }}";
	public static String RAW_CROSS_DOMAIN_2 = "SELECT  ?party ?page WHERE {{<http://dbpedia.org/resource/Barack_Obama> <http://dbpedia.org/ontology/party> ?party. } {?x <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama>. ?x <http://data.nytimes.com/elements/topicPage> ?page. }}";
	public static String FEDERATED_CROSS_DOMAIN_3 = "SELECT  ?president ?party ?page WHERE {SERVICE <http://localhost:7000/sparql/> {?president <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/President>. ?president <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/United_States>. ?president <http://dbpedia.org/ontology/party> ?party. }SERVICE <http://localhost:9000/sparql/> {?x <http://www.w3.org/2002/07/owl#sameAs> ?president. ?x <http://data.nytimes.com/elements/topicPage> ?page. }}";
	public static String RAW_CROSS_DOMAIN_3 = "SELECT  ?president ?party ?page WHERE {{?president <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/President>. ?president <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/United_States>. ?president <http://dbpedia.org/ontology/party> ?party. }{?x <http://www.w3.org/2002/07/owl#sameAs> ?president. ?x <http://data.nytimes.com/elements/topicPage> ?page. }}";
	public static String FEDERATED_CROSS_DOMAIN_4 = "SELECT  ?actor ?news WHERE {SERVICE <http://localhost:2500/sparql/> {?film <http://purl.org/dc/terms/title> \"Tarzan\". ?film <http://data.linkedmdb.org/resource/movie/actor> ?actor. ?actor <http://www.w3.org/2002/07/owl#sameAs> ?x. }SERVICE <http://localhost:9000/sparql/> {?y <http://data.nytimes.com/elements/topicPage> ?news. ?y <http://www.w3.org/2002/07/owl#sameAs> ?x. }}";
	public static String RAW_CROSS_DOMAIN_4 = "SELECT  ?actor ?news WHERE {{?film <http://purl.org/dc/terms/title> \"Tarzan\". ?film <http://data.linkedmdb.org/resource/movie/actor> ?actor. ?actor <http://www.w3.org/2002/07/owl#sameAs> ?x. } {?y <http://data.nytimes.com/elements/topicPage> ?news. ?y <http://www.w3.org/2002/07/owl#sameAs> ?x. }}";
	public static String FEDERATED_CROSS_DOMAIN_5 = "SELECT  ?film ?director ?genre WHERE {SERVICE <http://localhost:7000/sparql/> {?director <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/Italy>. ?film <http://dbpedia.org/ontology/director> ?director. }SERVICE <http://localhost:2500/sparql/> {?x <http://www.w3.org/2002/07/owl#sameAs> ?film. ?x <http://data.linkedmdb.org/resource/movie/genre> ?genre. }}";
	public static String RAW_CROSS_DOMAIN_5 = "SELECT  ?film ?director ?genre WHERE {{?director <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/Italy>. ?film <http://dbpedia.org/ontology/director> ?director. } {?x <http://www.w3.org/2002/07/owl#sameAs> ?film. ?x <http://data.linkedmdb.org/resource/movie/genre> ?genre. }}";
	public static String FEDERATED_CROSS_DOMAIN_6 = "SELECT  ?name ?location ?news WHERE {SERVICE <http://localhost:2000/sparql/> {?germany <http://www.geonames.org/ontology#name> \"Federal Republic of Germany\". ?location <http://www.geonames.org/ontology#parentFeature> ?germany. } SERVICE <http://localhost:5000/sparql/> {?artist <http://xmlns.com/foaf/0.1/based_near> ?location. ?artist <http://xmlns.com/foaf/0.1/name> ?name. }}";
	public static String RAW_CROSS_DOMAIN_6 = "SELECT  ?name ?location ?news WHERE {{?germany <http://www.geonames.org/ontology#name> \"Federal Republic of Germany\". ?location <http://www.geonames.org/ontology#parentFeature> ?germany. } {?artist <http://xmlns.com/foaf/0.1/based_near> ?location. ?artist <http://xmlns.com/foaf/0.1/name> ?name. }}";
	public static String FEDERATED_CROSS_DOMAIN_7 = "SELECT  ?location ?news WHERE {SERVICE <http://localhost:2000/sparql/> {?parent <http://www.geonames.org/ontology#name> \"California\". ?location <http://www.geonames.org/ontology#parentFeature> ?parent. }SERVICE <http://localhost:9000/sparql/> {?y <http://www.w3.org/2002/07/owl#sameAs> ?location. ?y <http://data.nytimes.com/elements/topicPage> ?news. }}";
	public static String RAW_CROSS_DOMAIN_7 = "SELECT  ?location ?news WHERE {{?parent <http://www.geonames.org/ontology#name> \"California\". ?location <http://www.geonames.org/ontology#parentFeature> ?parent. }{?y <http://www.w3.org/2002/07/owl#sameAs> ?location. ?y <http://data.nytimes.com/elements/topicPage> ?news. }}";

	public static CacheManager getByteSizedCache() {
		// override the cache configuration and configure the cache according to
		// byte number
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		// set name
		cacheConfiguration.setName(BYTE_CACHE);
		// set cache size
		cacheConfiguration.setMaxBytesLocalHeap(RESTRICTED_CACHE_SIZE_IN_BYTES);
		// set eternity of the elements in cache
		cacheConfiguration.setEternal(false);
		// set overflow to disk property
		cacheConfiguration.addPersistence(new PersistenceConfiguration()
				.strategy(PersistenceConfiguration.Strategy.NONE));
		// set buffer size the cache will use
		cacheConfiguration.setDiskSpoolBufferSizeMB(20);
		// set time to idle in seconds
		cacheConfiguration.setTimeToIdleSeconds(86400);
		// set time to live in seconds
		cacheConfiguration.setTimeToLiveSeconds(86400);
		// set eviction policy
		cacheConfiguration
				.setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy.LFU);
		// create the cache
		Cache byteCache = new Cache(cacheConfiguration);
		// Initialize the CacheManager
		CacheManager cacheManager = CacheManager.create();
		// add byte cache to the cache manager
		cacheManager.addCache(byteCache);
		return cacheManager;
	}

	public static CacheManager getQuerySizedCache(
			MemoryGraphWithQuerySize memoryGraph) {
		// override the cache configuration and configure the cache according to
		// byte number
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		// set name
		cacheConfiguration.setName(RESTRICTED_QUERY_CACHE);
		// set cache size
		cacheConfiguration
				.setMaxEntriesLocalHeap(RESTRICTED_CACHE_SIZE_IN_QUERY);
		// set eternity of the elements in cache
		cacheConfiguration.setEternal(false);
		// set overflow to disk property
		cacheConfiguration.addPersistence(new PersistenceConfiguration()
				.strategy(PersistenceConfiguration.Strategy.NONE));
		// set buffer size the cache will use
		cacheConfiguration.setDiskSpoolBufferSizeMB(20);
		// set time to idle in seconds
		cacheConfiguration.setTimeToIdleSeconds(86400);
		// set time to live in seconds
		cacheConfiguration.setTimeToLiveSeconds(86400);
		// set eviction policy
		cacheConfiguration
				.setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy.LFU);
		// create the cache
		Cache querySizedCache = new Cache(cacheConfiguration);
		// add eviction listener to the cache
		querySizedCache.getCacheEventNotificationService().registerListener(
				new EvictionListener(memoryGraph));
		// Initialize the CacheManager
		CacheManager cacheManager = CacheManager.create();
		// add byte cache to the cache manager
		cacheManager.addCache(querySizedCache);
		return cacheManager;
	}
}
