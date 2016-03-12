package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// http session for project 5
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.text.DateFormat;


public class ConfirmServlet extends HttpServlet implements Servlet {
       
    public ConfirmServlet() {}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {

    	HttpSession session = request.getSession(false);

    	String item_id = (String) session.getAttribute("ItemID");
    	String item_name = (String) session.getAttribute("itemName");
    	String buy_price = (String) session.getAttribute("Buy_Price");
    	String creditNum = (String)request.getParameter("creditNum");
    	Date curr = new Date();

    	request.setAttribute("ItemID", item_id);
    	request.setAttribute("Name", item_name);
    	request.setAttribute("Buy_Price", buy_price);
    	request.setAttribute("creditNum", creditNum);
    	request.setAttribute("currTime", DateFormat.getDateTimeInstance(
    		DateFormat.LONG, DateFormat.LONG).format(curr));

		request.getRequestDispatcher("/confirm.jsp").forward(request, response);


    }
}