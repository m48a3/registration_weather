package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Сервлет для обработки входа пользователя.
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    /**
     * Обрабатывает GET-запросы для страницы входа.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws IOException      Если возникает ошибка ввода/вывода.
     * @throws ServletException Если возникает ошибка при обработке сервлета.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Отобразить страницу входа
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    /**
     * Обрабатывает POST-запросы для входа пользователя.
     *
     * @param request  Запрос от клиента с данными пользователя.
     * @param response Ответ сервера.
     * @throws IOException      Если возникает ошибка ввода/вывода.
     * @throws ServletException Если возникает ошибка при обработке сервлета.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Connection connection = DatabaseUtils.getConnection();
            String selectUserQuery = "SELECT u.id, u.city, a.admin_id FROM users u " +
                    "LEFT JOIN admins a ON u.id = a.user_id " +
                    "WHERE u.username = ? AND u.password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String city = resultSet.getString("city");
                boolean isAdmin = resultSet.getInt("admin_id") != 0;

                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("username", username);
                session.setAttribute("city", city);
                session.setAttribute("isAdmin", isAdmin);

                if (isAdmin) {

                    response.sendRedirect("admin");
                } else {

                    response.sendRedirect("profile");
                }
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