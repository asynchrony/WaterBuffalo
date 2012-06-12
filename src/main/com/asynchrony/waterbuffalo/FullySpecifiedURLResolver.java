package com.asynchrony.waterbuffalo;

import java.net.MalformedURLException;
import java.net.URL;

public class FullySpecifiedURLResolver {

	private URL currentURL;


	public String resolve(String url) {
		String currentURLString = null;
		try {
			if (currentURL == null) {
				currentURL = new URL(url);
			} else {
				 currentURL = new URL(currentURL, url);
			}
			currentURLString = currentURL.toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return currentURLString ;
	}


}
