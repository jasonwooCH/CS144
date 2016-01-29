/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
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
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */

        Node root = doc.getDocumentElement();
        
        org.w3c.dom.NodeList itemList = root.getChildNodes();

        for (int i = 0; i < itemList.getLength(); i++) {
            Node currItem = itemList.item(i);
            String currType = typeName[currItem.getNodeType()];

            if (currType == "Element")
                processItem(currItem);

            //else if Text, it's whitespace - garbage data
        }

        
        /**************************************************************/
        
    }

    public static void processBid(Node n, String item_id) { // Bid Element
        // each bid = one bidder, time, amount etc.

        HashMap<String, String> bidderMap = new HashMap<String, String>();
        bidderMap.put("UserID", null);
        bidderMap.put("Rating", null);
        bidderMap.put("Location", "\\N");
        bidderMap.put("Country", "\\N");

        HashMap<String, String> bidsMap = new HashMap<String, String>();
        bidsMap.put("Time", null);
        bidsMap.put("Amount", null);

        org.w3c.dom.NodeList nlist = n.getChildNodes();
        // child = Bidder(tree), Time or Amount
        for (int i = 0; i < nlist.getLength(); i++) {

            // Bid information
            if (nlist.item(i).getNodeName() == "Time") { // ex. Dec-09-01 20:16:20
                String bidTime = nlist.item(i).getFirstChild().getNodeValue();
                //System.out.println(bidTime);
                SimpleDateFormat xmlFormat = 
                    new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
                SimpleDateFormat tsFormat = 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date parsed = null;
                try {
                    parsed = xmlFormat.parse(bidTime);    
                }
                catch(ParseException pe) {
                    System.out.println("ERROR: Cannot parse \"" + bidTime + "\"");
                }
                
                String sqlTime = tsFormat.format(parsed);

                bidsMap.put("Time", sqlTime);
                //System.out.println(sqlTime);

            }
            else if (nlist.item(i).getNodeName() == "Amount") {
                String bidAmt = nlist.item(i).getFirstChild().getNodeValue();
                
                String sqlAmt = strip(bidAmt);
                //System.out.println(sqlAmt); 
                bidsMap.put("Amount", sqlAmt);  
            }
            // Construct Bidder Table attributes
            else if (nlist.item(i).getNodeName() == "Bidder") {
                org.w3c.dom.NamedNodeMap nattrib = nlist.item(i).getAttributes();

                // attributes are ID and Rating
                for (int j = 0; j < nattrib.getLength(); j++) {
                    String bidAttr = nattrib.item(j).getNodeName();
                    if (bidderMap.containsKey(bidAttr))
                        bidderMap.put(bidAttr, nattrib.item(j).getNodeValue());
                }

                // only elements are Location or Country
                org.w3c.dom.NodeList bidderInfo = nlist.item(i).getChildNodes();

                for (int j = 0; j < bidderInfo.getLength(); j++) {
                    Node info = bidderInfo.item(j);
                    String infoName = info.getNodeName();
                    if (bidderMap.containsKey(infoName))
                        bidderMap.put(infoName, "\"" + info.getFirstChild().getNodeValue() + "\"");
                }
            }

        }
        ///* PRINT OUT Bidder.dat
        try {
            PrintStream writetoBidder = new PrintStream(
                new FileOutputStream("Bidder.dat", true));


            writetoBidder.append("\"" + bidderMap.get("UserID") + "\"" + "," +
                            bidderMap.get("Rating") + "," +
                            bidderMap.get("Location") + "," +
                            bidderMap.get("Country") + "\n");

        }
        catch (Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }

        ///* PRINT OUT Bids.dat
        try {
            PrintStream writetoBids = new PrintStream(
                new FileOutputStream("Bids.dat", true));

            writetoBids.append(item_id + "," +
                                "\"" + bidderMap.get("UserID") + "\"" + "," +
                                bidsMap.get("Amount") + "," +
                                bidsMap.get("Time") + "\n");
        }
        catch(Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }

        ///* ADD TO EbayUser.dat
        try {
            PrintStream writetoUser = new PrintStream(
                new FileOutputStream("EbayUser.dat", true));

            writetoUser.append(bidderMap.get("UserID") + "\n");
        }
        catch (Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }

    }
    

    /* In this function process the Item relation
        HashMap for each column in table
        Will call other aux functions for Category, Bidder etc.
    */
    public static void processItem(Node n) {

        HashMap<String, String> itemMap = new HashMap<String, String>();
        itemMap.put("ItemID", null);
        itemMap.put("Name", null);
        itemMap.put("Description", null);
        itemMap.put("First_Bid", null);
        itemMap.put("Started", null);
        itemMap.put("Ends", null);
        itemMap.put("Number_of_Bids", null);
        itemMap.put("Currently", null);
        itemMap.put("Country", null);
        itemMap.put("Buy_Price", "\\N");

        List<String> categoryList = new ArrayList<String>();

        HashMap<String, String> locationMap = new HashMap<String, String>();
        locationMap.put("Latitude", "\\N");
        locationMap.put("Longitude", "\\N");
        locationMap.put("Name", null);

        HashMap<String, String> sellerMap = new HashMap<String, String>();
        sellerMap.put("Rating", null);
        sellerMap.put("UserID", null);


        org.w3c.dom.NamedNodeMap nattrib = n.getAttributes();
        // should only have one
        String item_id = null;
        if(nattrib != null && nattrib.getLength() == 1) {
            item_id = nattrib.item(0).getNodeValue(); 
            itemMap.put("ItemID", item_id); // update itemID
        }
        
        org.w3c.dom.NodeList nlist = n.getChildNodes();

        for(int i=0; i < nlist.getLength(); i++) {

            String elemName = nlist.item(i).getNodeName();

            // Construct the Item Table
            if (itemMap.containsKey(elemName)) {
                org.w3c.dom.NodeList elemChild = nlist.item(i).getChildNodes();

                if (elemChild.getLength() != 0) {
                    String result = elemChild.item(0).getNodeValue();

                    if (result.length() > 4000)
                        result = result.substring(0, 4000);
                    

                    itemMap.put(elemName, result);
                }
            }
            // Construct ItemCategory Table
            else if (elemName == "Category") {
                org.w3c.dom.NodeList elemChild = nlist.item(i).getChildNodes();

                if (elemChild.getLength() != 0) {
                    categoryList.add(elemChild.item(0).getNodeValue());
                }
            }
            // Construct the ItemLocation Table
            else if (elemName == "Location") {
                org.w3c.dom.NodeList locChild = nlist.item(i).getChildNodes();

                if (locChild.getLength() != 0) {
                    locationMap.put("Name", locChild.item(0).getNodeValue());
                }

                org.w3c.dom.NamedNodeMap locAttrb = nlist.item(i).getAttributes();
                if (locAttrb.getLength() != 0) {

                    for (int j = 0; j < locAttrb.getLength(); j++) {
                        String latLong = locAttrb.item(j).getNodeName();

                        if (locationMap.containsKey(latLong))
                            locationMap.put(latLong, locAttrb.item(j).getNodeValue());

                    }
                }

            }
            // Construct the Seller and Auction
            else if (elemName == "Seller") {
                org.w3c.dom.NamedNodeMap sellAttrb = nlist.item(i).getAttributes();

                for (int j = 0; j < sellAttrb.getLength(); j++) {
                    String sellerAttr = sellAttrb.item(j).getNodeName();

                    if(sellerMap.containsKey(sellerAttr))
                        sellerMap.put(sellerAttr, sellAttrb.item(j).getNodeValue());

                }
            }
            // Construct Bidder and Bids
            else if (elemName == "Bids") {
                org.w3c.dom.NodeList bidsList = nlist.item(i).getChildNodes();

                for (int j = 0; j < bidsList.getLength(); j++) {
                    Node currItem = bidsList.item(j);
                    String currType = typeName[currItem.getNodeType()];

                    if (currType == "Element")
                        processBid(currItem, itemMap.get("ItemID"));
                }
            }
        }

        ///* PRINT OUT Item.dat
        try {
            PrintStream writetoItem = new PrintStream(
                new FileOutputStream("Item.dat", true));

            writetoItem.append(itemMap.get("ItemID") + "," + 
                           "\""+ itemMap.get("Name") + "\"" + "," +
                           "\"" +itemMap.get("Description") + "\"" + "," +
                           itemMap.get("First_Bid") + "," +
                           itemMap.get("Started") + "," +
                           itemMap.get("Ends") + "," +
                           itemMap.get("Number_of_Bids") + "," +
                           itemMap.get("Currently") + "," +
                           "\"" + itemMap.get("Country") + "\"" + "," +
                           itemMap.get("Buy_Price") + "\n");
        }
        catch (Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }

        ///* PRINT OUT ItemCategory.dat
        try {

            PrintStream writetoItemCat = new PrintStream(
                new FileOutputStream("ItemCategory.dat", true));

            for (int i = 0; i < categoryList.size(); i++) 
                writetoItemCat.append(itemMap.get("ItemID") + "," + 
                                    "\"" + categoryList.get(i) + "\"" + "\n");
        }
        catch (Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }



        ///* PRINT OUT ItemLocation.dat 
        try {
            PrintStream writetoItemLoc = new PrintStream(
                new FileOutputStream("ItemLocation.dat", true));

            writetoItemLoc.append(itemMap.get("ItemID") + "," +
                                    "\"" + locationMap.get("Name") + "\"" + "," +
                                    locationMap.get("Latitude") + "," +
                                    locationMap.get("Longitude") + "\n");

        }
        catch (Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }

        

        ///*  PRINT OUT Seller.dat
        try {
            PrintStream writetoSeller = new PrintStream(
                new FileOutputStream("Seller.dat", true));

            writetoSeller.append("\"" + sellerMap.get("UserID") + "\"" + "," +
                            sellerMap.get("Rating") + "\n");

        }
        catch (Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }        

        ///*  PRINT OUT Auction.dat
        try {
            PrintStream writetoAuction = new PrintStream(
                new FileOutputStream("Auction.dat", true));

            writetoAuction.append(itemMap.get("ItemID") + "," +
                            "\"" + sellerMap.get("UserID") + "\"" + "\n");
        }
        catch (Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }

        ///* ADD TO EbayUser.dat
        try {
            PrintStream writetoUser = new PrintStream(
                new FileOutputStream("EbayUser.dat", true));

            writetoUser.append(sellerMap.get("UserID") + "\n");
        }
        catch (Exception e) {
            System.out.println("PrintStream Error");
            System.exit(2);
        }

        
    }

    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }

        /*
        try {
            System.setOut(new PrintStream(new File("bidsTest.dat")));
        } catch (Exception e) {
             e.printStackTrace();
        }
        */
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
    }
}
