package jupiter.broadcasting.live.tv.parser;

import java.util.Hashtable;
import java.util.Vector;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
/*
 * Copyright (c) 2012 Shane Quigley
 *
 * This software is MIT licensed see link for details
 * http://www.opensource.org/licenses/MIT
 * 
 * @author Shane Quigley
 */
public class RssHandler extends DefaultHandler{
    private Vector<String> rssTitles;
    private Vector<String> rssLinks;
    private Vector<String> rssEnclosures;
    private String linkString;
    private String titleString;
    private int counter = 0;
    private int maxRecords = 15;
    private boolean isLink = false;
    private boolean isTitle = false;
    private boolean ifInsideItem = false;
    private boolean enclosure = true;
    private boolean badLinkNext = false;
    /**
     * Constructor
     */
    public RssHandler() {
        linkString = "link";
        titleString = "title";
        rssTitles = new Vector<String>();
        rssLinks = new Vector<String>();
        rssEnclosures = new Vector<String>();
    }
    
    /**
     * Constructer that allows a little more control over parsing the feed
     * @param title
     * @param link
     * @param numberOfRecords The max number of item to be parsed.
     */
    public RssHandler(String title,String link,int numberOfRecords) {
        titleString = title;
        linkString = link;
        rssTitles = new Vector<String>();
        rssLinks = new Vector<String>();
        maxRecords = numberOfRecords;
        rssEnclosures = new Vector<String>();
    }
           
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(ifInsideItem){
            isLink = qName.equalsIgnoreCase(linkString);
            isTitle = qName.equalsIgnoreCase(titleString);
            if(enclosure && qName.equalsIgnoreCase("enclosure")){
                rssEnclosures.addElement(attributes.getValue("url"));
            }
        }else{
            ifInsideItem = qName.equalsIgnoreCase("item");
        }
        if(isTitle){
            if(counter > maxRecords){
                throw new SAXException("Parsing limit of "+maxRecords+" items reached");
            }
            counter++;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }
    
    public void characters(char ch[], int start, int length) throws SAXException {
    	String toAdd = new String(ch, start, length);
    	if(!toAdd.contains("del.icio.us")){
	        if (isLink && !badLinkNext) {
	            rssLinks.addElement(new String(ch, start, length));
	            isLink = false;
	        }else if(isTitle){
	            rssTitles.addElement(new String(ch, start, length));
	            isTitle = false;
	            badLinkNext = false;
	        }
    	}else{
    		badLinkNext = true;
    	}
    }
    
    public Hashtable<String,String[]> getTable(){
        Hashtable<String,String[]> output = new Hashtable<String,String[]>();
        for(int i =0; i< rssTitles.size(); i++){
        	try{
        		output.put(rssTitles.elementAt(i), new String[]{rssLinks.elementAt(i),rssEnclosures.elementAt(i)});
        	}catch (Exception e){
        		Log.e("Woops", e.getMessage());
        	}
        }
        return output;
    }
    
    public Vector<String> getTitles(){
        return rssTitles;
    }
}