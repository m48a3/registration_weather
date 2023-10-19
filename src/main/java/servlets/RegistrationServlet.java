package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
