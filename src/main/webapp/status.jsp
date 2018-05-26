<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<html>
<head>
 <title>Status - Temprature Manager</title>
</head>
<body>
<h1>Tempmanager</h1>

<p>timezone: ${timezone}</p>

<h2>Record Count</h2>
<dl>
 <dt>Record Total</dt>
 <dd>${recordTotal}</dd>
 <dt>Today Total</dt>
 <dd>${todayTotal}</dd>
</dl>

<h2>Current Status</h2>
<dl>
 <dt>Last Recorded</dt>
 <dd>${status.recordedTimestamp}</dd>
 <dt>Temprature</dt>
 <dd>${status.temprature} Celsius</dd>
</dl>

<h2>Histories</h2>
<table border="1">
<tr>
 <td>Min</td>
 <td>Max</td>
 <td>Avg</td>
 <td>Date</td>
</tr>
<c:forEach items="${histories}" var="history">
<tr>
    <td>${history.maxTemp}</td>
    <td>${history.minTemp}</td>
    <td>${history.avgTemp}</td>
    <td>${history.date}</td>
</tr>
</c:forEach>
</table>

<form action="/tempmanager/status">
 <input type="submit" name="submit" value="refresh"/>
</form>
</body>
</html>
