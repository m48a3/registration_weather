package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

/**
 * Сервлет для обработки входа пользователя.
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private static final String API_KEY = "ea60a55e-9bf3-485c-ad40-692d82f5b8ac"; // Замените на свой API-ключ

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

        // Проверка учетных данных пользователя
        try {
            Connection connection = DatabaseUtils.getConnection();
            String selectUserQuery = "SELECT id, city FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String city = resultSet.getString("city");
                String cityFull = "";
                if (city.equals("2")) {
                    cityFull = "Saint-Petersburg";
                }
                if (city.equals("213")) {
                    cityFull = "Moscow";
                }
                if (city.equals("68")) {
                    cityFull = "Kazan";
                }
                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("username", username);
                session.setAttribute("city", cityFull);

                // Получить текущую погоду и сохранить ее в сессии
                String temperature = getCurrentWeather(city);
                session.setAttribute("temperature", temperature);

                request.getRequestDispatcher("/profile.jsp").forward(request, response);
            } else {
                response.getWriter().write("Login failed");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Login failed");
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает текущую погоду для заданного города.
     *
     * @param city Идентификатор города.
     * @return Текущая температура в заданном городе.
     */
    public String getCurrentWeather(String city) {
        if (city != null) {
            String apiUrl = "https://api.weather.yandex.ru/v2/forecast?city=" + city;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("X-Yandex-API-Key", API_KEY);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject fact = json.getJSONObject("fact");
                String temperature = fact.getString("temp");

                return temperature;
            } catch (Exception e) {
                e.printStackTrace();
                return "N/A";
            }
        } else {
            return "Город не найден";
        }
    }
}
