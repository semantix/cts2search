package edu.mayo.bsi.cts.cts2connector.cts2search;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class CTS2Repository extends RESTRepositoryImpl 
{
	//private String codeSystemName = "SNOMEDCT";
	//private String codeSystemVersion = "SNOMEDCT_2011_01_31_UMLS-RELA";
	
	private String restPrefixForSearch = "/cts2/rest/";
	
	public CTS2Repository(SearchQuery query)
	{
		super(query);
	}

	public List<URI> getSearchURIs() 
	{
		if (this.query_ == null)
			return null;
		
		if (!(this.query_ instanceof CTS2SearchQuery))
			return null;
		
		CTS2SearchQuery osq = (CTS2SearchQuery) this.query_;
		try
		{
			RefreshSearchURIs(osq.searchPhrase);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		List<URI> allURIs = new ArrayList<URI>();
		for(URI uri : searchURLs.keySet())
			allURIs.add(uri);
		
		return allURIs;

	}

	protected void RefreshSearchURIs(String phrase) throws SearchException
	{
		searchURLs.clear();
		
		CTS2Config sc = this.query_.getSearchContext();
		
		if ((sc == null)||(!(sc instanceof RESTContext)))
			throw new SearchException("Vocabulary REST Context missing !!");
		
		RESTContext vrsc = (RESTContext) sc;
		
		Vector<SearchableEntity> ents = this.query_.getRequestedSearchEntityTypes();

		if ((ents != null)&&(!ents.isEmpty()))
		{
			for(SearchableEntity sety : ents)
			{
				if ((vrsc.vocabularyIDs != null)&&
						(!vrsc.vocabularyIDs.isEmpty())&&
						(vrsc.vocabularyIDs.size() > 1))
				{
					for (int codeSystemOrValueSetIndex = 0; 
							codeSystemOrValueSetIndex < vrsc.vocabularyIDs.size(); 
							codeSystemOrValueSetIndex++)
					{
						URI u = getSearchURI(phrase, sety, vrsc, codeSystemOrValueSetIndex);
					
						if (u == null)
							continue;
					
						if (!searchURLs.contains(u))
							searchURLs.put(u, sety);
					}
				}
				else
				{
					URI u = getSearchURI(phrase, sety, vrsc);
				
					if (u == null)
						continue;
					
					if (!searchURLs.contains(u))
						searchURLs.put(u, sety);
				}
			}
		}
	}
	
	public URI getSearchURI(String phrase, SearchableEntity currentSearchableEntityType, CTS2Config rsc)
	{
		return getSearchURI(phrase, currentSearchableEntityType, rsc, 0);
	}
	
	public URI getSearchURI(String phrase, SearchableEntity currentSearchableEntityType, CTS2Config rsc, int csvIndex)
	{
		// In case code system and version strings are needed to narrow down on coding scheme
 		String csVersionString = "";
		
		if (!(rsc instanceof RESTContext))
			return null;
		
		RESTContext vrsc = (RESTContext) rsc;
		
		String vocabName = null;
		String vocabVersion = null;
			
		if ((vrsc.vocabularyIDs != null)&&
			(!vrsc.vocabularyIDs.isEmpty())&&
			(vrsc.vocabularyIDs.size() > csvIndex))
		{
			VocabularyId csv = vrsc.vocabularyIDs.elementAt(csvIndex);
			
			vocabName = csv.name;
			vocabVersion = csv.version;
			
			if ((!CTS2Utils.isNull(vocabName))&&(!CTS2Utils.isNull(vocabVersion)))
				csVersionString = "codesystem/" + CTS2Utils.encode(vocabName) + "/version/" + CTS2Utils.encode(vocabVersion);
		}

		// Create arrays to store parameters to be appended to the URL later with thier values.
		String[] urlParams = new String[100];
		String[] urlValues = new String[100];
		
		for (int y=0; y < urlParams.length;y++)
		{
			urlParams[y] = null;
			urlValues[y] = null;
		}		
		
		// Start with first Index
		int paramIndex = 0;
		
		if (vrsc.startIndex > 0)
		{
			urlParams[paramIndex] = "page";
			urlValues[paramIndex] = "" + vrsc.startIndex;
			paramIndex++;
		}
		
		if ((currentSearchableEntityType == SearchableEntity.CODE_SYSTEM)||
				(currentSearchableEntityType == SearchableEntity.CONCEPT))
		{
			urlParams[paramIndex] = "matchvalue";
			if (!CTS2Utils.isNull(phrase))
				urlValues[paramIndex] = phrase;
			else
				urlValues[paramIndex] = "";
			
			paramIndex++;
		}
		
		// if format is JSON then add otherwise to default xml format
		String urlStr = makeUrlPrefix() + this.serviceURLSuffix;
		
		if (this.query_.getSearchContext().outputFormat == ServiceResultFormat.JSON)
		{
			urlParams[paramIndex] = "format";
			urlValues[paramIndex] = "json";
			paramIndex++;
		}
		
		boolean useTempAsURL = false;
		String temp = "";

		switch(currentSearchableEntityType)
		{
			case CODE_SYSTEM:	
				temp = "codesystems";		
				break;
			
			case CODE_SYSTEM_DEFINITION:
				
				if (!CTS2Utils.isNull(csVersionString))
					temp = csVersionString;
				else
					temp = "codesystem/" + CTS2Utils.encode(phrase);
				break;
				
			case VALUE_SET:
				
				if ((!CTS2Utils.isNull(phrase))&&
					(!CTS2Utils.isNull(vocabName)))
				{
						temp = "valueset/" + CTS2Utils.encode(phrase); 
				}
				else
				{
					temp = "valuesets";
					
					urlParams[paramIndex] = "matchvalue";
					if (!CTS2Utils.isNull(phrase))
						urlValues[paramIndex] = phrase;
					else
						urlValues[paramIndex] = "";
					
					paramIndex++;

					if ((rsc.getUserParameterList() != null)&&(rsc.getUserParameterList().size() > 0))
					{
						/*
						urlParams[paramIndex] = "filtercomponent1";
						urlValues[paramIndex] = "resourceSynopsis";
						paramIndex++;

						urlParams[paramIndex] = "matchvalue1";
						if (!CTS2Utils.isNull(phrase))
							urlValues[paramIndex] = phrase;
						else
							urlValues[paramIndex] = "";
						paramIndex++;
						 */
						
						int ct = 1;
						String algoSuffix = "matchalgorithm";
						for (String param : rsc.getUserParameterList())
						{
							if ((!CTS2Utils.isNull(param))&&(CTS2Utils.isCTS2RequestParameter(param))&&(!param.endsWith(algoSuffix)))
							{
								String pval = rsc.getUserParameterValue(param);
								
								String matchAlgorithm = rsc.getUserParameterValue(param + algoSuffix);							
								if (CTS2Utils.isNull(matchAlgorithm))
									matchAlgorithm = "exactMatch";
									
								//temp += "&filtercomponent"+ ct + "=" + param + "&matchvalue" + ct + "=" + ((CTS2Utils.isNull(pval))?"":CTS2Utils.encode(pval)) +"&matchalgorithm"+ ct + "=" + matchAlgorithm;
								
								urlParams[paramIndex] = "filtercomponent" + ct;
								urlValues[paramIndex] = param;
								paramIndex++;

								urlParams[paramIndex] = "matchvalue" + ct;
								if (!CTS2Utils.isNull(pval))
									urlValues[paramIndex] = pval;
								else
									urlValues[paramIndex] = "";
								paramIndex++;

								urlParams[paramIndex] = "matchalgorithm" + ct;
								urlValues[paramIndex] = matchAlgorithm;
								paramIndex++;

								ct++;
							}
						}
					}
				}
				
				if (!CTS2Utils.isNull(vocabName))
				{
					urlParams[paramIndex] = "codesystem";
					urlValues[paramIndex] = vocabName;
					paramIndex++;
				}

				break;
				
			case VALUE_SET_INFORMATION:
				temp = "valueset/" + CTS2Utils.encode(phrase);
				break;

			case VALUE_SET_MEMBER:
				if (phrase == null)
				{
					temp = vrsc.requestedValueSetURI.toString();
					useTempAsURL = true;
				}
				else
				{
					//TODO:  There is another context variable 'pageSize', which is not yet used.
					// right now we use max to return to show or download value set members for 
					// a selected valueSet. 
					
					temp = "valueset/" + CTS2Utils.encode(phrase) + "/resolution";
				}
				
				break;

			case CONCEPT:
			default:
				if ((phrase == null)&&(vrsc.requestedValueSetURI != null))
				{
					temp = vrsc.requestedValueSetURI.toString();
					useTempAsURL = true;
				}
				else
				{
					if (!CTS2Utils.isNull(csVersionString))
						temp = csVersionString + "/";
					temp += "entities";
					
					urlParams[paramIndex] = "filtercomponent";
					urlValues[paramIndex] = "resourceSynopsis";
					paramIndex++;
				}
		}
		
		// Set Result Limits
		if ((currentSearchableEntityType != SearchableEntity.CODE_SYSTEM_DEFINITION)&&
				(currentSearchableEntityType != SearchableEntity.VALUE_SET_INFORMATION))
		{
			int maxResult = rsc.resultLimit;
			if (rsc.resultLimit < 1)
				maxResult = 1000;
			
			urlParams[paramIndex] = "maxtoreturn";
			urlValues[paramIndex] = "" + maxResult;
			paramIndex++;
		}

		MatchAlgorithm algo = this.query_.searchContext.matchAlgorithm_;
		if ((algo != null)&&(algo != MatchAlgorithm.EXACT))
		{
			urlParams[paramIndex] = "matchalgorithm";
			urlValues[paramIndex] = this.query_.searchContext.matchAlgorithm_.toString();
			paramIndex++;
		}

		String transformPrefix = this.query_.searchContext.transforms.get(this.query_.getSearchContext().outputFormat.toString().toLowerCase());
		
		if (useTempAsURL)
			urlStr = temp;
		else
		{
			urlStr +=  temp;

			// TODO: DEEPAK: Move this to phy4CTS2 sepcific calls
			//if (!CTS2Utils.isNull(transformPrefix))
			//{
				urlParams[paramIndex] = "bypass";
				urlValues[paramIndex] = "1";
				paramIndex++;
			//}
			
			String paramStr = "";
			String encodedParamStr = "";
			String marker = "?";
			for (int p=0; p < urlParams.length; p++)
			{
				if (urlParams[p] == null)
					break;
				
				
				if (p > 0)
					marker = "&";
				
				paramStr += marker + urlParams[p] + "=" + CTS2Utils.encode(((urlValues[p] == null)?"":urlValues[p]));
				encodedParamStr += CTS2Utils.encode(marker + urlParams[p] + "=" + ((urlValues[p] == null)?"":urlValues[p]));
			}
			
			if (!CTS2Utils.isNull(transformPrefix))
				urlStr = transformPrefix + urlStr + encodedParamStr;
			else
				urlStr += paramStr;
		}

		if (!CTS2Utils.isNull(urlStr))
		{
			try 
			{
				return URI.create(urlStr);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();			
			}
		}
		
		return null;
	}

	public Object parse(Object sb)
	{
		try 
        {
			if ((sb instanceof StringBuffer)||(sb instanceof String))
				return sb;
			else
				return "CTS 2 Result Parser only recognizes XML String object as of now...";
		} 
        catch (Exception e) 
        {
			e.printStackTrace();
		}
		
        return sb;
	}
	
	@Override
	public Hashtable<URI, Object> getMapTargets() {
		// TODO Auto-generated method stub
		return null;
	}
}
