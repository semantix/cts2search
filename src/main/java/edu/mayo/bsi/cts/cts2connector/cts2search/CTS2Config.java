package edu.mayo.bsi.cts.cts2connector.cts2search;

import edu.mayo.bsi.cts.cts2connector.cts2search.aux.*;

import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

public class CTS2Config
{
	public static String SERVICE_ISSECURE = "isSecure";
	public static String SERVICE_HOST = "hostName";
	public static String SERVICE_PORT = "port";
	public static String SERVICE_SERVICEURLSUFFIX = "serviceUrlSuffix";
	public static String LOGIN_USER = "userName";
	public static String LOGIN_PSWD = "password";
	
	public static String SERVICE_DEFAULTCODESYSTEM = "codeSystem";
	public static String SERVICE_DEFAULTCODESYSTEM_VERSION = "codeSystemVersion";
	
	public static String SERVICE_TRANSFORM = "transform";

	public static String REQUIRES_CREDENTIALS = "requiresCredentials";
	public static String MUENABLED = "muenabled";
	public static String START_PAGE_INDEX = "startPageIndex";
	public static String RESULT_LIMIT = "resultLimit";
	public static String DOWNLOAD_PAGE_SIZE = "downloadPageSize";
	
	private Vector<String> definedPropertyKeys = new Vector<String>();
	
	public String hostName = null;
	public String port = null;
	public boolean secure = false;
	public String serviceUrlSuffix = null;
	public String userName = null;
	public String password = null;
	
	public boolean getAllentities = false;
	
	public ServiceResultFormat outputFormat = ServiceResultFormat.JSON;
	
	public VocabularyId defaultVocab = null;
	
	private static final String DEFAULT_PROFILE = null;
	
	public Hashtable<String, String> transforms = new Hashtable<String, String>();
	
	private String profileName_ = DEFAULT_PROFILE;
	
	public int startIndex = 0;
	public int resultLimit = 100;
	public int downloadPageSize = 1000;

	public MatchAlgorithm matchAlgorithm_ = MatchAlgorithm.EXACT;
	
	private CTS2Logger logger_ = null;
	
	public Vector<SearchableEntity> searchingFor = new Vector<SearchableEntity>();
	
	private Hashtable<String, String> params = new Hashtable<String, String>();
	
	public CTS2Config(CTS2Logger logger)
	{
		if (logger == null)
			logger_ = new CTS2Logger();
		else
			logger_ = logger;
	}
	
	public String getProfile()
	{
		return this.profileName_;
	}
	
	public void setProfile(String profileName)
	{
		if (!(CTS2Utils.isNull(profileName)))
		{
			this.profileName_ = profileName;
			
			definedPropertyKeys.clear();
			definedPropertyKeys.add(getPropertyName(CTS2Config.SERVICE_ISSECURE));
			definedPropertyKeys.add(getPropertyName(CTS2Config.SERVICE_HOST));
			definedPropertyKeys.add(getPropertyName(CTS2Config.SERVICE_PORT));
			definedPropertyKeys.add(getPropertyName(CTS2Config.SERVICE_SERVICEURLSUFFIX));
			definedPropertyKeys.add(getPropertyName(CTS2Config.LOGIN_USER));
			definedPropertyKeys.add(getPropertyName(CTS2Config.LOGIN_PSWD));
			definedPropertyKeys.add(getPropertyName(CTS2Config.SERVICE_DEFAULTCODESYSTEM));
			definedPropertyKeys.add(getPropertyName(CTS2Config.SERVICE_DEFAULTCODESYSTEM_VERSION));
			definedPropertyKeys.add(getPropertyName(CTS2Config.SERVICE_TRANSFORM));
			definedPropertyKeys.add(getPropertyName(CTS2Config.START_PAGE_INDEX));
			definedPropertyKeys.add(getPropertyName(CTS2Config.RESULT_LIMIT));
			definedPropertyKeys.add(getPropertyName(CTS2Config.DOWNLOAD_PAGE_SIZE));
		}
	}
	
	protected String getPropertyName(String property)
	{
		if (property == null)
			return null;
		
		return getProfile() + "." + property;
	}
	
	private boolean getBoolean(String value)
	{
		return (("yes".equalsIgnoreCase(value))||("true".equalsIgnoreCase(value)));
	}
	
	// checks if the property is anything other than prefidened ones
	private boolean isAdditionalPorperty(String propertyName)
	{
		if (propertyName == null)
			return false;
		
		return (!(this.definedPropertyKeys.contains(propertyName)));
	}
	
