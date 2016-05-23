package edu.mayo.bsi.cts.cts2connector.cts2search;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.*;

import java.net.URI;
import java.util.Properties;
import java.util.Vector;

public class RESTContext extends CTS2Config 
{
	public RESTContext(CTS2Logger logger) 
	{
		super(logger);
	}

	public static String REST_DEFAULT_CODESYSTEM = "codeSystem";
	public static String REST_DEFAULT_CODESYSTEM_VERSION = "codeSystemVersion";
	
	public VocabularyId defaultVocabulary = null;
	
	public Vector<VocabularyId> vocabularyIDs = new Vector<VocabularyId>();
	
	public String requestedValueSetName = null;
	public String requestedValueSetVersion = null;
	public URI requestedValueSetURI = null;

	public boolean loadContext(String profile, Properties props) throws SearchException
	{
		if (super.loadProperties(profile, props))
		{
			try
			{
				String value = null;
				value = props.getProperty(getPropertyName(RESTContext.REST_DEFAULT_CODESYSTEM));
				if (!CTS2Utils.isNull(value))
				{
					VocabularyId csv = new VocabularyId();
					csv.name = value;
					
					value = props.getProperty(getPropertyName(RESTContext.REST_DEFAULT_CODESYSTEM_VERSION));
					if (!CTS2Utils.isNull(value))
					{
						csv.version = value;
					}
					
					this.vocabularyIDs.add(csv);
					defaultVocabulary = csv;
				}
			}
			catch(Exception e)
			{
				throw new SearchException(e);
			}
		}
		
		return true;
	}
	
	public VocabularyId getDefaultExternalVocabulary()
	{
		return defaultVocabulary;
	}
	
	public void setUserParameter(CTS2RestRequestParameters paramName, String paramValue)
	{
		if (paramName != null)
			super.setUserParameter(paramName.toString(), paramValue);
	}
	
	public void removeUserParameter(CTS2RestRequestParameters paramName)
	{
		super.removeUserParameter(paramName.toString());
	}

	public String getUserParameterValue(CTS2RestRequestParameters paramName)
	{
		if (paramName != null)
			return super.getUserParameterValue(paramName.toString());
		
		return null;
	}
	
	public boolean addSearchEntity(SearchableEntity entity, boolean removeOldSearchEntities)
	{
		if (this.searchingFor == null)
			this.searchingFor = new Vector<SearchableEntity>();

		if (removeOldSearchEntities)
			this.searchingFor.clear();
		
		if (!this.searchingFor.contains(entity))
			this.searchingFor.add(entity);
		
		return true;
	}
	
	public void setOutputFormat(ServiceResultFormat format)
	{
		if (format != null)
			this.outputFormat = format;
		else
		{
			if (this.outputFormat == null)
				this.outputFormat = ServiceResultFormat.XML;
		}
	}
	
	public void addVocabularies(VocabularyId vocabulary, boolean removeOldVocabularies)
	{
		if (vocabulary == null)
			return;
		
		if (removeOldVocabularies)
			this.vocabularyIDs.clear();
		
		this.vocabularyIDs.add(vocabulary);
	}
	
	public boolean isSearchingFor(SearchableEntity entity)
	{
		return this.searchingFor.contains(entity);
	}
}
