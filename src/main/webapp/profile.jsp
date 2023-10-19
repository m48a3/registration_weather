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

%>
<p>Welcome ${username}! The temperature in ${city} is currently ${temperature} degrees!</p>
<p><a href="registration_weather_war_exploded/login">Logout</a></p>
</body>
</html>
