package edu.mayo.bsi.cts.cts2connector.cts2search;

import java.util.Vector;
import java.util.logging.Level;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.CTS2Logger;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchableEntity;

public abstract class SearchQuery
{
	protected SearchRepository repository = null;
	public String searchPhrase;
	protected Vector<SearchableEntity> requestedEntityTypes = new Vector<SearchableEntity>();
	protected Vector<SearchableEntity> searchableEntities = null;
	
	protected CTS2Config searchContext = null;

	public CTS2Logger logger = null;
	
	public SearchQuery(CTS2Config context, CTS2Logger myLogger)
	{
		if (myLogger != null)
			this.logger = myLogger;
		else 
			this.logger = new CTS2Logger();
		
		this.searchContext = context;
	}
	
	public Vector<SearchableEntity> getSearchableEntities()
	{
		return searchableEntities;
	}
	
	public boolean searachableEntity(SearchableEntity entity)
	{
		if (this.searchableEntities == null)
			return false;
		
		return this.searchableEntities.contains(entity);
	}
	
	protected void addSearcheableEntity(SearchableEntity type) throws SearchException
	{
		if (type == null)
			return;
		
		if (this.searchableEntities == null)
			this.searchableEntities = new Vector<SearchableEntity>();
		
		if (!this.searchableEntities.contains(type))
			this.searchableEntities.add(type);
	}
	
	public void resetRequestedSearchEntityType()
	{
		this.requestedEntityTypes.clear();
	}
	
	public Vector<SearchableEntity> getRequestedSearchEntityTypes()
	{
		return this.requestedEntityTypes;
	}

	public void addRequestedSearchEntityType(SearchableEntity type) throws SearchException
	{
		if (!this.searachableEntity(type))
			return;
		
		if (this.requestedEntityTypes == null)
			this.requestedEntityTypes = new Vector<SearchableEntity>();
		
		if (!this.requestedEntityTypes.contains(type))
			this.requestedEntityTypes.add(type);
	}
	
	public CTS2Config getSearchContext()
	{
		return this.searchContext;
	}
	
	public void setSearchContext(CTS2Config context)
	{
		this.searchContext = context;
	}
	
	public void log(Object msg)
	{
		if (this.logger != null)
			this.logger.log(Level.INFO, msg.toString());
	}
	
	public abstract SearchRepository getSearchRepository();
	public abstract Object search() throws SearchException;
	public abstract Object get(Object resourceInfo) throws SearchException;
	public abstract Object postProcess(Object resultItem);
	public abstract Object getCurrentSearchRequest();
}
