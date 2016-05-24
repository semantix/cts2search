package edu.mayo.bsi.cts.cts2connector.cts2search.aux;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CTS2Logger 
{
	public Level currentLogLevel_ = Level.WARNING;
	Logger logger = Logger.getLogger("cts2logger");

	public CTS2Logger()	{	}
	
	public CTS2Logger(Level pLevel)	
	{	
		this.currentLogLevel_ = pLevel;
	}
	
	public void turnLoggingOFF()
	{
		this.currentLogLevel_ = Level.OFF;
	}
	
	public void turnLoggingON()
	{
		this.currentLogLevel_ = Level.SEVERE;
	}
	
	public void turnLoggingON(Level level)
	{
		this.currentLogLevel_ = level;
	}
	
	public void log(Level level, String message) 
	{
		if (level == null)
			return;
		
		if (this.currentLogLevel_ == Level.OFF)
			return;
		
		if (level == Level.OFF)
			return;
		
		if (level.intValue() > this.currentLogLevel_.intValue())
			logger.log(level, message);
	}
}
