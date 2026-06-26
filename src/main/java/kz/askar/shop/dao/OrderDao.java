package kz.askar.shop.dao;

import kz.askar.shop.entity.Order;
import kz.askar.shop.entity.Status;
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
public class OrderDao {

    private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);
    private final JdbcTemplate jdbcTemplate;

    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setAddress(rs.getString("address"));
            order.setStatus(Status.values()[rs.getInt("status")]);
            order.setOrderDate(rs.getTimestamp("order_date"));

            Long userId = rs.getLong("user_id");
            if (userId != null && userId > 0) {
                User user = new User();
                user.setId(userId);
                order.setUser(user);
            }

            return order;
        }
    }

    public Optional<Order> findById(Long id) {
        logger.debug("Finding order by id: {}", id);
        String sql = """
            SELECT o.id, o.address, o.status, o.order_date, o.user_id,
                   u.name as user_name, u.last_name as user_last_name, u.login as user_login
            FROM orders o
            JOIN users u ON o.user_id = u.id
            WHERE o.id = ?
            """;
        List<Order> orders = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setAddress(rs.getString("address"));
            order.setStatus(Status.values()[rs.getInt("status")]);
            order.setOrderDate(rs.getTimestamp("order_date"));

            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setName(rs.getString("user_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setLogin(rs.getString("user_login"));
            order.setUser(user);

            return order;
        }, id);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    public List<Order> findAll() {
        logger.debug("Finding all orders");
        String sql = "SELECT * FROM orders";
        return jdbcTemplate.query(sql, new OrderRowMapper());
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            logger.info("Creating new order");
            return insert(order);
        } else {
            logger.info("Updating order with id: {}", order.getId());
            return update(order);
        }
    }

    private Order insert(Order order) {
        String sql = "INSERT INTO orders (user_id, address, status, order_date) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, order.getUser() != null ? order.getUser().getId() : null);
            ps.setString(2, order.getAddress());
            ps.setInt(3, order.getStatus().ordinal());
            ps.setTimestamp(4, order.getOrderDate());
            return ps;
        }, keyHolder);

        order.setId(keyHolder.getKey().longValue());
        return order;
    }

    private Order update(Order order) {
        String sql = "UPDATE orders SET user_id = ?, address = ?, status = ?, order_date = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            order.getUser() != null ? order.getUser().getId() : null,
            order.getAddress(),
            order.getStatus().ordinal(),
            order.getOrderDate(),
            order.getId()
        );
        return order;
    }

    public void deleteById(Long id) {
        logger.info("Deleting order with id: {}", id);
        String sql = "DELETE FROM orders WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Order> findByUser(User user) {
        logger.debug("Finding orders by user: {}", user.getId());
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, new OrderRowMapper(), user.getId());
    }
}
