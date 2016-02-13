package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */

	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {

		try {
			SearchEngine se = new SearchEngine();

			int total = numResultsToSkip + numResultsToReturn;

			TopDocs topDocs = se.performSearch(query, total);
			ScoreDoc[] hits = topDocs.scoreDocs;

			SearchResult[] basicResult = new SearchResult[numResultsToReturn];

			int j = 0;
			for (int i = numResultsToSkip; i < total; i++, j++) {
				//System.out.println(i + "\n");
				try  {
					Document doc = se.getDocument(hits[i].doc);
					String doc_id = doc.get("id");
					String doc_name = doc.get("name");

					basicResult[j] = new SearchResult(doc_id, doc_name);

				} catch (Exception e) {
					break;
				}
			}

			//System.out.println(j + " VALUE OF J\n");

			SearchResult[] actualResult = new SearchResult[j];
			for (int i = 0; i < j; i++) {
				actualResult[i] = basicResult[i];
			}

			return actualResult;

		} catch (Exception e) {
			System.out.println(e + "\n");
		}
		return new SearchResult[0];
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {

		Connection conn = null;

		SearchResult[] basicResult = basicSearch(query, 0, 13000);

		try {
			Class.forName("com.mysql.jdbc.Driver");

    	    conn = DbManager.getConnection(true);

            Statement stmt = conn.createStatement();

            double lx, ly, rx, ry;
            lx = region.getLx(); ly = region.getLy();
            rx = region.getRx(); ry = region.getRy();

            /*
            CallableStatement callableStmt = 
            	conn.prepareCall("{call SET @box = 'Polygon((" 
            								+ lx + " " + ry + ", "
											+ rx + " " + ry + ", "
											+ rx + " " + ly + ", "
											+ lx + " " + ly + ", "
											+ lx + " " + ry + "))';}"); 
									//+ "SELECT ItemID FROM ItemPoint WHERE MBRContains(GeomFromText(@box), Coords);}");
			*/

			String box = "Polygon((" 
						+ lx + " " + ry + ", "
						+ rx + " " + ry + ", "
						+ rx + " " + ly + ", "
						+ lx + " " + ly + ", "
						+ lx + " " + ry + "))";						

            /*
        	callableStmt.setDouble(1, lx);
        	callableStmt.setDouble(2, ry);
        	callableStmt.setDouble(3, rx);
        	callableStmt.setDouble(4, ry);
        	callableStmt.setDouble(5, lx);
        	callableStmt.setDouble(6, ly);
        	callableStmt.setDouble(7, rx);
        	callableStmt.setDouble(8, ly);
        	callableStmt.setDouble(9, lx);
        	callableStmt.setDouble(10, ry);
			*/
            //ResultSet regionResult = 
            //callableStmt.execute();
            System.out.println("SET BOX DONE\n");
            

            /*
            String setBox = "SET @box = 'Polygon((" + lx + " " + ry + ","
											+ rx + " " + ry + ","
											+ lx + " " + ly + ","
											+ rx + " " + ly + ","
											+ lx + " " + ry + "))';\n";
			*/
            ResultSet regionResult = stmt.executeQuery(//setBox + 
            	"SELECT ItemID FROM ItemPoint WHERE MBRContains(GeomFromText(' " + box +  " '), Coords);\n");
						

            int i; int j = 0;
			for (i = 0; j < numResultsToSkip && i < basicResult.length; i++) {

				String curr_id = basicResult[i].getItemId();
				System.out.println("SPATIAL GOT ITEM FROM RESULT\n");

				while(regionResult.next()) {
            		
            		String result_id = regionResult.getString("ItemID");
            		if (curr_id.equals(result_id)) {
            			//System.out.println("SPATIAL FOUND MATCH\n");
            			j++; break;
            		}
            	}

            	regionResult.beforeFirst();
			}

			// not enough to skip
			if (j != numResultsToSkip)
				return new SearchResult[0];


			SearchResult[] spatialResult = new SearchResult[numResultsToReturn];
			for (j = 0; j < numResultsToReturn && i < basicResult.length; i++) {

				String curr_id = basicResult[i].getItemId();

				while(regionResult.next()) {

					String result_id = regionResult.getString("ItemID");
					if (curr_id.equals(result_id)) {
						String curr_name = basicResult[i].getName();

						spatialResult[j] = new SearchResult(result_id, curr_name);
						j++; 
						break;
					}

				}

				regionResult.first();

			}

			// handle numResultsToReturn > available
			if (j != numResultsToReturn) {
				SearchResult[] actualResult = new SearchResult[j];

				for (int k = 0; k < j; k++) {
					actualResult[k] = spatialResult[k];
				}

				return actualResult;

			}

			return spatialResult;




		} catch (SQLException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex){
            System.out.println(ex);
        }


		return new SearchResult[0];
	}

	/**
	 * Rebuilds an Item XML Element (and all of its sub-Elements), for the given
	 * ItemId.
	 * 
	 * @param itemId The ItemId of an item.
	 * @return A String of valid XML data (conforming to the original items.dtd)
	 * containing data about the requested itemId. null is returned when itemId 
	 * is not valid.
	 */
	public String getXMLDataForItemId(String itemId) {
	
		Connection conn = null;

		//SearchResult[] basicResult = basicSearch(query, 0, 13000);

		try {
			Class.forName("com.mysql.jdbc.Driver");

    	    conn = DbManager.getConnection(true);

            Statement stmt = conn.createStatement();

            ResultSet itemResult = stmt.executeQuery("SELECT * FROM Item WHERE ItemID =" + itemId + ";");

            String item_id, 
            		item_name, 
            		item_desc, 
            		first_bid, 
            		started, ends, num_bids, currently, country, buy_price;

           	String returnXML = "";

            if (itemResult.next()) {

            	item_id = itemResult.getString("ItemID");
            	// no match
            	if (!itemId.equals(item_id))
            		return "";

            	// here if match found
				item_name = itemResult.getString("Name");
				item_desc = itemResult.getString("Description");
				first_bid = itemResult.getString("First_Bid");
				started = itemResult.getString("Started");
				ends = itemResult.getString("Ends");
				num_bids = itemResult.getString("Number_of_Bids");
				currently = itemResult.getString("Currently");
				country = itemResult.getString("Country");
				buy_price = itemResult.getString("Buy_Price");


            	String rootTag = "<Item ItemID=\""+itemId+"\">\n";
            	//returnXML.concat(rootTag);
            	returnXML += rootTag;
            	//System.out.println(rootTag);
            	//System.out.println(returnXML);
            	
            	String nameTag = "  <Name>"+item_name+"</Name>\n";
            	returnXML+=nameTag;

            	ResultSet catResult = stmt.executeQuery("SELECT Category FROM ItemCategory WHERE ItemID="
            												+ itemId + ";");
            	//List<String> categoryTags = new ArrayList<String>();

            	while(catResult.next()) {

            		String curr_cat = catResult.getString("Category");
            		returnXML+="  <Category>"+curr_cat+"</Category>\n";

            		//categoryTags.add("  <Category>"+curr_cat+"</Category>\n");
            	}
            	/*
            		System.out.println(rootTag + "\n" + nameTag);
            		for (int i = 0; i < categoryTags.size(); i++) {
            			System.out.println(categoryTags.get(i));
            		}
				*/

            	String currTag = "  <Currently>" + currently + "</Currently>\n";
            	returnXML+=currTag;
            	String fbTag = "  <First_Bid>" + first_bid + "</First_Bid>\n";
            	returnXML+=fbTag;
            	String numTag = "  <Number_of_Bids>" + num_bids + "</Number_of_Bids>\n";
            	returnXML+=numTag;

            	/*  
					Started & Ends - Time format conversion
            	*/
            	SimpleDateFormat xmlFormat = 
                    new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
                SimpleDateFormat tsFormat = 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsed = null;

            	parsed = tsFormat.parse(started);    
                String xmlStarted = xmlFormat.format(parsed);
                String startedTag = "  <Started>" + xmlStarted + "</Started>\n";

                parsed = tsFormat.parse(ends);
                String xmlEnds = xmlFormat.format(parsed);
                String endsTag = "  <Ends>" + xmlEnds + "</Ends>\n";


            	/*
					Process and Format Bid tags
            	*/
            	String bidsRoot = "";
            	if (!num_bids.equals("0")) {

            		Statement stmt1 = conn.createStatement();
            		ResultSet bidResult = stmt1.executeQuery("SELECT * FROM Bids WHERE ItemID="
            												+ itemId + ";");
            		bidsRoot = "  <Bids>\n";
            		returnXML+=bidsRoot;
            	

	            	String bidderId, bidTime, bidAmt;

	            	while (bidResult.next()) {

	            		bidderId = bidResult.getString("UserID");
	            		bidTime = bidResult.getString("BidTime");
	            		bidAmt = bidResult.getString("Amount");

	            		ResultSet bidderResult = stmt.executeQuery("SELECT * FROM Bidder WHERE UserID="
	            													+ "\"" + bidderId +"\""+ ";");
	            		returnXML+="    <Bid>\n";

	            		String bidderRating, bidderLoc, bidderCountry, bidderTag;
	            		if (bidderResult.next()) {

	            			bidderRating = bidderResult.getString("Rating");
	            			bidderLoc = bidderResult.getString("Location");
	            			bidderCountry = bidderResult.getString("Country");

	            			bidderTag = "      <Bidder Rating=" + "\"" + bidderRating + "\" "
	            						+ "UserID=" + "\"" + bidderId + "\">\n";
	            			returnXML+=bidderTag;

	            			// System.out.println(bidderTag);

	            			String bidderLocTag = "";
	            			if (bidderLoc != null)
	            				bidderLocTag = "        <Location>" + bidderLoc + "</Location>\n";
	            			returnXML+=bidderLocTag;

	            			String bidderCountryTag = "";
	            			if (bidderCountry != null)
	            				bidderCountryTag = "        <Country>" + bidderCountry + "</Country>\n";
	            			returnXML+=bidderCountryTag;
	            		}

		            	/*  
							BidTime - Time format conversion
		            	*/
		            	parsed = tsFormat.parse(bidTime);    
		                String xmlBidTime = xmlFormat.format(parsed);
		                String bidTimeTag = "      <Time>" + xmlStarted + "</Time>\n";
		                returnXML+=bidTimeTag;

		                //System.out.println(bidTimeTag);

		                String bidAmtTag = "      <Amount>" + "$" + bidAmt + "</Amount>\n";
		                returnXML+=bidAmtTag;
		                //System.out.println(bidAmtTag);
		                returnXML+="    </Bid>\n";

		            }
		            returnXML+="  </Bids>\n";
		        }



            	/*
					Parse and format Location tag
            	*/
            	ResultSet locResult = stmt.executeQuery("SELECT * FROM ItemLocation WHERE ItemID="
            											+ itemId + ";");
            	String locationTag = "";
            	if (locResult.next()) {
            		String locVal = locResult.getString("Location");
            		String locLat = locResult.getString("Latitude");
            		String locLong = locResult.getString("Longitude");

            		//System.out.println("Here is lat " + locLat + "\n");
            		//System.out.println("NULL WHERE?");


            		if (locLat != null)
            			locLat = " Latitude=" + "\""+locLat+"\" ";
            		else
            			locLat = "";

            		if (locLong != null)
            			locLong = "Longitude=" + "\""+locLong+"\"";
            		else
            			locLong = "";


            		locationTag = 
            			"  <Location" + locLat + locLong + ">" + locVal + "</Location>\n";
            		returnXML+=locationTag;
            	}

            	//System.out.println(locationTag + "\n");


            	String countryTag = "  <Country>" + country + "</Country>\n";
            	returnXML+=countryTag;

                //System.out.println(xmlStarted + "\n" + xmlEnds + "\n");;

            	String descTag = "  <Description>" + item_desc + "</Description>\n";
            	returnXML+=descTag;

            	// TODO: get seller info
            	ResultSet sellerResult = stmt.executeQuery("SELECT * FROM Auction A, Seller S WHERE ItemID="
            												+ itemId + " AND A.UserID=S.UserID;");
            	String sellerId=""; 
            	String sellerRating="";
            	if(sellerResult.next()) {
            		sellerId = sellerResult.getString("UserID");
            		sellerRating = sellerResult.getString("Rating");
            		//System.out.println(sellerId + "\n" + sellerRating);
            	}

            	// TODO: add started and ends here
            	returnXML+=startedTag;
            	returnXML+=endsTag;

            	String sellerTag = "  <Seller Rating=" + "\"" + sellerRating + "\" "
            						+ "UserID=" + "\"" + sellerId + "\" />\n";
            	//System.out.println(sellerTag);
            	returnXML+=sellerTag;


            	String rootClose = "</Item>\n";
            	returnXML += rootClose;

            	//System.out.println(returnXML);

            	return returnXML;

            }
            else // no match
            	return "";


        } catch (Exception e) {
        	System.out.println(e + "\n");
        }
        
		return "";
	}
	
	public String echo(String message) {
		return message;
	}

}
