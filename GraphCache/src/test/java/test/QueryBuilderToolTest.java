package test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

import core.QueryBuilderTool;

public class QueryBuilderToolTest {
	@Test
	public void standardizingEndpointQuery() throws Exception {
		// define query to be standardized.
		String dbpediaDirectorQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX yago: <http://dbpedia.org/class/yago/> "
				+ "PREFIX foaf: <"
				+ FOAF.getURI()
				+ "> "
				+ "PREFIX dbprop: <http://dbpedia.org/property/> "
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
				+ "select * where {"
				+ "?director rdf:type yago:AmericanFilmDirectors. "
				+ "?director foaf:name ?name. "
				+ "?director dbprop:placeOfBirth ?place."
				+ "?film dbpedia-owl:director ?director.}";

		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				dbpediaDirectorQuery);
		// standardize query
		Query standardizedQuery = queryBuilderTool.standardize();
		assertNotNull(standardizedQuery);

		// define expected query text
		String expectedStandardizedQueryText = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX yago: <http://dbpedia.org/class/yago/> "
				+ "PREFIX foaf: <"
				+ FOAF.getURI()
				+ "> "
				+ "PREFIX dbprop: <http://dbpedia.org/property/> "
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
				+ "select * where {"
				+ "?a0 rdf:type yago:AmericanFilmDirectors. "
				+ "?a0 foaf:name ?b0. "
				+ "?a0 dbprop:placeOfBirth ?c0."
				+ "?d0 dbpedia-owl:director ?a0.}";

		// create expected query
		Query expectedStandardizedQuery = QueryFactory
				.create(expectedStandardizedQueryText);
		// check equality of queries
		assertEquals(expectedStandardizedQuery, standardizedQuery);
	}

	@Test
	public void standardizingFederatedQuery() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_2_SELECT);
		// standardize query
		Query standardizedQuery = queryBuilderTool.standardize();
		// create expected standardized query
		Query expectedStandardizedQuery = QueryFactory
				.create(Constants.FEDERATED_STANDARDIZED_LIFE_SCIENCES_2);
		// compare expected and constructed standardized queries
		assertEquals(expectedStandardizedQuery, standardizedQuery);
	}

	@Test
	public void turningSelectFederatedQueryIntoConstruct() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_2_SELECT);
		// turn given select query into construct form
		Query constructQuery = queryBuilderTool.rebuildSelectToConstruct();
		// create expected construct query
		Query expectedConstruct = QueryFactory
				.create(Constants.FEDERATED_CONSTRUCT_LIFE_SCIENCES_2);
		// compare expected and constructed standardized queries
		assertEquals(expectedConstruct, constructQuery);
	}

	@Test
	public void turnFederatedSelectLifeSciencesQuery2IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_2_SELECT);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory
				.create(Constants.RAW_LIFE_SCIENCES_2_SELECT);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedConstructLifeSciencesQuery2IntoRaw()
			throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_CONSTRUCT_LIFE_SCIENCES_2);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory
				.create(Constants.RAW_CONSTRUCT_LIFE_SCIENCES_2);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedAskLifeSciencesQuery2IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_ASK_LIFE_SCIENCES_2);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory
				.create(Constants.RAW_ASK_LIFE_SCIENCES_2);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectLifeSciencesQuery1IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_1);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_LIFE_SCIENCES_1);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectLifeSciencesQuery3IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_3);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_LIFE_SCIENCES_3);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectLifeSciencesQuery4IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_4);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_LIFE_SCIENCES_4);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectLifeSciencesQuery5IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_5);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_LIFE_SCIENCES_5);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectLifeSciencesQuery6IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_6);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_LIFE_SCIENCES_6);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectLifeSciencesQuery7IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_LIFE_SCIENCES_7);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_LIFE_SCIENCES_7);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectCrossDomainQuery1IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_CROSS_DOMAIN_1);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_CROSS_DOMAIN_1);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectCrossDomainQuery2IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_CROSS_DOMAIN_2);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_CROSS_DOMAIN_2);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectCrossDomainQuery3IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_CROSS_DOMAIN_3);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_CROSS_DOMAIN_3);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectCrossDomainQuery4IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_CROSS_DOMAIN_4);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_CROSS_DOMAIN_4);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectCrossDomainQuery5IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_CROSS_DOMAIN_5);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_CROSS_DOMAIN_5);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectCrossDomainQuery6IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_CROSS_DOMAIN_6);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_CROSS_DOMAIN_6);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

	@Test
	public void turnFederatedSelectCrossDomainQuery7IntoRaw() throws Exception {
		// create query builder tool instance
		QueryBuilderTool queryBuilderTool = new QueryBuilderTool(
				Constants.FEDERATED_CROSS_DOMAIN_7);
		// turn given select query into construct form
		Query rawQuery = queryBuilderTool.makeRawQuery();
		// create expected construct query
		Query expectedRaw = QueryFactory.create(Constants.RAW_CROSS_DOMAIN_7);
		// compare expected and constructed standardized queries
		assertEquals(expectedRaw, rawQuery);
	}

}
