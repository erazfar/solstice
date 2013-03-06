package com.example.solstice;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * Class used to parse the atom feed XML data.
 * @see {@link DefaultHandler}
 */
public class XmlHandler extends DefaultHandler {
	
	private static final String LOG_TAG = "XmlHandler";
	private List<FeedItem> itemList;
	StringBuffer chars = new StringBuffer();
	boolean found = false;
	FeedItem item;

	public List<FeedItem> getLatestArticles(String feedUrl) {
		itemList = new ArrayList<FeedItem>();		
		
		try {
			URL url = new URL(feedUrl);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			
			reader.setContentHandler(this);
			reader.parse(new InputSource(url.openStream()));
			
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString());
		} catch (SAXException e) {
			Log.e(LOG_TAG, e.toString());
		} catch (ParserConfigurationException e) {	
			Log.e(LOG_TAG, e.toString());
		}

		return itemList;
	}

	/*
	 * Looks for the start element in the XML. When the start element is found, sets the boolean to true so that 
	 * we can execute code from endElement. Also creates a new feedItem.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		chars = new StringBuffer();
		if (localName.equalsIgnoreCase("item")){
			item = new FeedItem();
			found = true;
		}
		
		
		// Reached a start tag
	}
	
	/*
	 * After startElement is found, looks for Authors, Dates, Titles, and Links, and appends them
	 * to the FeedItem object. When the </item> tag is found, sets boolean 'found' to false. It then
	 * adds the FeedItem into the itemList.
	 */

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {		
		// Reached an end tag
		if (localName.equalsIgnoreCase("item")){
			found = false;
			itemList.add(item);
		}
		
		else if (localName.equalsIgnoreCase("title") && found){
			item.setTitle(chars.toString());
		}
		
		else if (localName.equalsIgnoreCase("pubdate") && found){
			item.setDatePublished(chars.toString().replace("+0000", ""));
		}
		
		else if (localName.equalsIgnoreCase("link") && found){
			item.setUrl(chars.toString());
		}
		
		else if (localName.equalsIgnoreCase("author") && found){
			item.setAuthor("Author: " + chars.toString().substring(chars.toString().indexOf("("), chars.toString().indexOf(")")).replace("(", "") + "\nEmail: "
					+ chars.toString().substring(0, chars.toString().indexOf(" ")));
		}
		
	}

	@Override
	public void characters(char ch[], int start, int length) {
		chars.append(new String(ch, start, length));
	}
	
}