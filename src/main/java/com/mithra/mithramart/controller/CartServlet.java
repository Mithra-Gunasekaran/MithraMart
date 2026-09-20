package com.mithra.mithramart.controller;

import com.mithra.mithramart.dao.CartDAO;
import com.mithra.mithramart.model.CartItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/secure/cart")
public class CartServlet extends HttpServlet {

    // GET /secure/cart -> view cart with running total
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        long userId = (Long) req.getSession(false).getAttribute("userId");

        try {
            CartDAO dao = new CartDAO();
            List<CartItem> items = dao.getCart(userId);

            if (items.isEmpty()) {
                resp.getWriter().println("Your cart is empty.");
                return;
            }

            for (CartItem item : items) {
                resp.getWriter().println(
                    "CartItemID: " + item.getId() + " | " + item.getProductName() +
                    " | qty: " + item.getQuantity() + " | $" + item.getUnitPrice() +
                    " each | line total: $" + item.getLineTotal()
                );
            }
            resp.getWriter().println("---");
            resp.getWriter().println("Cart Total: $" + dao.getCartTotal(userId));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Error: " + e.getMessage());
        }
    }

    // POST /secure/cart -> add an item (action=add), update qty (action=update), or remove (action=remove)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        long userId = (Long) req.getSession(false).getAttribute("userId");

        String action = req.getParameter("action");

        try {
            CartDAO dao = new CartDAO();

            if ("add".equals(action)) {
                long productId = Long.parseLong(req.getParameter("productId"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                dao.addItem(userId, productId, quantity);
                resp.getWriter().println("Item added to cart.");

            } else if ("update".equals(action)) {
                long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                boolean success = dao.updateQuantity(cartItemId, userId, quantity);
                resp.getWriter().println(success ? "Cart updated." : "Item not found in your cart.");

            } else if ("remove".equals(action)) {
                long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                boolean success = dao.removeItem(cartItemId, userId);
                resp.getWriter().println(success ? "Item removed." : "Item not found in your cart.");

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Invalid action. Use add, update, or remove.");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid number format.");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Error: " + e.getMessage());
        }
    }
}
