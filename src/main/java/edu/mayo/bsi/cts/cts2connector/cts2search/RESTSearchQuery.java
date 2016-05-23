package edu.mayo.bsi.cts.cts2connector.cts2search;

import java.net.URI;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.CTS2Logger;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.RESTClient;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.RESTRequestMethod;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchableEntity;

public abstract class RESTSearchQuery extends SearchQuery 
{
	public URI currentSearchURI = null;
	
	public String userName = null;
	public String password = null;
	
	public RESTSearchQuery(CTS2Config context, CTS2Logger myLogger) 
	{
		super(context, myLogger);
		Vector<SearchableEntity> searchables = new Vector<SearchableEntity>();
		super.searchableEntities = searchables;
	}
	
	public SearchRepository getSearchRepository() 
	{
		return this.repository;
	}

	public Object parse(Object obj) throws SearchException
	{
		SearchRepository repo = this.getSearchRepository();
		return repo.parse(obj);
	}

	public Object postProcess(Object result) 
	{
		return result;
	}

	public void initializeRequestedSearchTypes(Vector<SearchableEntity> requestedSearchTypes, SearchableEntity defaultSearchType)
	{
		try
		{
			resetRequestedSearchEntityType();
			
			if ((requestedSearchTypes != null)&&(!requestedSearchTypes.isEmpty()))
			{
				for (SearchableEntity se : requestedSearchTypes)
					addRequestedSearchEntityType(se);
			}
			
			if (getRequestedSearchEntityTypes().isEmpty())
				addRequestedSearchEntityType(defaultSearchType);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public Object search() throws SearchException 
	{
		return search(false, false);
	}
	
	public Object search(boolean isSecureConnection, boolean useCredentials) throws SearchException 
	{
		SearchRepository repo = this.getSearchRepository();
		
		if (repo == null)
			return null;
		
		List<URI> searchURIs = repo.getSearchURIs();
		
		Object returnObject = null;
		
		for (URI uri : searchURIs)
		{
			try
			{
				this.logger.log(Level.INFO, uri.toString());
				currentSearchURI = uri;
				
				String contentType = null;
				
				if ((this.getSearchContext() == null)||(!(this.getSearchContext() instanceof CTS2Config)))
					continue;

				StringBuffer temp = RESTClient.request(true, RESTRequestMethod.GET, uri.toURL(), isSecureConnection, useCredentials, this.userName, this.password, null, contentType);
				
				returnObject = postProcess(this.parse(temp));
			}
			catch(Exception ie)
			{
				ie.printStackTrace();
				this.logger.log(Level.SEVERE, "Failed to get results for URI=" + uri);
			}
		}
		
		return returnObject;
	}
	
	@Override
	public Object getCurrentSearchRequest() 
	{
		return this.currentSearchURI;
	}

	public Object get(URI uri) throws SearchException 
	{
		return get(uri, false, false);
	}
	
	public Object get(URI uri, boolean isSecureConnection, boolean useCredentials) throws SearchException 
	{
		CTS2Repository repo = (CTS2Repository) this.getSearchRepository();
		
		if (repo == null)
			return null;

		try
		{
			this.logger.log(Level.INFO, uri.toString());
			
			StringBuffer temp = RESTClient.request(true, RESTRequestMethod.GET, uri.toURL(), isSecureConnection, useCredentials, repo.userName, repo.password, null, null);
			
			return temp;
		}
		catch(Exception ie)
		{
			throw new SearchException("Failed to get results for URI=" + uri + "\n" + ie.getLocalizedMessage());
		}
	}
}
