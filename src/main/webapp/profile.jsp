<%@ page import="servlets.ProfileServlet" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Profile</title>
</head>
<body>
<h1>Profile</h1>
<%
    String username = (String) request.getAttribute("username");
    String city = (String) request.getAttribute("city");
    String temperature = (String) request.getAttribute("temperature");
    if (city.equals("213")){city = "Moscow";};
    if (city.equals("68")){city = "Kazan";};
    if (city.equals("2")){city = "Saint-Petersburg";};
%>
<p>Welcome <%= username %>! The temperature in <%= city %> is currently <%= temperature %> degrees!</p>

<form action="profile" method="post">
    <p><input type="submit" name="action" value="makeRequest">Make a request</p>
</form>
<p><a href="${pageContext.request.contextPath}/login">Logout</a></p>
</body>
</html>
