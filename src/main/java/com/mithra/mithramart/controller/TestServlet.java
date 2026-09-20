package com.mithra.mithramart.controller;

import com.mithra.mithramart.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");

        try {
            UserDAO dao = new UserDAO();
            int count = dao.countUsers();
            resp.getWriter().println("Connected to DB successfully. User count: " + count);
        } catch (Exception e) {
            resp.getWriter().println("Database error: " + e.getMessage());
        }
    }
}
