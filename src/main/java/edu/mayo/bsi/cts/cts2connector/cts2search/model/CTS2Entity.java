package edu.mayo.bsi.cts.cts2connector.cts2search.model;

import java.util.List;
import java.util.Properties;

public class CTS2Entity 
{
	public String preferredName;
	public String code;
	public String reference;
	public List<String> synonyms;
	public Properties properties;
	public List<CTS2Entity> links;
}
