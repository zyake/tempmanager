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

</dl>

<h2>Current Status</h2>
<dl>
 <dt>Last Recorded</dt>
 <dd>${status.recordedTimestamp}</dd>
 <dt>Temprature</dt>
 <dd>${status.temprature} Celsius</dd>
</dl>

<h2>Weekly Data</h2>
<table border="1">
    <tr>
        <td>Date</td>
        <td>avg</td>
        <td>max</td>
        <td>min</td>
        <td>count</td>
    </tr>
<c:forEach items="${weekly}" var="week">
    <tr>
        <td>${week.date}</td>
        <td>${week.avgTemp}</td>
        <td>${week.maxTemp}</td>
        <td>${week.minTemp}</td>
        <td>${week.count}</td>
    </tr>
</c:forEach>
</table>

<h2>Monthly Data</h2>
<table border="1">
   <tr>
        <td>Date</td>
        <td>avg</td>
        <td>max</td>
        <td>min</td>
        <td>count</td>
    </tr>
<c:forEach items="${monthly}" var="month">
    <tr>
        <td>${month.date}</td>
        <td>${month.avgTemp}</td>
        <td>${month.maxTemp}</td>
        <td>${month.minTemp}</td>
        <td>${month.count}</td>
    </tr>
</c:forEach>
</table>

<form action="/tempmanager/list_monthly_temp">
<h2>Download Monthly Temprature CSV</h2>
<h3>Year</h3>
 <select name="year">
   <option value="2018">2018</option>
   <option value="2019">2019</option>
 </select>
 <h3>Month</h3>
  <select name="month">
    <option value="1">1</option>
    <option value="2">2</option>
    <option value="3">3</option>
    <option value="4">4</option>
    <option value="5">5</option>
    <option value="6">6</option>
    <option value="7">7</option>
    <option value="8">8</option>
    <option value="9">9</option>
    <option value="10">10</option>
    <option value="11">11</option>
    <option value="12">12</option>
  </select>
  <p>
  <input type="submit" name="download" value="download"/>
  </p>
</form>

<form action="/tempmanager/list_monthly_temp_slow">
<h2>Download Monthly Temprature CSV(SLOW)</h2>
<h3>Year</h3>
 <select name="year">
   <option value="2018">2018</option>
   <option value="2019">2019</option>
 </select>
 <h3>Month</h3>
  <select name="month">
    <option value="1">1</option>
    <option value="2">2</option>
    <option value="3">3</option>
    <option value="4">4</option>
    <option value="5">5</option>
    <option value="6">6</option>
    <option value="7">7</option>
    <option value="8">8</option>
    <option value="9">9</option>
    <option value="10">10</option>
    <option value="11">11</option>
    <option value="12">12</option>
  </select>
  <p>
  <input type="submit" name="download" value="download"/>
  </p>
</form>

<form action="/tempmanager/list_yearly_temp">
<h2>Download Yearly Temprature CSV</h2>
<h3>Year</h3>
 <select name="year">
   <option value="2018">2018</option>
   <option value="2019">2019</option>
 </select>
 <p>
 <input type="submit" name="download" value="download"/>
 </p>
</form>
</body>
</html>
