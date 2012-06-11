package com.asynchrony.waterbuffalo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;


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
		if (!(Boolean) capabilities.getCapability("isRemote")) {
			String browserName = capabilities.getBrowserName();
			if (browserName.equals(DesiredCapabilities.firefox().getBrowserName())) {
				return new FirefoxDriver();
			} else if (browserName.equals(DesiredCapabilities.chrome().getBrowserName())) {
				return new ChromeDriver();
			} else if (browserName.equals(DesiredCapabilities.safari().getBrowserName())) {
				return new SafariDriver();
			} else  {
				return new InternetExplorerDriver();
			}			
		} else {

			return new RemoteWebDriver(capabilities);
		}
	}

}
