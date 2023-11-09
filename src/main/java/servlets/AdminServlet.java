package servlets;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminServlet", urlPatterns = "/admin")
public class AdminServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkAdminAndProceed(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkAdminAndProceed(request, response);
    }

    private void checkAdminAndProceed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Implement your logic for checking admin status
        if (isAdmin(request)) {
            String actionUrl = request.getParameter("actionUrl");

            if ("deleteUsers".equals(actionUrl)) {
                String[] selectedUsers = request.getParameterValues("selectedUsers");

                if (selectedUsers != null && selectedUsers.length > 0) {
                    try (Session session = DatabaseUtils.getSessionFactory().openSession()) {
                        session.beginTransaction();

                        for (String userId : selectedUsers) {
                            Query<?> query = session.createQuery("DELETE FROM User u WHERE u.id = :userId");
                            query.setParameter("userId", Long.parseLong(userId));
                            query.executeUpdate();
                        }

                        session.getTransaction().commit();
                    }

                    response.sendRedirect("admin");
                } else {
                    response.sendRedirect("admin?error=1");
                }
            } else if ("anotherAction".equals(actionUrl)) {
                // Добавь здесь другие действия, если необходимо
                // Например, обработку других действий, кроме удаления пользователей
            } else {
                // Обработка других сценариев
                response.sendRedirect(actionUrl);
            }
        } else {
            response.sendRedirect("login.html");
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        // Implement your logic for checking admin status
        return false;
    }
}
