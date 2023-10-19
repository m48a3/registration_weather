package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Сервлет для отображения профиля пользователя.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = "/profile")
public class ProfileServlet extends HttpServlet {

    /**
     * Обрабатывает GET-запросы для отображения профиля пользователя.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws IOException      Если возникает ошибка ввода/вывода.
     * @throws ServletException Если возникает ошибка при обработке сервлета.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        if (session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            String city = (String) session.getAttribute("city");
            String temperature = (String) session.getAttribute("temperature");

            // Выводим информацию на странице
            request.setAttribute("username", username);
            request.setAttribute("city", city);
            request.setAttribute("temperature", temperature);
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        } else {
            response.sendRedirect("registration_weather_war_exploded/login"); // Перенаправляем на страницу входа, если нет активной сессии
        }
    }
}