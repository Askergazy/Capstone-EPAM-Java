package kz.askar.shop.dao;

import kz.askar.shop.entity.Category;
import kz.askar.shop.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDao {

    private static final Logger logger = LoggerFactory.getLogger(ProductDao.class);
    private final JdbcTemplate jdbcTemplate;

    public ProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setPrice(rs.getInt("price"));
            product.setImage(rs.getString("image"));

            Long categoryId = rs.getLong("category_id");
            if (categoryId != null && categoryId > 0) {
                Category category = new Category();
                category.setId(categoryId);
                product.setCategory(category);
            }

            return product;
        }
    }

    public Optional<Product> findById(Long id) {
        logger.debug("Finding product by id: {}", id);
        String sql = "SELECT * FROM products WHERE id = ?";
        List<Product> products = jdbcTemplate.query(sql, new ProductRowMapper(), id);
        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
    }

    public Product findByName(String name) {
        logger.debug("Finding product by name: {}", name);
        String sql = "SELECT * FROM products WHERE name = ?";
        List<Product> products = jdbcTemplate.query(sql, new ProductRowMapper(), name);
        return products.isEmpty() ? null : products.get(0);
    }

    public List<Product> findByCategory(Optional<Category> category) {
        if (category.isEmpty()) {
            logger.debug("Category is empty, returning empty list");
            return List.of();
        }
        logger.debug("Finding products by category: {}", category.get().getName());
        String sql = "SELECT * FROM products WHERE category_id = ?";
        return jdbcTemplate.query(sql, new ProductRowMapper(), category.get().getId());
    }

    public List<Product> findByNameIgnoreCaseContaining(String name) {
        logger.debug("Finding products by name containing: {}", name);
        String sql = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, new ProductRowMapper(), "%" + name + "%");
    }

    public List<Product> findAll() {
        logger.debug("Finding all products");
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, new ProductRowMapper());
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            logger.info("Creating new product: {}", product.getName());
            return insert(product);
        } else {
            logger.info("Updating product with id: {}", product.getId());
            return update(product);
        }
    }

    private Product insert(Product product) {
        String sql = "INSERT INTO products (name, price, image, category_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, product.getName());
            ps.setInt(2, product.getPrice());
            ps.setString(3, product.getImage());
            ps.setLong(4, product.getCategory() != null ? product.getCategory().getId() : null);
            return ps;
        }, keyHolder);

        product.setId(keyHolder.getKey().longValue());
        return product;
    }

    private Product update(Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, image = ?, category_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            product.getName(),
            product.getPrice(),
            product.getImage(),
            product.getCategory() != null ? product.getCategory().getId() : null,
            product.getId()
        );
        return product;
    }

    public void deleteById(Long id) {
        logger.info("Deleting product with id: {}", id);
        String sql = "DELETE FROM products WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
