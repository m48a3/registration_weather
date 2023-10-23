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
 * Сервлет для обработки регистрации пользователей.
 */
@WebServlet(name = "RegistrationServlet", urlPatterns = "/register")
public class RegistrationServlet extends HttpServlet {

    /**
     * Обрабатывает GET-запросы для отображения страницы регистрации.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws IOException      Если возникает ошибка ввода/вывода.
     * @throws ServletException Если возникает ошибка при обработке сервлета.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.html").forward(request, response);
    }

    /**
     * Обрабатывает POST-запросы для обработки данных регистрации пользователя.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws IOException Если возникает ошибка ввода/вывода.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        int cityId = Integer.parseInt(request.getParameter("city"));

        String adminPassword = request.getParameter("adminPassword");

        boolean isAdmin = false;
        if (adminPassword.equals("12345")) {
            isAdmin = true;
        }

        if (userExists(username)) {
            response.getWriter().write("Пользователь с таким именем уже существует");
            return;
        }

        try {
            Connection connection = DatabaseUtils.getConnection();
            String insertUserQuery = "INSERT INTO users (username, password, city) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertUserQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, cityId);
            int rowsInserted = preparedStatement.executeUpdate();
            preparedStatement.close();

            if (rowsInserted > 0) {
                if (isAdmin) {
                    // Add the user to the admins table
                    addAdmin(username);
                    // Redirect to the admin panel
                    response.sendRedirect("admin");
                } else {
                    // Redirect to the regular profile page
                    response.sendRedirect("profile");
                }
            } else {
                response.getWriter().write("Registration failed");
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Registration failed");
        }
    }

    /**
     * Добавляет пользователя в таблицу администраторов.
     *
     * @param username Имя пользователя, которого нужно сделать администратором.
     * @throws SQLException Если произошла ошибка SQL.
     */
    private void addAdmin(String username) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        String insertAdminQuery = "INSERT INTO admins (user_id) VALUES ((SELECT id FROM users WHERE username = ?))";
        PreparedStatement preparedStatement = connection.prepareStatement(insertAdminQuery);
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    /**
     * Проверяет, существует ли пользователь с заданным именем.
     *
     * @param username Имя пользователя для проверки.
     * @return true, если пользователь существует, в противном случае - false.
     */
    private boolean userExists(String username) {
        try {
            Connection connection = DatabaseUtils.getConnection();
            String checkUserQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkUserQuery);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
