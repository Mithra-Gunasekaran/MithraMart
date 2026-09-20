package com.mithra.mithramart.controller;

import com.mithra.mithramart.dao.OrderDAO;
import com.mithra.mithramart.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/secure/orders")
public class OrderHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("userRole");

        try {
            OrderDAO dao = new OrderDAO();
            List<Order> orders;

            // Buyers see their own orders; sellers see orders containing their products
            if ("SELLER".equals(role)) {
                orders = dao.findOrdersForSeller(userId);
            } else {
                orders = dao.findOrdersByBuyer(userId);
            }

            if (orders.isEmpty()) {
                resp.getWriter().println("No orders found.");
                return;
            }

            for (Order o : orders) {
                resp.getWriter().println(
                    "Order #" + o.getId() + " | Status: " + o.getStatus() +
                    " | Total: $" + o.getTotalAmount()
                );
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Error: " + e.getMessage());
        }
    }
}