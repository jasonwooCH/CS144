package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.*;
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
            			System.out.println("SPATIAL FOUND MATCH\n");
            			j++; break;
            		}
            	}

            	regionResult.first();
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

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		return "";
	}
	
	public String echo(String message) {
		return message;
	}

}
