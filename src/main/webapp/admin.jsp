<%@ page import="servlets.User" %>
<%@ page import="java.util.List" %>
<%@ page import="servlets.DatabaseUtils" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.query.Query" %>

<%
    List<User> getAllUsers() {
    List<User> users = null;
    try (Session hibernateSession = DatabaseUtils.getSessionFactory().openSession()) {
        Query<User> query = hibernateSession.createQuery("FROM User", User.class);
        users = query.list();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return users;
}

    List<User> users = getAllUsers();
%>

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
        <thead>
        <tr>
            <th>Select</th>
            <th>ID</th>
            <th>Username</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <% if (users != null && !users.isEmpty()) { %>
        <% for (User user : users) { %>
        <tr>
            <td><input type="checkbox" name="selectedUsers" value="<%= user.getId() %>"></td>
            <td><%= user.getId() %></td>
            <td><%= user.getUsername() %></td>
            <td>Delete</td>
        </tr>
        <% } %>
        <% } %>
        </tbody>
    </table>
    <input type="submit" value="Delete Selected Users">
</form>

<!-- Add a link to log out from the admin panel if needed -->

</body>
</html>
