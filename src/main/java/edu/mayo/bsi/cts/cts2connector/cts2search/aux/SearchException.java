package edu.mayo.bsi.cts.cts2connector.cts2search.aux;

public class SearchException extends Exception 
{
	public SearchException(Exception e)
	{
		super("Failed in Search API:" + e.getMessage(), e);
	}

	public SearchException(String message) 
	{
		super(message);
	}
}
