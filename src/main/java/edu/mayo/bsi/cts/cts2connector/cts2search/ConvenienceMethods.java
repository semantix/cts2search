package edu.mayo.bsi.cts.cts2connector.cts2search;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

public class ConvenienceMethods 
{
	private String PROFILES_PROPERTY = "profiles";
	private String DEFAULT_PROFILE_ROPERTY = "defaultProfile";
	private static String propertyFileLocation_ = "CTS2Profiles.properties";
	
	private String defaultProfile_ = null;

	private CTS2Logger logger = new CTS2Logger();
	
	private static ConvenienceMethods instance;
	
	private Hashtable<String, RESTContext> profiles = new Hashtable<String, RESTContext>();
	
	private String contextErrorMsg = "REST Context is not defined or null!";

	public static synchronized ConvenienceMethods instance()
	{
		if(instance == null)
		{
			try
			{
				instance = new ConvenienceMethods(ConvenienceMethods.getPropertyFileLocation());
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
		}

		return instance;
	}

	public static synchronized ConvenienceMethods instance(String propertyFileLocation)
	{
		if(instance == null)
		{
			try 
			{
				instance = new ConvenienceMethods(propertyFileLocation);
			} 
			catch (Exception e) 
			{
				throw new IllegalStateException(e);
			}
		}
		
		return instance;
	}
	
	public static String getPropertyFileLocation()
	{
		return ConvenienceMethods.propertyFileLocation_;
	}

	public void setPropertyFileLocation(String propertyFileLocation) throws Exception
	{
		if (CTS2Utils.isNull(propertyFileLocation))
		{
			logger.log(Level.SEVERE, "No Property file found at the given location \"" + propertyFileLocation + "\" !! Using Default property file:" + this.propertyFileLocation_);
		}
		else
			this.propertyFileLocation_ = propertyFileLocation;
		
		this.profiles.clear();
		Properties props = null;
		try
		{
			if (this.propertyFileLocation_ != null)
			{
				String dir = System.getProperty("user.dir");
				InputStream inputStream = new FileInputStream(dir + "/resources/" + this.propertyFileLocation_);
				props = new Properties();
				props.load(inputStream);
			}
		}
		catch(Exception e)
		{
			String msg = "May be difficulties in reading property file:\"" + this.propertyFileLocation_ + "\" not found.\n" +
	                 "Should be in directory \"" + System.getProperty("user.dir") + "\"\n";
			msg += "Looking for " + this.getClass().getPackage().getName();
			this.logger.log(Level.WARNING, msg);
		}

		if (props == null)
			return;
		
		String profileNames = props.getProperty(PROFILES_PROPERTY);
		
		if (CTS2Utils.isNull(profileNames))
		{
			this.logger.log(Level.SEVERE, "No Profile found to load in property file!!");
			return;
		}
		
		String[] pNames = profileNames.split(",");
		
		for (String pName : pNames)
		{
			RESTContext rctx = new RESTContext(this.logger);
			rctx.loadContext(pName, props);
			this.profiles.put(pName, rctx);
		}
		
		defaultProfile_ = props.getProperty(DEFAULT_PROFILE_ROPERTY);
		
		if ((CTS2Utils.isNull(defaultProfile_))||(!(this.profiles.keySet().contains(defaultProfile_))))
		{
			Set<String> pkeys = this.profiles.keySet();
			if (pkeys.size() > 0)
				defaultProfile_ = pkeys.iterator().next();
		}
	}

	public String getDefaultProfileName()
	{
		return this.defaultProfile_;
	}
	
	public RESTContext getContext(String profile) throws SearchException
	{
		try
		{
			return this.profiles.get(profile);
		}
		catch(Exception e)
		{
			String message = "No context found to serve CTS2 Content! Make sure configuration file " +
					ConvenienceMethods.getPropertyFileLocation() + " is available.";
			throw new SearchException(message);
		}
	}
	
	public Set<String> getAvailableProfiles()
	{
		return this.profiles.keySet();
	}
	
	private ConvenienceMethods(String propertyFileLocation) throws Exception
	{
		setPropertyFileLocation(propertyFileLocation);
	}

	private void addEntityType(RESTContext context, SearchableEntity type, boolean removeOldSearchEntities) throws SearchException
	{
		if (context == null)
			throw (new SearchException(contextErrorMsg));

		context.addSearchEntity(type, removeOldSearchEntities);
	}
	
	public String getAvailableVocabularies(RESTContext context) throws SearchException
	{
		return this.getMatchingVocabularies(null, context);
	}
	
	public String getMatchingVocabularies(String matchPhrase, RESTContext context) throws SearchException 
	{
		addEntityType(context, SearchableEntity.CODE_SYSTEM, true);
		
		Object result = this.getMatchingVocabulariesElementByType(matchPhrase, context, false, false);
		return (String) ((result != null)?(result.toString()):result);
	}

	public String getVocabularyDefinition(VocabularyId vocabulary, RESTContext context) throws SearchException
	{
		addEntityType(context, SearchableEntity.CODE_SYSTEM_DEFINITION, true);
		
		Object result = this.getMatchingVocabulariesElementByType(vocabulary, context);
		return (String) ((result != null)?(result.toString()):result);
	}

	public String getAvailableValueSets(boolean filterByDefaultCodeSystem, boolean populateMembers, boolean enableRanking, RESTContext context) throws SearchException
	{
		return this.getMatchingValueSets(null, filterByDefaultCodeSystem, populateMembers, enableRanking, context);
	}

	public String getMatchingValueSets(String matchPhrase, boolean filterByDefaultCodeSystem, boolean populateMembers,  boolean enableRanking, RESTContext context) throws SearchException
	{
		addEntityType(context, SearchableEntity.VALUE_SET, true);

		Object result = this.getMatchingVocabulariesElementByType(matchPhrase, context, false, filterByDefaultCodeSystem);
		return (String) ((result != null)?(result.toString()):result);
	}

	public String getValueSetInformation(VocabularyId valueSet, RESTContext context) throws SearchException
	{
		if ((valueSet == null)||(valueSet.name == null))
			return null;
		
		addEntityType(context, SearchableEntity.VALUE_SET_INFORMATION, true);
		
		Object result = this.getMatchingVocabulariesElementByType(valueSet.name, context, false, false);
		return (String) ((result != null)?(result.toString()):result);
	}

	public String getValueSetMembers(String valueSetURIString, RESTContext context) throws SearchException
	{
		addEntityType(context, SearchableEntity.VALUE_SET_MEMBER, true);
		
		Object result = this.getMatchingVocabulariesElementByType(valueSetURIString, context, false, false);
		return (String) ((result != null)?(result.toString()):result);
	}
	
	public String getVocabularyEntityByURI(String uriString, RESTContext context) throws SearchException
	{
		addEntityType(context, SearchableEntity.CONCEPT, true);
		
		Object result = this.getMatchingVocabulariesElementByType(uriString, context, true, false);
		return (String) ((result != null)?(result.toString()):result);
	}

	public String getVocabularyEntities(String matchPhrase, VocabularyId vocabulary, RESTContext context) throws SearchException
	{
		addEntityType(context, SearchableEntity.CONCEPT, true);

		VocabularyId vocab = vocabulary;
		if ((vocab == null)||(vocab.name == null))
			vocab = context.getDefaultExternalVocabulary();

		context.addVocabularies(vocab, true);

		Object result = this.getMatchingVocabulariesElementByType(matchPhrase, context, false, true);
		return (String) ((result != null)?(result.toString()):result);
	}

	private Object getMatchingVocabulariesElementByType(VocabularyId vocabulary, RESTContext context) throws SearchException
	{
		try
		{
			boolean filterByVocabulary = true;

			VocabularyId vocab = vocabulary;
			if ((vocab == null)||(vocab.name == null))
				vocab = context.getDefaultExternalVocabulary();

			context.addVocabularies(vocab, true);

			return getMatchingVocabulariesElementByType(vocab.name, context, false, filterByVocabulary);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new SearchException(e.getMessage());
		}
	}

	private Object getMatchingVocabulariesElementByType(String matchPhrase,
														RESTContext context,
														 boolean usePhraseAsURI,
														 boolean filterByDefaultCodeSystem 
														 ) throws SearchException
	{
		try
		{
			if (!filterByDefaultCodeSystem)
				context.vocabularyIDs.clear();
			
			if ((context.isSearchingFor(SearchableEntity.VALUE_SET_MEMBER))||(usePhraseAsURI))
			{
				if (CTS2Utils.isNull(matchPhrase))
					throw new SearchException("URI String/phrase is null!!");
				
				if ((matchPhrase.startsWith("http"))||(usePhraseAsURI))
				{
					// In this case the phrase is actually the URI of value set.
					context.requestedValueSetURI = new URI(matchPhrase);
					matchPhrase = null;
				}
			}

			CTS2SearchManager search = new CTS2SearchManager(new CTS2Logger());
			Object results = search.findMatchingEntities(matchPhrase, context);
			
			return results;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new SearchException(e.getMessage());
		}
	}
}
