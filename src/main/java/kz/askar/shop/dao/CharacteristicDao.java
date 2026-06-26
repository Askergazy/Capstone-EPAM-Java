package kz.askar.shop.dao;

import kz.askar.shop.entity.Category;
import kz.askar.shop.entity.Characteristic;
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
public class CharacteristicDao {

    private static final Logger logger = LoggerFactory.getLogger(CharacteristicDao.class);
    private final JdbcTemplate jdbcTemplate;

    public CharacteristicDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class CharacteristicRowMapper implements RowMapper<Characteristic> {
        @Override
        public Characteristic mapRow(ResultSet rs, int rowNum) throws SQLException {
            Characteristic characteristic = new Characteristic();
            characteristic.setId(rs.getLong("id"));
            characteristic.setName(rs.getString("name"));

            Long categoryId = rs.getLong("category_id");
            if (categoryId != null && categoryId > 0) {
                Category category = new Category();
                category.setId(categoryId);
                characteristic.setCategory(category);
            }

            return characteristic;
        }
    }

    public Optional<Characteristic> findById(Long id) {
        logger.debug("Finding characteristic by id: {}", id);
        String sql = "SELECT * FROM options WHERE id = ?";
        List<Characteristic> characteristics = jdbcTemplate.query(sql, new CharacteristicRowMapper(), id);
        return characteristics.isEmpty() ? Optional.empty() : Optional.of(characteristics.get(0));
    }

    public List<Characteristic> findAll() {
        logger.debug("Finding all characteristics");
        String sql = "SELECT * FROM options";
        return jdbcTemplate.query(sql, new CharacteristicRowMapper());
    }

    public Characteristic save(Characteristic characteristic) {
        if (characteristic.getId() == null) {
            logger.info("Creating new characteristic: {}", characteristic.getName());
            return insert(characteristic);
        } else {
            logger.info("Updating characteristic with id: {}", characteristic.getId());
            return update(characteristic);
        }
    }

    private Characteristic insert(Characteristic characteristic) {
        String sql = "INSERT INTO options (name, category_id) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, characteristic.getName());
            ps.setLong(2, characteristic.getCategory() != null ? characteristic.getCategory().getId() : null);
            return ps;
        }, keyHolder);

        characteristic.setId(keyHolder.getKey().longValue());
        return characteristic;
    }

    private Characteristic update(Characteristic characteristic) {
        String sql = "UPDATE options SET name = ?, category_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            characteristic.getName(),
            characteristic.getCategory() != null ? characteristic.getCategory().getId() : null,
            characteristic.getId()
        );
        return characteristic;
    }

    public void deleteById(Long id) {
        logger.info("Deleting characteristic with id: {}", id);
        String sql = "DELETE FROM options WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Characteristic> findCharacteristicByCategory(Category category) {
        logger.debug("Finding characteristics by category: {}", category.getName());
        String sql = "SELECT * FROM options WHERE category_id = ?";
        return jdbcTemplate.query(sql, new CharacteristicRowMapper(), category.getId());
    }
}
