<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<!DOCTYPE html>
<html>
	<head>

		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" /> 
		<style type="text/css"> 
		  html { height: 100% } 
		  body { height: 100%; margin: 0px; padding: 0px } 
		  #map_canvas { height: 100% } 
		</style> 
		<script type="text/javascript" 
		    src="http://maps.google.com/maps/api/js?sensor=false"> 
		</script> 
		<script type="text/javascript"> 
		  function initialize() { 

		  	var latlng = new google.maps.LatLng(34.063509,-118.44541);

		  	if ("${Latitude}" != '' && "${Longitude}" != '') {
		  		console.log("latitude: ${Latitude}, longitude: ${Longitude}")
		    	latlng = new google.maps.LatLng("${Latitude}","${Longitude}"); 
		  	}
		    else {
		    	var geocoder = new google.maps.Geocoder();

		    	geocoder.geocode( { 'address': "${Location}"}, function(results, status) {
			      	if (status == google.maps.GeocoderStatus.OK) {
			      		latlng = results[0].geometry.location;
			        	map.setCenter(latlng);
			        	
			        	var marker = new google.maps.Marker({
			            	map: map,
			            	position: results[0].geometry.location
			        	});
			        	
			      	} else {
			      		var msg = document.getElementById("item_location");
			      		msg.innerHTML = "This item is given an invalid location<br>Default location of UCLA shown";
			      	}
			    });
		    }

		    console.log("latlng value is = " + latlng);
		    var myOptions = { 
		      zoom: 10, // default is 8  
		      center: latlng, 
		      mapTypeId: google.maps.MapTypeId.ROADMAP 
		    }; 
		    var map = new google.maps.Map(document.getElementById("map_canvas"), 
		        myOptions); 

		    var marker = new google.maps.Marker({
		    		position: latlng,
		    		map: map
		    });
		  } 

		</script> 

	</head>

	<body onload="initialize()">

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

		<h4> Item Location</h4>
		<div id="item_location"></div>
		<div id="map_canvas" style="width: 80%; margin: 0 auto;"></div>

	</body>

</html>