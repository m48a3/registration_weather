<%@ page import="servlets.AdminServlet" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Panel</title>
</head>
<body>
<h1>Admin Panel</h1>

<!-- Display the list of users and a form for deletion -->
<form method="post" action="admin">
    <table>
        <tr>
            <th>Select</th>
            <th>ID</th>
            <th>Username</th>
            <th>Action</th>
        </tr>
        <%
            List<AdminServlet.User> users = (List<AdminServlet.User>) request.getAttribute("users");
            if (users != null && !users.isEmpty()) {
                for (AdminServlet.User user : users) {
        %>
        <tr>
            <td><input type="checkbox" name="selectedUsers" value="<%= user.getId() %>"></td>
            <td><%= user.getId() %></td>
            <td><%= user.getUsername() %></td>
            <td>Delete</td>
        </tr>
        <%
                }
            }
        %>
    </table>
    <input type="submit" value="Delete Selected Users">
</form>

<!-- Add a link to log out from the admin panel if needed -->

</body>
</html>