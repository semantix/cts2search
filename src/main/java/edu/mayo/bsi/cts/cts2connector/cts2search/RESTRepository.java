package edu.mayo.bsi.cts.cts2connector.cts2search;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.CTS2Utils;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.RESTClient;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.RESTRequestMethod;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException;

import java.io.InputStream;
import java.net.URL;


public abstract class RESTRepository extends SearchRepository
{
	public static final String defaultHost = "localhost";
	public static final String defaultPort = "8080";
	public static final String urlSeparator = "/";
	public static final String portSeparator = ":";

	public String hostName = null;
	public String port = null;
	//public boolean secure = false;
	public String baseURL = null;
	public String serviceURLSuffix = null;
	
	public String userName = null;
	public String password = null;
	
	public Object inputObject = null;
	
	protected String getProtocol()
	{
		// ((this.query_.getSearchContext().secure)?"https://":"http://");
		
		return "http://";
	}
	
	public String makeUrlPrefix()
	{
		String hn = (CTS2Utils.isNull(hostName))?defaultHost:hostName;
		String pt = (!CTS2Utils.isNull(port))?(portSeparator + port):"";
		return getProtocol() + hn + pt;
	}
	
	public RESTRepository(SearchQuery query)
	{
		super(query);
	}
	
	
	public Object invoke(boolean quiet, RESTRequestMethod method, URL url, boolean isSecureConnection, boolean useCredentials, InputStream postContent, String contentType) throws SearchException
	{
		try
		{
			if (useCredentials)
				return RESTClient.request(quiet, method, url, isSecureConnection, true, this.userName, this.password, postContent, contentType);
			else
				return RESTClient.request(quiet, method, url, isSecureConnection, false, null, null, postContent, contentType);
		}
		catch(Exception fe)
		{
			throw new SearchException(fe);
		}
	}
}
