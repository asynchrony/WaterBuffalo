package old.asynchrony.waterbuffalo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

	private Properties environmentProperties;
	public static final String DEFAULT_CHROME_MAC_PATH = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
	public static final String DEFAULT_WINDOWS_PATH = System.getenv("APPDATA")
	+ "\\Local\\Google\\Chrome\\Application";

	public void loadProperties() {
		environmentProperties = new Properties();
		try {
			InputStream environmentPropertiesFile = this.getClass().getResourceAsStream(
					"/com/asynchrony/waterbuffalo/environment.properties");

			environmentProperties.load(environmentPropertiesFile);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	public String getBrowserType() {
		loadProperties();
		setChromePath();
		setWebDriverUrl();
		return  environmentProperties.getProperty("browser");
	}

	public void setChromePath() {
		String chromePath = environmentProperties.getProperty("webdriver.chrome.driver");
		if (chromePath == null) {
			if (Browser.isMac()) {
				chromePath = PropertiesLoader.DEFAULT_CHROME_MAC_PATH;
			} else if (Browser.isWindows()) {
				chromePath = PropertiesLoader.DEFAULT_WINDOWS_PATH;
			}
		}
		System.setProperty("webdriver.chrome.driver", chromePath);
	}

	public void setWebDriverUrl() {
		System.setProperty("webdriver.remote.server", environmentProperties.getProperty("webdriver.remote.server"));
	}

}
