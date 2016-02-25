<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<!DOCTYPE html>
<html>
	<head>
	</head>

	<body>

		<form method="GET" action="/eBay/item">
			Item ID Search:
			<input type="text" name="ItemID">
			<input type="submit" value="Search">
		</form>
		<br>

		<b>Here is the result for ItemID: <%= request.getAttribute("ItemID") %></b>

		<h3><%= request.getAttribute("itemName") %></h3>

		<ul>
			<li>Categories: 
			<c:forEach items="${Categories}" var="cat">
				<c:out value="${cat}" />
			</c:forEach>
			</li>
			<li>Currently: <c:out value="${Currently}" /></li>
			<li>First Bid: <c:out value="${First_Bid}" /></li>
			<li>Number of Bids: <c:out value="${Number_of_Bids}" /></li>

			<!-- Bids -->
			<li>
			Bids:
				<c:if test="${fn:length(BidTimes) > 0}">
				<c:forEach begin="0" end="${fn:length(BidTimes) - 1}" var="i">
					<ul>
					Time: <c:out value="${BidTimes[i]}" />
					<ul>
					<li>Bidder: <c:out value="${BidderIDs[i]}"/> (Rating: <c:out value="${BidderRatings[i]}"/>)</li>
					<li>Location: <c:out value="${BidderLocations[i]}"/>  Country: <c:out value="${BidderCountrys[i]}"/></li>
					<li>Bid Amount: <c:out value="${BidAmounts[i]}"/></li>
					</ul>
					</ul>
				</c:forEach>
				</c:if>
				
			</li>

			<li>Location: <c:out value="${Location}" /> 
			(Latitude: <c:out value="${Latitude}" />, Longitude: <c:out value="${Longitude}"/>)
			</li>
			<li>Country: <c:out value="${Country}"/></li>
			<li>Started: <c:out value="${Started}" /></li>
			<li>Ends: <c:out value="${Ends}" /></li>
		</ul>
		<h4>Description</h4>
		<p><c:out value="${Description}" /></p>
		<h4>Seller Info</h4>
		<p>UserID: <c:out value="${SellerID}" /> (Rating: <c:out value="${SellerRating}" />)</p>

	</body>

</html>