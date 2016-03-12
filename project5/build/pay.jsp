<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
	<title>Payment Information</title>
	</head>

	<body>
		<h3>Payment Information for your item</h3>
		<ul>
			<li>ItemID: <c:out value="${ItemID}" /> </li>
			<li>Item Name: <c:out value="${itemName}" /> </li>
			<li>Buy Price: <c:out value="${Buy_Price}" /> </li>
		</ul>
		<br>

		<% String confirmURL = "https://" + request.getServerName()+
					":8443" + request.getContextPath() + "/confirm"; %>

		<form method="POST" action="<%= confirmURL %>">
			Please Enter Your Credit Card Number: 
			<input type="text" name="creditNum">
			<input type="submit" value="Confirm">
		</form>

	</body>

</html>
