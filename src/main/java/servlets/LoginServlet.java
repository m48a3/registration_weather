package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Отобразить страницу входа
        request.getRequestDispatcher("/login.html").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Проверка учетных данных пользователя
        try {
            Connection connection = DatabaseUtils.getConnection();
            String selectUserQuery = "SELECT id FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                // Создание сессии для аутентифицированного пользователя
                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                response.getWriter().write("Login successful");
            } else {
                response.getWriter().write("Login failed");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Login failed");
        }
    }
}