package edu.mayo.bsi.cts.cts2connector.cts2search;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TestConvenienceMethods
{
	private String serviceName = "PY4CTS2";
	private ServiceResultFormat format = ServiceResultFormat.JSON;
	private ConvenienceMethods cm = null;
	private RESTContext serviceContext = null;
	
	@Before
	public void setup()
	{
		System.out.println("\n\n***************************** BEGIN TestConvenienceMethods for " + serviceName + " ********************************\n");
		cm = ConvenienceMethods.instance();

		try
		{
			serviceContext = cm.getContext(serviceName);
		} catch (SearchException e) {
			e.printStackTrace();
		}
		serviceContext.setOutputFormat(format);
	}

	@After
	public void afterTest()
	{
		//System.out.println("\n************************************** END ************************************************\n");
	}
	/////////////////////////////////////////////////////////////////////////////////
	/*
	 *  LOCAL REPOSITORY METHODS
	 */
	/////////////////////////////////////////////////////////////////////////////////
	
	private void printFewLines(String str, int limit)
	{
		System.out.println((str == null)?"value is null.": str.substring(0, ((limit > 0)?limit:0)));
	}
	
	@Test
	public void testGetAvailableVocabularies()
	{
		System.out.println("TESTING... testGetAvailableVocabularies");
		
		try
		{
			String vocabs = cm.getAvailableVocabularies(serviceContext);
			assertNotNull(vocabs);
			printFewLines(vocabs, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}

	@Test
	public void testGetMatchingVocabularies()
	{
		System.out.println("TESTING... testGetMatchingVocabularies");

		try
		{
			String selectedVocabs = cm.getMatchingVocabularies("SNO", serviceContext);
			assertNotNull(selectedVocabs);
			printFewLines(selectedVocabs, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}

	@Test
	public void testGetMatchingWithSwitchContextOfBaseURL()
	{
		System.out.println("TESTING... testGetMatchingWithSwitchContextOfBaseURL");
		
		try
		{
			cm.addUpdateRESTBaseServiceURL("myOwnCTS2Service", "http://informatics.mayo.edu/py4cts2/");
			RESTContext myCtx = cm.getContext("myOwnCTS2Service");
			String selectedVocabs = cm.getMatchingVocabularies("SNO", myCtx);
			assertNotNull(selectedVocabs);
			printFewLines(selectedVocabs, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}
	
	@Test
	public void testGetVocabularyDefinition()
	{
		System.out.println("TESTING... testGetVocabularyDefinition");
		
		try
		{
			//VocabularyId csv = new VocabularyId();
			//csv.name = "SNOMED_CT";
			//csv.version = "20150131";
			String vocab = cm.getVocabularyDefinition(null, serviceContext);
			assertNotNull(vocab);
			printFewLines(vocab, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}

	private String getMatchingEntities(String phrase, MatchAlgorithm matchAlgorithm) throws SearchException
	{
			VocabularyId csv = new VocabularyId();
			csv.name = "SNOMED_CT";
			csv.version = "20150131";
			serviceContext.addSearchEntity(SearchableEntity.CONCEPT, true);
			serviceContext.matchAlgorithm_ = matchAlgorithm;
			return cm.getVocabularyEntities(phrase, csv, serviceContext);
	}
	@Test
	public void testGetAllEntities()
	{
		System.out.println("TESTING... testGetAllEntities");

		try
		{
			String vocab = getMatchingEntities(null, null);
			assertNotNull(vocab);
			printFewLines(vocab, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}

	@Test
	public void testGetExactMatchingEntities()
	{
		System.out.println("TESTING... testGetExactMatchingEntities");

		try
		{
			String vocab = getMatchingEntities("Glucose", MatchAlgorithm.EXACT);
			assertNotNull(vocab);
			printFewLines(vocab, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}

	@Test
	public void testGetContainsMatchEntities()
	{
		System.out.println("TESTING... testGetExactMatchingEntities");

		try
		{
			String vocab = getMatchingEntities("Glucose", MatchAlgorithm.CONTAINS);
			assertNotNull(vocab);
			printFewLines(vocab, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}

	/*  // TODO: Uncomment and test following methods
	@Test
	public void testGetAvailableValueSetsAsXML()
	{
		System.out.println("TESTING... testGetAvailableValueSetsAsXML");
		
		try
		{
			String vel = cm.getAvailableValueSets(false, false, false, serviceContext);

			// TODO: DEEPAK Not yet implemented with py4cts2 - update it later
			assertNull(vel);
			//printFewLines(vel, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}
	
	@Test
	public void testGetMatchingValueSetsAsXML()
	{
		System.out.println("TESTING... testGetMatchingValueSetsAsXML");
		
		try
		{
			String vel = cm.getMatchingValueSets("SNOMED-C", false, false, false, serviceContext);
			// TODO: DEEPAK Not yet implemented with py4cts2 - update it later
			assertNull(vel);
			//printFewLines(vel, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fail();
	}
	
	@Test
	public void testEntityAsXML()
	{
		System.out.println("TESTING... testEntityAsXML");
		
		try
		{
			String uriStr ="http://informatics.mayo.edu/cts2/rest/codesystem/SNOMEDCT-CORE/version/CORE_2011_01_31/entity/10050004";
			String vel = cm.getVocabularyEntityByURI(uriStr, serviceContext);
			assertNotNull(vel);
			printFewLines(vel, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		fail();
	}
	
	@Test
	public void testEntityAsJSON()
	{
		System.out.println("TESTING... testEntityAsJSON");
		
		try
		{
			String uriStr ="http://informatics.mayo.edu/cts2/rest/codesystem/SNOMEDCT-CORE/version/CORE_2011_01_31/entity/10050004";
			serviceContext.setOutputFormat(ServiceResultFormat.JSON);
			String vel = cm.getVocabularyEntityByURI(uriStr, serviceContext);
			assertNotNull(vel);
			printFewLines(vel, 500);
			serviceContext.setOutputFormat(ServiceResultFormat.XML);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		serviceContext.setOutputFormat(ServiceResultFormat.XML);
		fail();
	}
	
	@Test
	public void testGetValueSetMembersAsXML()
	{
		System.out.println("TESTING... testGetValueSetMembersAsXML");
		
		try
		{
			VocabularyId vs = new VocabularyId();
			vs.name = "SNOMEDCT-MAS";
			vs.version = null;
			String vel = cm.getValueSetInformation(vs, serviceContext);
			assertNotNull(vel);
			
			System.out.println("Getting members for URL:" + vs.name);
			String velmem = cm.getValueSetMembers(vs.name, serviceContext);
			assertNotNull(velmem);
			printFewLines(velmem, 500);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		fail();
	}
	
	@Test
	public void testGetValueSetMembersAsCSV()
	{
		System.out.println("TESTING... testGetValueSetMembersAsCSV");
		
		try
		{
			VocabularyId vs = new VocabularyId();
			vs.name = "SNOMEDCT-MAS";
			vs.version = null;
			String vel = cm.getValueSetInformation(vs, serviceContext);
			assertNotNull(vel);
			
			System.out.println("Getting members for URL:" + vs.name);
			serviceContext.setOutputFormat(ServiceResultFormat.CSV);
			String velmem = cm.getValueSetMembers(vs.name, serviceContext);
			assertNotNull(velmem);
			printFewLines(velmem, 50);
			serviceContext.setOutputFormat(ServiceResultFormat.XML);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		serviceContext.setOutputFormat(ServiceResultFormat.XML);
		fail();
	}
	
	@Test
	public void testGetValueSetMembersAsSVS()
	{
		System.out.println("TESTING... testGetValueSetMembersAsSVS");
		
		try
		{
			VocabularyId vs = new VocabularyId();
			vs.name = "SNOMEDCT-MAS";
			vs.version = null;
			serviceContext.setOutputFormat(ServiceResultFormat.SVS);
			String vel = cm.getValueSetInformation(vs, serviceContext);
			assertNotNull(vel);
			
			System.out.println("Getting members for URL in SVS:" + vs.name);
			String velmem = cm.getValueSetMembers(vs.name, serviceContext);
			assertNotNull(velmem);
			printFewLines(velmem, 50);
			serviceContext.setOutputFormat(ServiceResultFormat.XML);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		serviceContext.setOutputFormat(ServiceResultFormat.XML);
		fail();
	}
	*/
}
