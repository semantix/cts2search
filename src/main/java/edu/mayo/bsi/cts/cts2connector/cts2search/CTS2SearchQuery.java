package edu.mayo.bsi.cts.cts2connector.cts2search;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.CTS2Logger;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.CTS2Utils;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchableEntity;

public class CTS2SearchQuery extends RESTSearchQuery 
{
	private SearchableEntity defaultSearchType = SearchableEntity.VALUE_SET;
	
	public CTS2SearchQuery(CTS2Config context, CTS2Logger logger) throws SearchException
	{
		super(context, logger);
		
		// Set what could be searched here
		addSearcheableEntity(SearchableEntity.CONCEPT);
		addSearcheableEntity(SearchableEntity.CODE_SYSTEM);
		addSearcheableEntity(SearchableEntity.CODE_SYSTEM_DEFINITION);
		addSearcheableEntity(SearchableEntity.VALUE_SET);
		addSearcheableEntity(SearchableEntity.VALUE_SET_INFORMATION);
		addSearcheableEntity(SearchableEntity.VALUE_SET_MEMBER);
		
		initializeRequestedSearchTypes(context.searchingFor, defaultSearchType);
	}

	public SearchRepository getSearchRepository() 
	{
		if (this.repository == null)
			this.repository = new CTS2Repository(this);
		
		adjustForContext();
		
		return this.repository;
	}

	public void adjustForContext()
	{
		if (this.repository == null)
			return;
		
		if (this.searchContext == null)
			return;
		
		if (!(this.searchContext instanceof RESTContext))
			return;
		
		CTS2Repository repo = (CTS2Repository) this.repository;
		RESTContext ctx = (RESTContext) this.searchContext;
		
		if (!CTS2Utils.isNull(ctx.hostName))
			repo.hostName = ctx.hostName;

		if (!CTS2Utils.isNull(ctx.port))
			repo.port = ctx.port;

		if (!CTS2Utils.isNull(ctx.userName))
			repo.userName = ctx.userName;

		if (!CTS2Utils.isNull(ctx.password))
			repo.password = ctx.password;

		if (!CTS2Utils.isNull(ctx.serviceUrlSuffix))
			repo.serviceURLSuffix = ctx.serviceUrlSuffix;

		if (!CTS2Utils.isNull(ctx.baseURL))
			repo.baseURL = ctx.baseURL;
	}

	public Object search() throws SearchException 
	{
		CTS2Repository repo = (CTS2Repository) this.getSearchRepository();
		
		if (repo == null)
			return null;
		
		super.userName = repo.userName;
		super.password = repo.password;

		RESTContext ctx = (RESTContext) this.searchContext;
		
		String useCredentials = ctx.getUserParameterValue(CTS2Config.REQUIRES_CREDENTIALS);
		
		if (useCredentials != null)
			useCredentials = useCredentials.trim();
		
		boolean isSecureConnection = ctx.secure;
		
		return super.search(isSecureConnection, "true".equalsIgnoreCase(useCredentials));
	}

	@Override
	public Object get(Object resourceInfo) throws SearchException 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
