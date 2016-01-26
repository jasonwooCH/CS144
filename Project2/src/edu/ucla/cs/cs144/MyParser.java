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

            //else
            //    System.out.println("GARBAGE LINE");

            //else if Text, it's whitespace - garbage data
        }

        
        /**************************************************************/
        
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
        itemMap.put("Buy_Price", null);

        List<String> categoryList = new ArrayList<String>();

        org.w3c.dom.NamedNodeMap nattrib = n.getAttributes();
        // should only have one
        String item_id = null;
        if(nattrib != null && nattrib.getLength() == 1) {
            item_id = nattrib.item(0).getNodeValue(); 
            itemMap.put("ItemID", item_id); // update itemID
            //System.out.println("ItemID is " + item_id);
        }
        
        org.w3c.dom.NodeList nlist = n.getChildNodes();

        for(int i=0; i < nlist.getLength(); i++) {

            String elemName = nlist.item(i).getNodeName();

            // Construct the Item Table
            if (itemMap.containsKey(elemName)) {
                org.w3c.dom.NodeList elemChild = nlist.item(i).getChildNodes();

                if (elemChild.getLength() != 0) {
                    String result = elemChild.item(0).getNodeValue();

                    if (result.length() > 4000) {
                        result = result.substring(0, 4000);
                        //System.out.println("OVER " + result.length() + elemName);
                    }

                    itemMap.put(elemName, result);
                    //System.out.println(elemName + " " + elemChild.item(0).getNodeValue());
                    //System.out.println("ItemID is " + item_id);
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
                org.w3c.dom.NodeList elemChild = nlist.item(i).getChildNodes();

                if (elemChild.getLength() != 0) {

                }
            }

        }

        /* PRINT OUT Item.dat
        System.out.println(itemMap.get("ItemID") + ", " + 
                           itemMap.get("Name") + ", " +
                           //itemMap.get("Description") + ", " +
                           itemMap.get("First_Bid") + ", " +
                           itemMap.get("Started") + ", " +
                           itemMap.get("Ends") + ", " +
                           itemMap.get("Number_of_Bids") + ", " +
                           itemMap.get("Currently") + ", " +
                           itemMap.get("Country") + ", " +
                           itemMap.get("Buy_Price"));
        */

        /* PRINT OUT ItemCategory.dat
        for (int i = 0; i < categoryList.size(); i++) 
            System.out.println(itemMap.get("ItemID") + ", " + categoryList.get(i));
        */

    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }

        /*
        try {
            System.setOut(new PrintStream(new File("categoryTest.dat")));
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
