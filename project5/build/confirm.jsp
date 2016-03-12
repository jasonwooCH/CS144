<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
	<title>Payment Confirmation</title>
	</head>

	<body>
		<h3>Payment Confirmation for your item</h3>
		<ul>
			<li>ItemID: <c:out value="${ItemID}" /> </li>
			<li>Item Name: <c:out value="${itemName}" /> </li>
			<li>Buy Price: <c:out value="${Buy_Price}" /> </li>
			<li>Credit Card Number: <c:out value="${creditNum}" /> </li>
			<li>Transaction Time: <c:out value="${currTime}" /></li>
		</ul>

	</body>

</html>
