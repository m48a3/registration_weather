package servlets;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            Session hibernateSession = DatabaseUtils.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            // Создаем пользователя
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setCity(String.valueOf(cityId));

            // Сохраняем пользователя в базе данных
            hibernateSession.save(user);

            if (isAdmin) {
                // Если пользователь админ, создаем соответствующую запись
                Admin admin = new Admin();
                admin.setUser(user);

                hibernateSession.save(admin);
            }

            transaction.commit();
            hibernateSession.close();

            if (isAdmin) {
                response.sendRedirect("admin");
            } else {
                response.sendRedirect("profile");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Registration failed");
        }
    }

    /**
     * Проверяет, существует ли пользователь с заданным именем.
     *
     * @param username Имя пользователя для проверки.
     * @return true, если пользователь существует, в противном случае - false.
     */
    private boolean userExists(String username) {
        try {
            Session hibernateSession = DatabaseUtils.getSessionFactory().openSession();

            // Проверяем существование пользователя с заданным именем
            Query<User> query = hibernateSession.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            User user = query.uniqueResult();

            hibernateSession.close();

            return user != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
