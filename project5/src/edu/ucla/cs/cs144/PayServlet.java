package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// http session for project 5
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;

public class PayServlet extends HttpServlet implements Servlet {
       
    public PayServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {

    	HttpSession session = request.getSession(false);
    	// we must use an existing session, so if there was not one, don't create

    	/*
    	String serv_name = request.getServerName();
    	int serv_portno = request.getServerPort();

    	String confirmURL = "https://" + serv_name + ":" + Integer.toString(serv_portno)
    						+ request.getContextPath() + "/confirm";


    	request.setAttribute("Confirm_URL", confirmURL);*/

    	String item_id = (String) session.getAttribute("ItemID");
    	String item_name = (String) session.getAttribute("itemName");
    	String buy_price = (String) session.getAttribute("Buy_Price");

    	request.setAttribute("ItemID", item_id);
    	request.setAttribute("Name", item_name);
    	request.setAttribute("Buy_Price", buy_price);


    	request.getRequestDispatcher("/pay.jsp").forward(request, response);


    }
}