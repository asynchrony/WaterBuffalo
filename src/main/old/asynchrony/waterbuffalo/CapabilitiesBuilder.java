package old.asynchrony.waterbuffalo;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CapabilitiesBuilder {
	
	private final PropertiesLoader loader;

	public CapabilitiesBuilder() {
		this(new PropertiesLoader());
	}
	
	protected CapabilitiesBuilder(PropertiesLoader loader) {
		this.loader = loader;
		
	}
	
	public DesiredCapabilities buildCapabilities() {
		DesiredCapabilities capabilities = null;
		String type = loader.getBrowserType();
		if (type.equals("Firefox")) {
			capabilities = DesiredCapabilities.firefox();
		} else if (type.equals("Chrome")) {
			capabilities = DesiredCapabilities.chrome();
		} else if (type.equals("Safari")) {
			capabilities = DesiredCapabilities.safari();
		} else if (type.equals("Internet Explorer")) {
			capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		} else {
			throw new UnsupportedOperationException("Please set browserType.");
		}
		capabilities.setCapability("isRemote", loader.isRemote());
		return capabilities;
	}

}
