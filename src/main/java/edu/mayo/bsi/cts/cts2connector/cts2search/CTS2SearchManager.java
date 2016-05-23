package edu.mayo.bsi.cts.cts2connector.cts2search;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.CTS2Logger;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException;


public class CTS2SearchManager 
{
	protected SearchQuery query = null;
	protected CTS2Logger logger = null;
	
	
	public CTS2SearchManager(CTS2Logger slogger) throws SearchException
	{
		this.logger = slogger;
	}

	private void validate() throws SearchException
	{
		if (query == null)
			throw new SearchException("Search Query is null!");

		if (!(query instanceof CTS2SearchQuery))
			throw new SearchException("Search Implementation not available for " + query.getClass().getCanonicalName());
	}
	
	public Object findMatchingEntities(String phrase, CTS2Config ctx) throws SearchException
	{
		if (ctx == null)
			throw new SearchException("Search Context is null!");

		query = new CTS2SearchQuery(ctx, this.logger);
		
		validate();
		
		query.searchPhrase = phrase;

		return query.search();
	}
	
	public Object get(Object inputObject, CTS2Config ctx) throws SearchException
	{
		if (ctx == null)
			throw new SearchException("Search Context is null!");

		query = new CTS2SearchQuery(ctx, this.logger);
		
		validate();
		
		return query.get(inputObject);
	}

	public SearchQuery getQuery() 
	{
		return query;
	}

	public boolean setQuery(SearchQuery query) 
	{
		this.query = query;
		return true;
	}
}
