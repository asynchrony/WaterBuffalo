package com.asynchrony.waterbuffalo;


import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.asynchrony.waterbuffalo.CapabilitiesBuilder;
import com.asynchrony.waterbuffalo.RemoteDriverBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { RemoteDriverBuilder.class, RemoteWebDriver.class })
public class RemoteDriverBuilderTest {

	@Mock private DesiredCapabilities mockCapabilities;
	@Mock private CapabilitiesBuilder mockCapabilitiesBuilder;
	@Mock private HttpCommandExecutor mockExecutor;
	@Mock private RemoteWebDriver mockWebDriver;
	private RemoteDriverBuilder testObject;
	@Mock private FirefoxDriver mockFirefoxDriver;
	@Mock private InternetExplorerDriver mockInternetExplorerDriver;
	@Mock private ChromeDriver mockChromeDriver;
	@Mock private SafariDriver mockSafariDriver;
	
	@Before
	public void setup() {
		initMocks(this);
		when(mockCapabilitiesBuilder.buildCapabilities()).thenReturn(mockCapabilities);
		testObject = new RemoteDriverBuilder(mockCapabilitiesBuilder);
		
	}

	@Test
	public void verifyRemote() throws Exception {
		suppress(method(RemoteWebDriver.class,"init"));
		suppress(method(RemoteWebDriver.class,"startClient"));
		suppress(method(RemoteWebDriver.class,"startSession"));
		whenNew(RemoteWebDriver.class).withArguments(mockCapabilities).thenReturn(mockWebDriver);
		whenNew(HttpCommandExecutor.class).withArguments(null).thenReturn(mockExecutor);
		when(mockCapabilities.getCapability("isRemote")).thenReturn(true);
		testObject.build();
		verifyNew(RemoteWebDriver.class).withArguments(mockCapabilities);
	}
	
	@Test
	public void verifyFirefox() throws Exception {
		whenNew(FirefoxDriver.class).withNoArguments().thenReturn(mockFirefoxDriver);
		when(mockCapabilities.getCapability("isRemote")).thenReturn(false);
		when(mockCapabilities.getBrowserName()).thenReturn(DesiredCapabilities.firefox().getBrowserName());
		testObject.build();
		verifyNew(FirefoxDriver.class).withNoArguments();
	}
	
	@Test
	public void verifySafari() throws Exception {
		whenNew(SafariDriver.class).withNoArguments().thenReturn(mockSafariDriver);
		when(mockCapabilities.getCapability("isRemote")).thenReturn(false);
		when(mockCapabilities.getBrowserName()).thenReturn(DesiredCapabilities.safari().getBrowserName());
		testObject.build();
		verifyNew(SafariDriver.class).withNoArguments();
	}
	
	@Test
	public void verifyChrome() throws Exception {
		whenNew(ChromeDriver.class).withNoArguments().thenReturn(mockChromeDriver);
		when(mockCapabilities.getCapability("isRemote")).thenReturn(false);
		when(mockCapabilities.getBrowserName()).thenReturn(DesiredCapabilities.chrome().getBrowserName());
		testObject.build();
		verifyNew(ChromeDriver.class).withNoArguments();
	}
	
	@Test
	public void verifyInternetExplorer() throws Exception {
		whenNew(InternetExplorerDriver.class).withNoArguments().thenReturn(mockInternetExplorerDriver);
		when(mockCapabilities.getCapability("isRemote")).thenReturn(false);
		when(mockCapabilities.getBrowserName()).thenReturn("foo");
		testObject.build();
		verifyNew(InternetExplorerDriver.class).withNoArguments();
	}

}
