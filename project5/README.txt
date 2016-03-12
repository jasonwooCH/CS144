Q1.
For which communication(s) do you use the SSL encryption? If you are encrypting the communication from (1) to (2) in Figure 2, for example, write (1)→(2) in your answer.

A: (4)->(5) and (5)->(6)


Q2.
How do you ensure that the item was purchased exactly at the Buy_Price of that particular item?

A: When item page is requested and generated through ItemServlet, in addition to attaching the variables (ItemID, Name, and Buy Price) to the request, we also store these variables in an HTTP session - which is kept in the backend. When the user clicks "Buy Now", they are navigated to pay.jsp, retrieves the variables stored in the HTTP session from the backend. Since these variables are stored in the backend, there is no way the user could have changed the values.