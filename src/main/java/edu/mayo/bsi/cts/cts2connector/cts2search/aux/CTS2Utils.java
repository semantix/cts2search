package edu.mayo.bsi.cts.cts2connector.cts2search.aux;

import java.net.URLEncoder;

public class CTS2Utils 
{
	public static final String SELECTED_TAG = "_SELECTED";
	public static boolean isNull(String str)
	{
		if ((str == null)||("null".equalsIgnoreCase(str.trim()))||("".equalsIgnoreCase(str.trim())))
			return true;
		return false;
	}
	
	public static String encode(String urlStr)
	{
		String encoded = CTS2Utils.encode(urlStr, null);
		
		//if (encoded != null)
			//encoded = encoded.replaceAll("+", "%20");
		
		return encoded;
	}
	
	public static String encode(String urlStr, String encoding)
	{
		try
		{
			if (encoding == null)
				return URLEncoder.encode(urlStr, "UTF-8");
			
			return URLEncoder.encode(urlStr, encoding);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return urlStr;
	}
	
	public static String getSelected(String serviceName)
	{
		if (serviceName == null)
			return null;
		
		if (serviceName.endsWith(SELECTED_TAG))
				return serviceName.split(SELECTED_TAG)[0];
		
		return null;
	}
	
	public static boolean isCTS2RequestParameter(String value)
	{
		try
		{
			CTS2RestRequestParameters cp =  CTS2RestRequestParameters.valueOf(value);
			return (cp != null);
		}
		catch(Exception e)
		{
		}

		return false;
	}
}
