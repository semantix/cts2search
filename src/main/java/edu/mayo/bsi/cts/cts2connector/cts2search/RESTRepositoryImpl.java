package edu.mayo.bsi.cts.cts2connector.cts2search;

import java.net.URI;
import java.util.Hashtable;
import java.util.Vector;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchableEntity;

public abstract class RESTRepositoryImpl extends RESTRepository 
{
	protected Hashtable<URI, SearchableEntity> searchURLs = new Hashtable<URI, SearchableEntity>();
	protected Hashtable<URI, Object> mapURLs = new Hashtable<URI, Object>();

	public RESTRepositoryImpl(SearchQuery query)
	{
		super(query);
	}

	protected void RefreshSearchURIs(String phrase) throws SearchException
	{
		searchURLs.clear();
		
		CTS2Config sc = this.query_.getSearchContext();
		
		if ((sc == null)||(!(sc instanceof CTS2Config)))
			throw new SearchException("CTS2 Context missing !!");
		
		CTS2Config rsc = (CTS2Config) sc;
		
		Vector<SearchableEntity> ents = this.query_.getRequestedSearchEntityTypes();

		if ((ents != null)&&(!ents.isEmpty()))
		{
			for(SearchableEntity sety : ents)
			{
				URI u = getSearchURI(phrase, sety, rsc);
				
				if (u == null)
					continue;
				
				if (!searchURLs.contains(u))
					searchURLs.put(u, sety);
			}
		}
	}

	protected void RefreshMapURIs() throws SearchException
	{
		mapURLs.clear();
		
		CTS2Config sc = this.query_.getSearchContext();
		
		if ((sc == null)||(!(sc instanceof CTS2Config)))
			throw new SearchException("CTS2 Context missing !!");
	}
	
	public SearchableEntity getSearchableTypeFromURI(URI uri)
	{
		if (searchURLs.containsKey(uri))
			return searchURLs.get(uri);
		
		return null;
	}
	
	public abstract URI getSearchURI(String phrase, SearchableEntity currentSearchableEntityType, CTS2Config rsc);
}
