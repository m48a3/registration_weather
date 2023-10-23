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
 * Сервлет для отображения профиля пользователя.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = "/profile")
public class ProfileServlet extends HttpServlet {
    private static final String API_KEY = "ea60a55e-9bf3-485c-ad40-692d82f5b8ac"; // Замените на свой API-ключ

    /**
     * Обрабатывает GET-запросы для отображения профиля пользователя.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws IOException      Исключение, связанное с вводом-выводом.
     * @throws ServletException Исключение, связанное с обработкой сервлета.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        if (session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            String city = (String) session.getAttribute("city");

            WeatherRequest lastRequest = getLastWeatherRequestForUser(username);

            if (lastRequest != null) {
                request.setAttribute("city", lastRequest.getCity());
                request.setAttribute("temperature", lastRequest.getTemperature());
                request.setAttribute("weatherRequest", lastRequest);
            } else {
                request.setAttribute("city", "");
                request.setAttribute("temperature", "");
            }
            request.setAttribute("username", username);
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        } else {
            response.sendRedirect("login.html");
        }
    }

    /**
     * Обрабатывает POST-запросы, связанные с профилем пользователя.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws IOException      Исключение, связанное с вводом-выводом.
     * @throws ServletException Исключение, связанное с обработкой сервлета.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = request.getParameter("action");

        if ("makeRequest".equals(action)) {
            HttpSession session = request.getSession();
            String city = (String) session.getAttribute("city");

            String currentTemperature = getCurrentWeather(city);

            String username = (String) session.getAttribute("username");
            if (createWeatherRequest(username, city, currentTemperature)) {
                response.sendRedirect("profile");
            } else {
                response.sendRedirect("profile?error=1");
            }
        }
    }

    /**
     * Создает запись о погодном запросе в базе данных.
     *
     * @param username    Имя пользователя.
     * @param city        Город для запроса погоды.
     * @param temperature Текущая температура.
     * @return true, если запрос успешно создан, в противном случае - false.
     */
    private boolean createWeatherRequest(String username, String city, String temperature) {
        try {
            Connection connection = DatabaseUtils.getConnection();

            String insertRequestQuery = "INSERT INTO weather_requests (user_id, city, temperature) VALUES ((SELECT id FROM users WHERE username = ?), ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertRequestQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, city);
            preparedStatement.setString(3, temperature);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Получает последний погодный запрос для пользователя.
     *
     * @param username Имя пользователя.
     * @return Объект WeatherRequest с последним запросом, или null, если запросы отсутствуют.
     */
    private WeatherRequest getLastWeatherRequestForUser(String username) {
        WeatherRequest lastWeatherRequest = null;

        try {
            Connection connection = DatabaseUtils.getConnection();

            String selectRequestQuery = "SELECT city, temperature, request_time FROM weather_requests " +
                    "WHERE user_id = (SELECT id FROM users WHERE username = ?) " +
                    "ORDER BY request_time DESC LIMIT 1";

            PreparedStatement preparedStatement = connection.prepareStatement(selectRequestQuery);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String city = resultSet.getString("city");
                String temperature = resultSet.getString("temperature");
                String requestTime = resultSet.getString("request_time");

                lastWeatherRequest = new WeatherRequest(city, temperature, requestTime);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lastWeatherRequest;
    }

    /**
     * Получает текущую погоду для заданного города с использованием API.
     *
     * @param city Город для запроса погоды.
     * @return Текущая температура в градусах Цельсия или "N/A" в случае ошибки.
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
            return "City not found";
        }
    }

    /**
     * Класс, представляющий погодный запрос.
     */
    public class WeatherRequest {
        private String city;
        private String temperature;
        private String requestTime;

        /**
         * Создает новый объект WeatherRequest.
         *
         * @param city         Город запроса погоды.
         * @param temperature  Текущая температура в градусах Цельсия.
         * @param requestTime  Время запроса погоды.
         */
        public WeatherRequest(String city, String temperature, String requestTime) {
            this.city = city;
            this.temperature = temperature;
            this.requestTime = requestTime;
        }

        /**
         * Получает город запроса погоды.
         *
         * @return Город запроса погоды.
         */
        public String getCity() {
            return city;
        }

        /**
         * Получает текущую температуру.
         *
         * @return Текущая температура в градусах Цельсия.
         */
        public String getTemperature() {
            return temperature;
        }
    }
}
