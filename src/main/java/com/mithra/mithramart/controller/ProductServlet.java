package com.mithra.mithramart.controller;

import com.mithra.mithramart.dao.ProductDAO;
import com.mithra.mithramart.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/secure/products")
public class ProductServlet extends HttpServlet {

    // GET /secure/products -> list this seller's products
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        long sellerId = (Long) req.getSession(false).getAttribute("userId");

        try {
            ProductDAO dao = new ProductDAO();
            List<Product> products = dao.findBySeller(sellerId);

            for (Product p : products) {
                resp.getWriter().println(p.getId() + " | " + p.getName() + " | $" + p.getPrice() + " | stock: " + p.getStockQty());
            }
            if (products.isEmpty()) {
                resp.getWriter().println("No products yet.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Error: " + e.getMessage());
        }
    }

    // POST /secure/products -> create a new product
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        HttpSession session = req.getSession(false);
        long sellerId = (Long) session.getAttribute("userId");

        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");
        String category = req.getParameter("category");

        if (name == null || name.isBlank() || priceStr == null || stockStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Name, price, and stockQty are required.");
            return;
        }

        try {
            Product p = new Product();
            p.setSellerId(sellerId);
            p.setName(name);
            p.setDescription(description);
            p.setPrice(new BigDecimal(priceStr));
            p.setStockQty(Integer.parseInt(stockStr));
            p.setCategory(category);

            ProductDAO dao = new ProductDAO();
            long newId = dao.createProduct(p);

            resp.getWriter().println("Product created with ID: " + newId);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Price must be a number, stockQty must be an integer.");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Error: " + e.getMessage());
        }
    }
}