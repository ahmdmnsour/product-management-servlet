package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private ProductManager productManager = new ProductManager();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Product product = objectMapper.readValue(req.getInputStream(), Product.class);

        product.setId(productManager.addProduct(product));

        res.setContentType("application/json");
        res.getWriter().write(product.toString());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String idParam = req.getParameter("id");

        res.setContentType("application/json");
        PrintWriter out = res.getWriter();

        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Optional<Product> product = productManager.getProductById(id);

            if (product.isPresent()) {
                out.write(product.get().toString());
            } else {
                out.write("{\"error\": \"Product not found\"}");
            }
        } else {
            List<Product> allProducts = productManager.getAllProducts();
            out.write("[");
            for (int i = 0; i < allProducts.size(); i++) {
                out.write(allProducts.get(i).toString());
                if (i < allProducts.size() - 1) {
                    out.write(",");
                }
            }
            out.write("]");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        if (productManager.deleteProduct(id)) {
            res.getWriter().write("Product deleted successfully!");
        } else {
            res.getWriter().write("Product not found!");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Product product = objectMapper.readValue(req.getInputStream(), Product.class);

        Optional<Product> updatedProduct = productManager.updateProduct(id, product);

        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        if (updatedProduct.isPresent()) {
            out.write(updatedProduct.get().toString());
        } else {
            out.write("Product not found!");
        }
    }
}
