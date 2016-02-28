<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 

<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="autosuggest.css" />        
	    <script type="text/javascript" src="autosuggest2.js"></script>
        <script type="text/javascript" src="suggestions2.js"></script>
        <script type="text/javascript">
            window.onload = function () {
                var oTextbox = new AutoSuggestControl(document.getElementById("query"), new StateSuggestions());        
            }
        </script>
	</head>

	<body>

		<form method="GET" action="/eBay/search">
			Keyword Search:
			<input type="text" name="q" id="query">
			<input type='hidden' name='numResultsToSkip' value='0' />
			<input type='hidden' name='numResultsToReturn' value='20' />
			<input type="submit" value="Search">
		</form>

		Here are the results for: <%= request.getAttribute("q") %> <br>

		<table border="1">
			<c:if test="${fn:length(results) > 0}" >
			<c:forEach begin="0" end="${fn:length(results) - 1}" var="index">
				<tr>
					<td><a href="/eBay/item?ItemID=${results[index].getItemId()}"><c:out value="${results[index].getItemId()}"/></a></td>
					<td><c:out value="${results[index].getName()}"/></td>
				</tr>
			</c:forEach>
			</c:if>
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