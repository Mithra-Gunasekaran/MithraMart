package com.mithra.mithramart.controller;

import com.mithra.mithramart.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        resp.setContentType("text/plain");

        if (name == null || name.isBlank() ||
            email == null || email.isBlank() ||
            password == null || password.length() < 6 ||
            (!"BUYER".equals(role) && !"SELLER".equals(role))) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid input. Password must be at least 6 characters, role must be BUYER or SELLER.");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            boolean success = dao.createUser(name, email, password, role);

            if (success) {
                resp.getWriter().println("Registration successful for: " + email);
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().println("Email already registered.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Registration failed: " + e.getMessage());
        }
    }
}