	public boolean loadProperties(String profile, Properties props)
	{
		setProfile(profile);
		
		if (props == null)
			return false;
		
		String value = null;
		value = props.getProperty(getPropertyName(CTS2Config.SERVICE_ISSECURE));
		this.secure = (CTS2Utils.isNull(value))?false : getBoolean(value);
		
		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.SERVICE_HOST));
		if (!CTS2Utils.isNull(value))
			this.hostName = value;
		
		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.SERVICE_PORT));
		if (!CTS2Utils.isNull(value))
		{
			try
			{
				int pt = Integer.parseInt(value);
				this.port = "" + pt;
			}
			catch(Exception ie)
			{
				ie.printStackTrace();
			}
		}

		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.START_PAGE_INDEX));
		if (!CTS2Utils.isNull(value))
		{
			try
			{
				int pt = Integer.parseInt(value);
				
				if (pt < 0)
					pt = 0;
				
				this.startIndex = pt;
			}
			catch(Exception ie)
			{
				ie.printStackTrace();
			}
		}

		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.RESULT_LIMIT));
		if (!CTS2Utils.isNull(value))
		{
			try
			{
				int pt = Integer.parseInt(value);
				this.resultLimit = pt;
			}
			catch(Exception ie)
			{
				ie.printStackTrace();
			}
		}

		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.DOWNLOAD_PAGE_SIZE));
		if (!CTS2Utils.isNull(value))
		{
			try
			{
				int pt = Integer.parseInt(value);
				this.downloadPageSize = pt;
			}
			catch(Exception ie)
			{
				ie.printStackTrace();
			}
		}
		
		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.SERVICE_SERVICEURLSUFFIX));
		if (!CTS2Utils.isNull(value))
			this.serviceUrlSuffix = value;

		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.LOGIN_USER));
		if (!CTS2Utils.isNull(value))
			this.userName = value;

		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.LOGIN_PSWD));
		if (!CTS2Utils.isNull(value))
			this.password = value;

		value = null;
		value = props.getProperty(getPropertyName(CTS2Config.SERVICE_DEFAULTCODESYSTEM));
		if (!CTS2Utils.isNull(value))
		{
			this.defaultVocab = new VocabularyId();
			this.defaultVocab.name = value;

			value = null;
			value = props.getProperty(getPropertyName(CTS2Config.SERVICE_DEFAULTCODESYSTEM_VERSION));
			if (!CTS2Utils.isNull(value))
				this.defaultVocab.version = value;
		}
		
		String transformPrefix = getPropertyName(CTS2Config.SERVICE_TRANSFORM);
		
		for (Object propertyKey : props.keySet())
		{
			String propertyKeyStr = propertyKey.toString();
			if (propertyKeyStr.startsWith(transformPrefix))
			{
				String transformKey = propertyKeyStr.split(transformPrefix + ".")[1];
				
				if (CTS2Utils.isNull(transformKey))
					continue;
				
				String transformURL = value = props.getProperty(propertyKeyStr);
				
				if (CTS2Utils.isNull(transformURL))
					continue;
				
				this.transforms.put(transformKey.toLowerCase(), transformURL);
			}
			else
			{
				if ((propertyKeyStr.startsWith(getProfile()))&&(isAdditionalPorperty(propertyKeyStr)))
				{
					String actualProperty = propertyKeyStr.split(getProfile() + ".")[1];
					setUserParameter(actualProperty, props.getProperty(propertyKeyStr));
				}
			}
		}
		
		return true;
	}
	
	public boolean loadContext(String profile, String propertiesFile)
	{
		setProfile(profile);
		
		try
		{
			if (propertiesFile != null)
			{
				FileReader fr = new FileReader(new File(propertiesFile));
				Properties props = new Properties();
				props.load(fr);
				loadProperties(profile, props);
				return true;
			}
		}
		catch(Exception e)
		{
			String msg = "WARNING: May be difficulties in reading property file:\"" + propertiesFile + "\" not found.\n" +
	                 "Should be in directory \"" + System.getProperty("user.dir") + "\"\n";
			this.logger_.log(Level.WARNING, msg);
		}
		
		return false;
	}
	
	public void setUserParameter(String paramName, String paramValue)
	{
		if ((paramName != null)&&(paramValue != null))
			this.params.put(paramName, paramValue);
	}
	
	public void removeUserParameter(String paramName)
	{
		if ((paramName != null)&&(this.params.containsKey(paramName)))
			this.params.remove(paramName);
	}
	
	public String getUserParameterValue(String paramName)
	{
		if (paramName != null)
			return this.params.get(paramName);
		
		return null;
	}
	
	public Set<String> getUserParameterList()
	{
		return this.params.keySet();
	}
}
