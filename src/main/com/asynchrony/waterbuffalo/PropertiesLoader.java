package com.asynchrony.waterbuffalo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

	protected static final String BROWSER = "browser";
	protected static final String APPSERVER_PATH = "appserver.path";
	protected static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
	protected static final String WEBDRIVER_REMOTE_SERVER = "webdriver.remote.server";
	private Properties environmentProperties;
	public static final String DEFAULT_CHROME_MAC_PATH = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
	public static final String DEFAULT_CHROME_WINDOWS_PATH = System.getenv("APPDATA")
	+ "\\Local\\Google\\Chrome\\Application";
	
	public PropertiesLoader() {
		this(new Properties());
	}
	
	protected PropertiesLoader(Properties properties) {
		environmentProperties = properties;
	}

	public void loadProperties() {
		actualLoad(environmentProperties);
		setChromePath();
		setWebDriverUrl();
	}

	public void actualLoad(Properties environmentProperties) {
		try {
			InputStream environmentPropertiesFile = this.getClass().getResourceAsStream(
					"/com/asynchrony/waterbuffalo/environment.properties");
			environmentProperties.load(environmentPropertiesFile);
		} catch (IOException e) {
			//will all be defaults
		}
	}
	
	public boolean isRemote() {
		return environmentProperties.getProperty(WEBDRIVER_REMOTE_SERVER) != null;
	}
	
	public String getBrowserType() {
		return  environmentProperties.getProperty(BROWSER);
	}

	private void setChromePath() {
		String chromePath = environmentProperties.getProperty(WEBDRIVER_CHROME_DRIVER);
		if (chromePath == null) {
			if (Browser.isMac()) {
				chromePath = PropertiesLoader.DEFAULT_CHROME_MAC_PATH;
			} else if (Browser.isWindows()) {
				chromePath = PropertiesLoader.DEFAULT_CHROME_WINDOWS_PATH;
			} else {
				chromePath = "";
			}
		} 
		System.setProperty(WEBDRIVER_CHROME_DRIVER, chromePath);
	}

	private void setWebDriverUrl() {
		System.setProperty(WEBDRIVER_REMOTE_SERVER, environmentProperties.getProperty(WEBDRIVER_REMOTE_SERVER,""));
	}

	public String getAppPath() {
		return environmentProperties.getProperty(APPSERVER_PATH);
	}

}
