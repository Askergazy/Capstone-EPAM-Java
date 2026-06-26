package kz.askar.shop.dao;

import kz.askar.shop.entity.CartItem;
import kz.askar.shop.entity.Product;
import kz.askar.shop.entity.User;
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
public class CartItemDao {

    private static final Logger logger = LoggerFactory.getLogger(CartItemDao.class);
    private final JdbcTemplate jdbcTemplate;

    public CartItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class CartItemRowMapper implements RowMapper<CartItem> {
        @Override
        public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            CartItem cartItem = new CartItem();
            cartItem.setId(rs.getLong("id"));
            cartItem.setQuantity(rs.getInt("quantity"));

            Long userId = rs.getLong("user_id");
            if (userId != null && userId > 0) {
                User user = new User();
                user.setId(userId);
                cartItem.setUser(user);
            }

            Long productId = rs.getLong("product_id");
            if (productId != null && productId > 0) {
                Product product = new Product();
                product.setId(productId);
                cartItem.setProduct(product);
            }

            return cartItem;
        }
    }

    public Optional<CartItem> findById(Long id) {
        logger.debug("Finding cart item by id: {}", id);
        String sql = "SELECT * FROM cart_items WHERE id = ?";
        List<CartItem> items = jdbcTemplate.query(sql, new CartItemRowMapper(), id);
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    public List<CartItem> findAll() {
        logger.debug("Finding all cart items");
        String sql = "SELECT * FROM cart_items";
        return jdbcTemplate.query(sql, new CartItemRowMapper());
    }

    public CartItem save(CartItem cartItem) {
        if (cartItem.getId() == null) {
            logger.info("Creating new cart item");
            return insert(cartItem);
        } else {
            logger.info("Updating cart item with id: {}", cartItem.getId());
            return update(cartItem);
        }
    }

    private CartItem insert(CartItem cartItem) {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, cartItem.getUser() != null ? cartItem.getUser().getId() : null);
            ps.setLong(2, cartItem.getProduct() != null ? cartItem.getProduct().getId() : null);
            ps.setInt(3, cartItem.getQuantity());
            return ps;
        }, keyHolder);

        cartItem.setId(keyHolder.getKey().longValue());
        return cartItem;
    }

    private CartItem update(CartItem cartItem) {
        String sql = "UPDATE cart_items SET user_id = ?, product_id = ?, quantity = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            cartItem.getUser() != null ? cartItem.getUser().getId() : null,
            cartItem.getProduct() != null ? cartItem.getProduct().getId() : null,
            cartItem.getQuantity(),
            cartItem.getId()
        );
        return cartItem;
    }

    public void deleteById(Long id) {
        logger.info("Deleting cart item with id: {}", id);
        String sql = "DELETE FROM cart_items WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<CartItem> findByUser(User user) {
        logger.debug("Finding cart items by user: {}", user.getLogin());
        String sql = """
            SELECT ci.id, ci.quantity, ci.user_id, ci.product_id,
                   p.name as product_name, p.price as product_price, p.image as product_image
            FROM cart_items ci
            JOIN products p ON ci.product_id = p.id
            WHERE ci.user_id = ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CartItem cartItem = new CartItem();
            cartItem.setId(rs.getLong("id"));
            cartItem.setQuantity(rs.getInt("quantity"));

            Long userId = rs.getLong("user_id");
            if (userId != null && userId > 0) {
                User u = new User();
                u.setId(userId);
                cartItem.setUser(u);
            }

            Long productId = rs.getLong("product_id");
            if (productId != null && productId > 0) {
                Product product = new Product();
                product.setId(productId);
                product.setName(rs.getString("product_name"));
                product.setPrice(rs.getInt("product_price"));
                product.setImage(rs.getString("product_image"));
                cartItem.setProduct(product);
            }

            return cartItem;
        }, user.getId());
    }

    public void deleteAll(List<CartItem> cartItems) {
        logger.info("Deleting {} cart items", cartItems.size());
        for (CartItem item : cartItems) {
            deleteById(item.getId());
        }
    }
}
