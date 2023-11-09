package servlets;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            SessionFactory sessionFactory = DatabaseUtils.getSessionFactory();
            try (Session session = sessionFactory.openSession()) {
                // Используем HQL (Hibernate Query Language) для получения данных пользователя и админа
                Query query = session.createQuery("SELECT u.id, u.city, ua.admin FROM User u " +
                        "LEFT JOIN UserAdmin ua ON u.id = ua.userId " +
                        "WHERE u.username = :username AND u.password = :password");
                query.setParameter("username", username);
                query.setParameter("password", password);

                Object[] result = (Object[]) ((org.hibernate.query.Query<?>) query).uniqueResult();

                if (result != null) {
                    int userId = (int) result[0];
                    String city = (String) result[1];
                    boolean isAdmin = result[2] != null;

                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("userId", userId);
                    httpSession.setAttribute("username", username);
                    httpSession.setAttribute("city", city);
                    httpSession.setAttribute("isAdmin", isAdmin);

                    if (isAdmin) {
                        response.sendRedirect("admin");
                    } else {
                        response.sendRedirect("profile");
                    }
                } else {
                    response.getWriter().write("Login failed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Login failed");
        }
    }
}
