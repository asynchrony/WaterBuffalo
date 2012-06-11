package com.asynchrony.waterbuffalo;


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
		loader.loadProperties();
		String type = loader.getBrowserType();
		if ("Chrome".equals(type)) {
			capabilities = DesiredCapabilities.chrome();
		} else if ("Safari".equals(type)) {
			capabilities = DesiredCapabilities.safari();
		} else if ("Internet Explorer".equals(type)) {
			capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		} else {
			capabilities = DesiredCapabilities.firefox();
		}
		capabilities.setCapability("isRemote", loader.isRemote());
		capabilities.setCapability("appPath", loader.getAppPath());
		return capabilities;
	}

}
