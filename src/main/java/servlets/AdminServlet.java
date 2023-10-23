package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервлет для администраторской панели.
 */
@WebServlet(name = "AdminServlet", urlPatterns = "/admin")
public class AdminServlet extends HttpServlet {
    /**
     * Обрабатывает GET-запросы для администраторской панели.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws ServletException Исключение, связанное с обработкой сервлета.
     * @throws IOException      Исключение, связанное с вводом-выводом.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Проверяем, является ли текущий пользователь администратором.
        boolean isAdmin = false;
        if (session.getAttribute("isAdmin") != null && (boolean) session.getAttribute("isAdmin")) {
            isAdmin = true;
        }

        if (isAdmin) {
            // Получаем список пользователей из базы данных.
            List<User> users = getUsersFromDatabase();

            // Устанавливаем атрибут "users" для передачи в JSP.
            request.setAttribute("users", users);

            // Отображаем страницу админ-панели.
            request.getRequestDispatcher("/admin.jsp").forward(request, response);
        } else {
            // Если текущий пользователь не является администратором, перенаправляем его на страницу входа.
            response.sendRedirect("login.html");
        }
    }

    /**
     * Обрабатывает POST-запросы для удаления пользователей.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws ServletException Исключение, связанное с обработкой сервлета.
     * @throws IOException      Исключение, связанное с вводом-выводом.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] selectedUserIds = request.getParameterValues("selectedUsers");

        if (selectedUserIds != null) {
            for (String userId : selectedUserIds) {
                deleteUserFromDatabase(userId);
            }
        }

        response.sendRedirect("admin");
    }

    /**
     * Удаляет пользователя из базы данных по его идентификатору.
     *
     * @param userId Идентификатор пользователя, который должен быть удален.
     */
    private void deleteUserFromDatabase(String userId) {
        try {
            Connection connection = DatabaseUtils.getConnection();
            String deleteUserQuery = "DELETE FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteUserQuery);
            preparedStatement.setInt(1, Integer.parseInt(userId));
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает список пользователей из базы данных.
     *
     * @return Список объектов User, представляющих пользователей.
     */
    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();

        try {
            Connection connection = DatabaseUtils.getConnection();
            String selectUsersQuery = "SELECT id, username, city FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUsersQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String username = resultSet.getString("username");
                int cityId = resultSet.getInt("city");

                // Создаем объект User и добавляем его в список пользователей.
                User user = new User(userId, username, cityId);
                users.add(user);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Класс, представляющий пользователя.
     */
    public class User {
        private int id;
        private String username;
        private int cityId;

        /**
         * Создает новый объект User.
         *
         * @param id       Идентификатор пользователя.
         * @param username Имя пользователя.
         * @param cityId   Идентификатор города пользователя.
         */
        public User(int id, String username, int cityId) {
            this.id = id;
            this.username = username;
            this.cityId = cityId;
        }

        /**
         * Возвращает идентификатор пользователя.
         *
         * @return Идентификатор пользователя.
         */
        public int getId() {
            return id;
        }

        /**
         * Возвращает имя пользователя.
         *
         * @return Имя пользователя.
         */
        public String getUsername() {
            return username;
        }

        /**
         * Возвращает идентификатор города пользователя.
         *
         * @return Идентификатор города пользователя.
         */
        public int getCityId() {
            return cityId;
        }
    }
}
