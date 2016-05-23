package edu.mayo.bsi.cts.cts2connector.cts2search;

import java.net.URI;
import java.util.Hashtable;
import java.util.List;

public abstract class SearchRepository 
{
	protected SearchQuery query_;
	
	public SearchRepository(SearchQuery query)
	{
		this.query_ = query;
	}
	
	public abstract List<URI> getSearchURIs();
	public abstract Hashtable<URI, Object> getMapTargets();
	public abstract Object parse(Object data);
}
