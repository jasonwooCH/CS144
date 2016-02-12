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

			System.out.println(j + " VALUE OF J\n");

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
