package com.mithra.mithramart.controller;

import com.mithra.mithramart.dao.ProductDAO;
import com.mithra.mithramart.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// Public endpoint - NOT under /secure/*, so no login required.
// This matches spec F3: buyers browse/search without needing an account.
@WebServlet("/products/search")
public class BrowseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");

        String category = req.getParameter("category");
        String keyword = req.getParameter("keyword");

        try {
            ProductDAO dao = new ProductDAO();
            List<Product> results = dao.searchProducts(category, keyword);

            if (results.isEmpty()) {
                resp.getWriter().println("No products found.");
                return;
            }

            for (Product p : results) {
                resp.getWriter().println(
                    p.getId() + " | " + p.getName() + " | $" + p.getPrice() +
                    " | " + p.getCategory() + " | stock: " + p.getStockQty()
                );
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Error: " + e.getMessage());
        }
    }
}