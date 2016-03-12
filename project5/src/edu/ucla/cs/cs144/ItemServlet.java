package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// http session for project 5
import javax.servlet.http.HttpSession;

/* From MyParser - for parsing xml yet again */
import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import java.lang.*;



public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    public static Document stringToDom(String xmlString) 
            throws SAXException, ParserConfigurationException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        if(xmlString.trim().isEmpty())
            return builder.newDocument();

        return builder.parse(new InputSource(new StringReader(xmlString)));
    }

    /* *	*	*	*	*	*	*	*	*	*	*	*	*
	*													*
    *	  Helper functions taken from MyParser.java 	*
	*													*
    *	*	*	*	*	*	*	*	*	*	*	*	*	*/

    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here

        HttpSession session = request.getSession(true);

        String req_item_id = request.getParameter("ItemID");
        request.setAttribute("reqItemID", req_item_id);

    	AuctionSearchClient client = new AuctionSearchClient();

    	String xmlResult = client.getXMLDataForItemId(req_item_id);
    	if (xmlResult == null)
    		System.out.println("Error with getXMLDataForItemId");


    	Document xmlDoc = null;
    	try {

    		xmlDoc = stringToDom(xmlResult);

            session.setAttribute("xmlResult", xmlResult);

    		Element root = xmlDoc.getDocumentElement();

            String item_id = root.getAttribute("ItemID");
            session.setAttribute("ItemID", item_id);
            request.setAttribute("ItemID", item_id);
            

    		/*
				Add basic info
    		*/
    		String itemName = getElementTextByTagNameNR(root, "Name");
    		String currently = getElementTextByTagNameNR(root, "Currently");
            String buy_price = getElementTextByTagNameNR(root, "Buy_Price");
    		String first_bid = getElementTextByTagNameNR(root, "First_Bid");
    		String num_bids = getElementTextByTagNameNR(root, "Number_of_Bids");
    		String location = getElementTextByTagNameNR(root, "Location");
    		String country = getElementTextByTagNameNR(root, "Country");
    		String started = getElementTextByTagNameNR(root, "Started");
    		String ends = getElementTextByTagNameNR(root, "Ends");
    		String description = getElementTextByTagNameNR(root, "Description");

            
            session.setAttribute("itemName", itemName);
            request.setAttribute("itemName", itemName);

    		request.setAttribute("Currently", currently);

            // keep buy price in session
            session.setAttribute("Buy_Price", buy_price);
            request.setAttribute("Buy_Price", buy_price);

    		request.setAttribute("First_Bid", first_bid);
    		request.setAttribute("Number_of_Bids", num_bids);
    		request.setAttribute("Location", location);
    		request.setAttribute("Country", country);
    		request.setAttribute("Started", started);
    		request.setAttribute("Ends", ends);
    		request.setAttribute("Description", description);

    		Element loc = getElementByTagNameNR(root, "Location");
    		request.setAttribute("Latitude", loc.getAttribute("Latitude"));
    		request.setAttribute("Longitude", loc.getAttribute("Longitude"));

    		/* Add Categories */
    		Element[] categories = getElementsByTagNameNR(root, "Category");
    		List<String> categoryList = new ArrayList<String>();

    		for (Element cat : categories) {
    			categoryList.add(getElementText(cat));
    		}

    		request.setAttribute("Categories", categoryList);

    		/* Get Seller Info */
    		Element seller = getElementByTagNameNR(root, "Seller");
    		String sellerID = seller.getAttribute("UserID");
    		String sellerRating = seller.getAttribute("Rating");

    		request.setAttribute("SellerID", sellerID);
    		request.setAttribute("SellerRating", sellerRating);

    		/*
				Process and Add Bid Infos
    		*/
    		Element bids = getElementByTagNameNR(root, "Bids");
    		Element[] bid_arr = getElementsByTagNameNR(bids, "Bid");

    		List<String> bidderList = new ArrayList<String>();
    		List<String> bidRatList = new ArrayList<String>();
    		List<String> bidLocList = new ArrayList<String>();
    		List<String> bidCntList = new ArrayList<String>();
    		List<String> bidTimeList = new ArrayList<String>();
    		List<String> bidAmtList = new ArrayList<String>();

    		for (Element bid : bid_arr) {

    			String bid_timestr = getElementTextByTagNameNR(bid, "Time");

    			SimpleDateFormat xmlFormat = 
                    new SimpleDateFormat("MMM-dd-yy HH:mm:ss");

                Date bid_time = xmlFormat.parse(bid_timestr);

                int i;
                for (i = 0; i < bidTimeList.size(); i++) {
                	Date curr_time = xmlFormat.parse(bidTimeList.get(i));
                	if (curr_time.after(bid_time))
                		break;
                }

    			Element bidder = getElementByTagNameNR(bid, "Bidder");
    			bidderList.add(i, bidder.getAttribute("UserID"));
    			bidRatList.add(i, bidder.getAttribute("Rating"));

    			bidLocList.add(i, getElementTextByTagNameNR(bidder, "Location"));
    			bidCntList.add(i, getElementTextByTagNameNR(bidder, "Country"));

    			bidTimeList.add(i, getElementTextByTagNameNR(bid, "Time"));
    			bidAmtList.add(i, getElementTextByTagNameNR(bid, "Amount"));
    		}

			request.setAttribute("BidderIDs", bidderList);
			request.setAttribute("BidderRatings", bidRatList);
			request.setAttribute("BidderLocations", bidLocList);
			request.setAttribute("BidderCountrys", bidCntList);
			request.setAttribute("BidTimes", bidTimeList);
			request.setAttribute("BidAmounts", bidAmtList);

    	}
    	catch (Exception e) {
    		System.out.println("Error at stringToDom on xmlResult");
    	}

    	request.getRequestDispatcher("/item.jsp").forward(request, response);



    }
}
