package com.mithra.mithramart.controller;

import com.mithra.mithramart.dao.CartDAO;
import com.mithra.mithramart.dao.OrderDAO;
import com.mithra.mithramart.model.CartItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/secure/checkout")
public class CheckoutServlet extends HttpServlet {

    // POST /secure/checkout -> mock payment confirmation, places the order
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        long buyerId = (Long) req.getSession(false).getAttribute("userId");

        try {
            CartDAO cartDao = new CartDAO();
            List<CartItem> cartItems = cartDao.getCart(buyerId);

            if (cartItems.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Your cart is empty. Add items before checking out.");
                return;
            }

            // Mock payment confirmation - per spec, no real payment gateway.
            // We just simulate a successful payment step here.
            boolean mockPaymentSuccess = true;

            if (!mockPaymentSuccess) {
                resp.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
                resp.getWriter().println("Mock payment failed.");
                return;
            }

            OrderDAO orderDao = new OrderDAO();
            long orderId = orderDao.placeOrder(buyerId, cartItems);

            resp.getWriter().println("Order placed successfully! Order ID: " + orderId);

        } catch (IllegalStateException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Checkout failed: " + e.getMessage());
        }
    }
}