package edu.mayo.bsi.cts.cts2connector.cts2search.aux;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;

public class RESTClient
{
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String POST = "POST";
    
    private static CTS2Logger logger = new CTS2Logger();
    
    private RESTClient()
    {
        super();
    }
    
    private static String getMethod(RESTRequestMethod method)
    {
    	switch(method)
    	{
    		case DELETE: return DELETE;
    		case POST: return POST;
    		case PUT: return PUT;
    		default: return GET;
    	}
    }
 
    public static StringBuffer request(boolean quiet, 
			RESTRequestMethod method, 
			URL url,
			boolean isSecureConnection,
			boolean toAuthenticate,
			String username, 
			String password,
			InputStream body, 
			String contentType)
					throws Exception
	{
    	if (url == null)
    		return null;
    	
    	URLConnection connection = null;

    	if (!quiet)
        {
            logger.log(Level.INFO, "[issuing request: " + method + " " + url + "]");
        }

    	//boolean secureReqeust = ((isSecure)&&(url.toString().toLowerCase().startsWith("https")));
    	
    	StringBuffer responseBody = null;
    		
    	if (toAuthenticate)
    	{
    		if (isSecureConnection)
    			connection = RESTClient.secureRequestConnection(quiet, method, url);
    		else
    			connection = RESTClient.unSecureRequestConnection(quiet, method, url);
    			
    		responseBody = RESTClient.processRequest(quiet, connection, username, password, body, contentType);
    	}
    	else
    	{
    		if (isSecureConnection)
    			connection = RESTClient.secureRequestConnection(quiet, method, url);
    		else
    			connection = RESTClient.unSecureRequestConnection(quiet, method, url);

    		responseBody = RESTClient.processRequest(quiet, connection, null, null, body, contentType);
    		
    		RESTClient.disconnectConnection(connection);
    	}
    	
    	RESTClient.disconnectConnection(connection);
    	
		return responseBody;
	}
    
    public static void disconnectConnection(URLConnection connection)
    {
    	try
    	{
    		if (connection == null)
    			return;
    		
    		((HttpsURLConnection)connection).disconnect();
    	}
    	catch(Exception e)
    	{
    		((HttpURLConnection)connection).disconnect();
    	}
    }
    
    private static URLConnection unSecureRequestConnection(boolean quiet, 
								RESTRequestMethod method, 
    							URL url)
    throws IOException
    {
       URLConnection connection = null;
    	try
    	{
    		if (url == null)
    			return null;
    		
    		connection = (HttpsURLConnection)url.openConnection();
    		((HttpsURLConnection) connection).setRequestMethod(getMethod(method));
    	}
    	catch(Exception e)
    	{
    		connection = (HttpURLConnection)url.openConnection();
    		((HttpURLConnection)connection).setRequestMethod(getMethod(method));
    	}
    	
		return connection;
    }
    
    private static URLConnection secureRequestConnection(boolean quiet, 
			RESTRequestMethod method, 
			URL url)
	throws IOException
	{
		if (!quiet)
		{
			logger.log(Level.INFO, "[issuing request: " + method + " " + url + "]");
		}
		
		URLConnection connection = unSecureRequestConnection(quiet, method, url);
		
		try
		{
			if ((connection != null)&&(connection instanceof HttpsURLConnection))
			{
			    // Create a trust manager that does not validate certificate chains
			    final TrustManager[] trustAllCerts = new TrustManager[] { 
			    				new X509TrustManager() 
			    				{
			    					public java.security.cert.X509Certificate[] getAcceptedIssuers() {return null;}
			    					public void checkClientTrusted(
										java.security.cert.X509Certificate[] arg0,
										String arg1) throws CertificateException {}
			    					public void checkServerTrusted(
										java.security.cert.X509Certificate[] arg0,
										String arg1) throws CertificateException {}
			    				} }; 
			    
			    // Install the all-trusting trust manager
			    final SSLContext sslContext = SSLContext.getInstance( "SSL" );
			    sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
			    // Create an ssl socket factory with our all-trusting manager
			    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			    ((HttpsURLConnection) connection).setSSLSocketFactory( sslSocketFactory);
			}
		}
		catch(Exception trustException)
		{
			trustException.printStackTrace();
		}
		
		return connection;
	}
    
    private static StringBuffer processRequest(boolean quiet, URLConnection connection, String username, String password, InputStream body, String contentType) throws Exception
    {
		if ((username != null)&&(!("null".equalsIgnoreCase(username))))
		{
			// write auth header
			//Base64 encoder = new Base64();
			byte[] encodedCredential = null;
			try 
			{
				if ("".equals(password)||(password == null))
					encodedCredential = Base64.encodeBase64("username:".getBytes());
				else
					encodedCredential = Base64.encodeBase64((username + ":" + password).getBytes());
				
			} 
			catch (Exception e) 
			{
				throw e;
			}
			
			connection.setRequestProperty("Authorization", "Basic " + new String(encodedCredential));
		}
		
		/*
		if (contentType == null)
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		else
			connection.setRequestProperty("Content-Type", contentType);
		*/
		
		// write body if we're doing POST or PUT
		byte buffer[] = new byte[8192];
		int read = 0;
		if (body != null)
		{
			connection.setDoOutput(true);
			
			OutputStream output = connection.getOutputStream();
			while ((read = body.read(buffer)) != -1)
			{
				output.write(buffer, 0, read);
			}
		}
		
		// do request
		long time = System.currentTimeMillis();
		
		connection.connect();
		
		logger.log(Level.WARNING, "connection url=" + connection.getURL());
		System.out.println("####################connection url=" + connection.getURL());
		
		InputStream responseBodyStream = connection.getInputStream();
		StringBuffer responseBody = new StringBuffer();
		while ((read = responseBodyStream.read(buffer)) != -1)
		{
			responseBody.append(new String(buffer, 0, read));
		}
		
		time = System.currentTimeMillis() - time;
		
		// start printing output
		if (!quiet)
			logger.log(Level.INFO, "[read " + responseBody.length() + " chars in " + time + "ms]");
		
		// look at headers
		// the 0th header has a null key, and the value is the response line ("HTTP/1.1 200 OK" or whatever)
		if (!quiet)
		{
			String header = null;
			String headerValue = null;
			int index = 0;
			while ((headerValue = connection.getHeaderField(index)) != null)
			{
				header = connection.getHeaderFieldKey(index);
				
				if (header == null)
				logger.log(Level.INFO, headerValue);
				else
				logger.log(Level.INFO, header + ": " + headerValue);
				
				index++;
			}
			logger.log(Level.INFO, "");
		}
		
		// dump body
		logger.log(Level.INFO, responseBody.toString());
		
		return responseBody;
	}
}