package kz.askar.shop.dao;

import kz.askar.shop.entity.Role;
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
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setRole(Role.values()[rs.getInt("role")]);
            user.setLogin(rs.getString("login"));
            user.setPassword(rs.getString("password"));
            user.setName(rs.getString("name"));
            user.setLastName(rs.getString("last_name"));
            user.setRegistrationDate(rs.getTimestamp("registration_data"));
            return user;
        }
    }

    public Optional<User> findById(Long id) {
        logger.debug("Finding user by id: {}", id);
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> findByLogin(String login) {
        logger.debug("Finding user by login: {}", login);
        String sql = "SELECT * FROM users WHERE login = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), login);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public List<User> findAll() {
        logger.debug("Finding all users");
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public User save(User user) {
        if (user.getId() == null) {
            logger.info("Creating new user with login: {}", user.getLogin());
            return insert(user);
        } else {
            logger.info("Updating user with id: {}", user.getId());
            return update(user);
        }
    }

    private User insert(User user) {
        String sql = "INSERT INTO users (role, login, password, name, last_name, registration_data) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setInt(1, user.getRole().ordinal());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getName());
            ps.setString(5, user.getLastName());
            ps.setTimestamp(6, user.getRegistrationDate());
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    private User update(User user) {
        String sql = "UPDATE users SET role = ?, login = ?, password = ?, name = ?, last_name = ?, registration_data = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            user.getRole().ordinal(),
            user.getLogin(),
            user.getPassword(),
            user.getName(),
            user.getLastName(),
            user.getRegistrationDate(),
            user.getId()
        );
        return user;
    }

    public void deleteById(Long id) {
        logger.info("Deleting user with id: {}", id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
