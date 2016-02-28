package edu.ucla.cs.cs144;

import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.net.URLEncoder;
import java.io.*;


public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
        //try {

	        PrintWriter out = response.getWriter();

	        String query = request.getParameter("q");
	    	
	    	String charset = "UTF-8";
	    	String encoded = URLEncoder.encode(query, charset);
	    	String url = "http://google.com/complete/search?output=toolbar&q=" + encoded;

	    	response.setContentType("text/xml");

	    	URL gurl = new URL(url);
	    	HttpURLConnection connection = (HttpURLConnection) gurl.openConnection();

	    	connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

	    	connection.setRequestProperty("Accept-Charset", charset);

	    	connection.setConnectTimeout(15 * 1000);
	    	connection.setReadTimeout(15 * 1000);
	    	connection.connect();

	    	BufferedReader reader = new BufferedReader(new InputStreamReader (connection.getInputStream()));
	    	StringBuilder stringBuilder = new StringBuilder();

	    	String line = "";
	    	while ((line = reader.readLine()) != null)
	    		stringBuilder.append(line + "\n");

	    	reader.close();
	    	connection.disconnect();

	    	out.println(stringBuilder.toString());
	    	/*
	    }
	    catch (Exception e) {
	        PrintWriter out = response.getWriter();
	        out.println("<html>");
	        out.println("<head><title>Servlet Example</title></head>");
	        out.println("<body>Hello from a Java Servlet</body>");
	        out.println("</html>");
	        out.close();
	    }
	    */
    }
}
