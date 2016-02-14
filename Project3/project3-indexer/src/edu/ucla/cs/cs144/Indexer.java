package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }

    private IndexWriter indexWriter = null;

    public IndexWriter getIndexWriter(boolean create) throws IOException {

        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index1"));
            // rename to /var/lib/lucene/index1 etc.

            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(indexDir, config);
        }

        return indexWriter;
    }

    public void closeIndexWriter() throws IOException {
        if (indexWriter != null)
            indexWriter.close();
    }

    public void indexItem(Item item) throws IOException {

        //System.out.println("Indexing item: " + item);
        IndexWriter writer = getIndexWriter(false);

        Document doc = new Document();
        doc.add(new StringField("id", item.getId(), Field.Store.YES));
        doc.add(new StringField("name", item.getName(), Field.Store.YES));
        String fullSearchableText = item.getName() + " " + item.getDescription();
        
        StringBuilder cats = new StringBuilder();
        List<String> cat_list = item.getCategories();
        
        for (int i = 0; i < cat_list.size(); i++) {
            cats.append(" " + cat_list.get(i));
        }
        String cat_string = cats.toString();

        fullSearchableText += cat_string; 

        doc.add(new TextField("categories", cat_string, Field.Store.YES));
        doc.add(new TextField("Description", item.getDescription(), Field.Store.YES));
        doc.add(new TextField("content", fullSearchableText, Field.Store.NO));
        
        writer.addDocument(doc);
    }
 
    public void rebuildIndexes() {

        Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
    	try {

            Class.forName("com.mysql.jdbc.Driver");

    	    conn = DbManager.getConnection(true);

            Statement stmt = conn.createStatement();


            // new index
            try {
                getIndexWriter(true);
            } catch (IOException ex){
                System.out.println(ex);
            }


            // execute select to get Item attributes
            // and store in to Item class
            String item_id, item_name, item_desc; 
            String item_newcat;
            List<String> item_cat = new ArrayList<String>();
            List<Item> item_list = new ArrayList<Item>();

            ResultSet resultItem = stmt.executeQuery("SELECT ItemID, Name, Description FROM Item");
            while (resultItem.next()) {

                item_id = resultItem.getString("ItemID");
                item_name = resultItem.getString("Name");
                item_desc = resultItem.getString("Description");

                Item curr = new Item(item_id, item_name, item_desc, null);
                item_list.add(curr);
            }

            ResultSet resultCat = stmt.executeQuery("SELECT * FROM ItemCategory");
            while(resultCat.next()) {

                item_id = resultCat.getString("ItemID");
                item_newcat = resultCat.getString("Category");
                int i;
                for (i = 0; i < item_list.size(); i++) {
                    if (item_id.equals(item_list.get(i).getId())) {
                        //System.out.println("FOUND ID MATCH");
                        break;
                    }
                }


                //TODO OPTIMIZE THIS
                if (i >= item_list.size())
                    System.out.println("INDEX OUT OF BOUNDS FOR CATEGORY");
                else
                    item_list.get(i).addCategory(item_newcat);
            }


            // iterate item list and index each
            for(Item item : item_list) {
                try {
                    indexItem(item);
                } catch (IOException ex){
                    System.out.println(ex);
                }
            }



            resultItem.close();
            resultCat.close();
            resultCat.close();
            stmt.close();

            try {
                closeIndexWriter();
            } catch (Exception e) {
                System.out.println("closing index writer exception\n");
            }

            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }

        } catch (SQLException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex){
            System.out.println(ex);
        }

    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
