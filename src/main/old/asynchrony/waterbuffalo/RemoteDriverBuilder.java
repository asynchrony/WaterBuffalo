package old.asynchrony.waterbuffalo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteDriverBuilder {


	private CapabilitiesBuilder capabilitiesBuilder;
	
	public RemoteDriverBuilder() {
		this(new CapabilitiesBuilder());
	}
	
	protected RemoteDriverBuilder(CapabilitiesBuilder capabilitiesBuilder) {
		this.capabilitiesBuilder = capabilitiesBuilder;
		
	}


	public WebDriver build() {
		DesiredCapabilities capabilities = capabilitiesBuilder.buildCapabilities();
		return new RemoteWebDriver(capabilities);
	}


	





}
