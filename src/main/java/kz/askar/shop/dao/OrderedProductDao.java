package kz.askar.shop.dao;

import kz.askar.shop.entity.Order;
import kz.askar.shop.entity.OrderedProduct;
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
public class OrderedProductDao {

    private static final Logger logger = LoggerFactory.getLogger(OrderedProductDao.class);
    private final JdbcTemplate jdbcTemplate;

    public OrderedProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class OrderedProductRowMapper implements RowMapper<OrderedProduct> {
        @Override
        public OrderedProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setId(rs.getLong("id"));
            orderedProduct.setCount(rs.getInt("count"));

            Long orderId = rs.getLong("order_id");
            if (orderId != null && orderId > 0) {
                Order order = new Order();
                order.setId(orderId);
                orderedProduct.setOrder(order);
            }

            Long productId = rs.getLong("product_id");
            if (productId != null && productId > 0) {
                Product product = new Product();
                product.setId(productId);
                orderedProduct.setProduct(product);
            }

            return orderedProduct;
        }
    }

    public Optional<OrderedProduct> findById(Long id) {
        logger.debug("Finding ordered product by id: {}", id);
        String sql = "SELECT * FROM ordered_products WHERE id = ?";
        List<OrderedProduct> orderedProducts = jdbcTemplate.query(sql, new OrderedProductRowMapper(), id);
        return orderedProducts.isEmpty() ? Optional.empty() : Optional.of(orderedProducts.get(0));
    }

    public List<OrderedProduct> findAll() {
        logger.debug("Finding all ordered products");
        String sql = "SELECT * FROM ordered_products";
        return jdbcTemplate.query(sql, new OrderedProductRowMapper());
    }

    public OrderedProduct save(OrderedProduct orderedProduct) {
        if (orderedProduct.getId() == null) {
            logger.info("Creating new ordered product");
            return insert(orderedProduct);
        } else {
            logger.info("Updating ordered product with id: {}", orderedProduct.getId());
            return update(orderedProduct);
        }
    }

    private OrderedProduct insert(OrderedProduct orderedProduct) {
        String sql = "INSERT INTO ordered_products (order_id, product_id, count) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, orderedProduct.getOrder() != null ? orderedProduct.getOrder().getId() : null);
            ps.setLong(2, orderedProduct.getProduct() != null ? orderedProduct.getProduct().getId() : null);
            ps.setInt(3, orderedProduct.getCount());
            return ps;
        }, keyHolder);

        orderedProduct.setId(keyHolder.getKey().longValue());
        return orderedProduct;
    }

    private OrderedProduct update(OrderedProduct orderedProduct) {
        String sql = "UPDATE ordered_products SET order_id = ?, product_id = ?, count = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            orderedProduct.getOrder() != null ? orderedProduct.getOrder().getId() : null,
            orderedProduct.getProduct() != null ? orderedProduct.getProduct().getId() : null,
            orderedProduct.getCount(),
            orderedProduct.getId()
        );
        return orderedProduct;
    }

    public void deleteById(Long id) {
        logger.info("Deleting ordered product with id: {}", id);
        String sql = "DELETE FROM ordered_products WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<OrderedProduct> findByOrder(Order order) {
        logger.debug("Finding ordered products by order id: {}", order.getId());
        String sql = """
            SELECT op.id, op.order_id, op.product_id, op.count,
                   p.name as product_name, p.price as product_price, p.image as product_image
            FROM ordered_products op
            JOIN products p ON op.product_id = p.id
            WHERE op.order_id = ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setId(rs.getLong("id"));
            orderedProduct.setCount(rs.getInt("count"));

            Long orderId = rs.getLong("order_id");
            if (orderId != null && orderId > 0) {
                Order o = new Order();
                o.setId(orderId);
                orderedProduct.setOrder(o);
            }

            Long productId = rs.getLong("product_id");
            if (productId != null && productId > 0) {
                Product product = new Product();
                product.setId(productId);
                product.setName(rs.getString("product_name"));
                product.setPrice(rs.getInt("product_price"));
                product.setImage(rs.getString("product_image"));
                orderedProduct.setProduct(product);
            }

            return orderedProduct;
        }, order.getId());
    }
}
