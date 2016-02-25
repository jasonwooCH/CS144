<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 

<!DOCTYPE html>
<html>
	<head>
	</head>

	<body>

		<form method="GET" action="/eBay/search">
			Keyword Search:
			<input type="text" name="q">
			<input type='hidden' name='numResultsToSkip' value='0' />
			<input type='hidden' name='numResultsToReturn' value='20' />
			<input type="submit" value="Search">
		</form>

		Here are the results for: <%= request.getAttribute("q") %> <br>

		<table border="1">
			<c:forEach begin="0" end="${fn:length(results) - 1}" var="index">
				<tr>
					<td><a href="/eBay/item?ItemID=${results[index].getItemId()}"><c:out value="${results[index].getItemId()}"/></a></td>
					<td><c:out value="${results[index].getName()}"/></td>
				</tr>
			</c:forEach>
		</table>

		<div>
			<c:if test="${numResultsToSkip > 0}">
				<a href="${prevURL}">Prev</a>
			</c:if>
			<c:if test="${fn:length(nextResults) > 0}">
			<a href="${nextURL}">Next</a>
			</c:if>
		</div>
	</body>

</html>