<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<c:forEach items="${temps}" var="temp">
${temp.recordedTimestamp},${temp.temprature}${newLine}
</c:forEach>