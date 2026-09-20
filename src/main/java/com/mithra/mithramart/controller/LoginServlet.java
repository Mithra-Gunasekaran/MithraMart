package com.mithra.mithramart.controller;

import com.mithra.mithramart.dao.UserDAO;
import com.mithra.mithramart.model.User;
import com.mithra.mithramart.util.PasswordUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        resp.setContentType("text/plain");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Email and password are required.");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.findByEmail(email);

            if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println("Invalid email or password.");
                return;
            }

            // Invalidate any old session, then create a fresh one.
            // This regenerates the session ID on login, per spec Section 2 rule 3
            // (prevents session fixation attacks).
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("userId", user.getId());
            newSession.setAttribute("userName", user.getName());
            newSession.setAttribute("userRole", user.getRole());
            newSession.setMaxInactiveInterval(30 * 60); // 30 minute timeout

            resp.getWriter().println("Login successful. Welcome, " + user.getName() + " (" + user.getRole() + ")");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Login failed: " + e.getMessage());
        }
    }
}