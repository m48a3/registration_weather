package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "RegistrationServlet", urlPatterns = "/register")
public class RegistrationServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Отобразить страницу регистрации
        request.getRequestDispatcher("/register.html").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        int cityId = Integer.parseInt(request.getParameter("city"));
        // Валидация данных пользователя (можно добавить свои правила валидации)

        // Добавление пользователя в базу данных
        try {
            Connection connection = DatabaseUtils.getConnection();
            String insertUserQuery = "INSERT INTO users (username, password, city) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertUserQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, cityId);
            int rowsInserted = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            if (rowsInserted > 0) {
                response.sendRedirect("login.html");
            } else {
                response.getWriter().write("Registration failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Registration failed");
        }
    }

}