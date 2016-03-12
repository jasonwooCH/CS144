package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        request.setAttribute("q", request.getParameter("q"));
        request.setAttribute("numResultsToSkip", 
                                request.getParameter("numResultsToSkip"));
        request.setAttribute("numResultsToReturn", 
                                request.getParameter("numResultsToReturn"));

        // your codes here
        String q = request.getParameter("q");
        String strSkip = request.getParameter("numResultsToSkip");
        String strReturn = request.getParameter("numResultsToReturn");

        //System.out.println("strSkip is" + strSkip);
        //System.out.println("strReturn is" + strReturn);

        int numSkip = Integer.parseInt(strSkip);
        int numReturn = Integer.parseInt(strReturn);

        int prevSkip = numSkip - numReturn;
        int nextSkip = numSkip + numReturn;

        String prevURL = "/eBay/search?q=" + q + "&numResultsToSkip="
                + Integer.toString(prevSkip) + "&numResultsToReturn=" + Integer.toString(numReturn);
        String nextURL = "/eBay/search?q=" + q + "&numResultsToSkip="
                + Integer.toString(nextSkip) + "&numResultsToReturn=" + Integer.toString(numReturn);

        request.setAttribute("prevURL", prevURL);
        request.setAttribute("nextURL", nextURL);

        AuctionSearchClient client = new AuctionSearchClient();

        SearchResult[] basicResults = client.basicSearch(q, numSkip, numReturn);
        SearchResult[] nextResults = client.basicSearch(q, nextSkip, numReturn);

        String testStr = "Hello cs144";

        //request.setAttribute("results", q);
        request.setAttribute("results", basicResults);
        request.setAttribute("nextResults", nextResults);

        request.getRequestDispatcher("/search.jsp").forward(request, response);

    }
}
