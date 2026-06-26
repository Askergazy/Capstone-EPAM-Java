package kz.askar.shop.dao;

import kz.askar.shop.entity.Characteristic;
import kz.askar.shop.entity.CharacteristicValue;
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
public class CharacteristicValueDao {

    private static final Logger logger = LoggerFactory.getLogger(CharacteristicValueDao.class);
    private final JdbcTemplate jdbcTemplate;

    public CharacteristicValueDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class CharacteristicValueRowMapper implements RowMapper<CharacteristicValue> {
        @Override
        public CharacteristicValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            CharacteristicValue value = new CharacteristicValue();
            value.setId(rs.getLong("id"));
            value.setValue(rs.getString("value"));

            Long productId = rs.getLong("product_id");
            if (productId != null && productId > 0) {
                Product product = new Product();
                product.setId(productId);
                value.setProduct(product);
            }

            Long optionId = rs.getLong("option_id");
            if (optionId != null && optionId > 0) {
                Characteristic characteristic = new Characteristic();
                characteristic.setId(optionId);
                value.setCharacteristic(characteristic);
            }

            return value;
        }
    }

    public Optional<CharacteristicValue> findById(Long id) {
        logger.debug("Finding characteristic value by id: {}", id);
        String sql = "SELECT * FROM characteristics_values WHERE id = ?";
        List<CharacteristicValue> values = jdbcTemplate.query(sql, new CharacteristicValueRowMapper(), id);
        return values.isEmpty() ? Optional.empty() : Optional.of(values.get(0));
    }

    public List<CharacteristicValue> findAll() {
        logger.debug("Finding all characteristic values");
        String sql = "SELECT * FROM characteristics_values";
        return jdbcTemplate.query(sql, new CharacteristicValueRowMapper());
    }

    public CharacteristicValue save(CharacteristicValue value) {
        if (value.getId() == null) {
            logger.info("Creating new characteristic value");
            return insert(value);
        } else {
            logger.info("Updating characteristic value with id: {}", value.getId());
            return update(value);
        }
    }

    private CharacteristicValue insert(CharacteristicValue value) {
        String sql = "INSERT INTO characteristics_values (product_id, value, option_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, value.getProduct() != null ? value.getProduct().getId() : null);
            ps.setString(2, value.getValue());
            ps.setLong(3, value.getCharacteristic() != null ? value.getCharacteristic().getId() : null);
            return ps;
        }, keyHolder);

        value.setId(keyHolder.getKey().longValue());
        return value;
    }

    private CharacteristicValue update(CharacteristicValue value) {
        String sql = "UPDATE characteristics_values SET product_id = ?, value = ?, option_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            value.getProduct() != null ? value.getProduct().getId() : null,
            value.getValue(),
            value.getCharacteristic() != null ? value.getCharacteristic().getId() : null,
            value.getId()
        );
        return value;
    }

    public void deleteById(Long id) {
        logger.info("Deleting characteristic value with id: {}", id);
        String sql = "DELETE FROM characteristics_values WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<CharacteristicValue> findByProduct(Product product) {
        logger.debug("Finding characteristic values by product id: {}", product.getId());
        String sql = """
            SELECT cv.id, cv.value, cv.product_id, cv.option_id, o.name as characteristic_name
            FROM characteristics_values cv
            LEFT JOIN options o ON cv.option_id = o.id
            WHERE cv.product_id = ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CharacteristicValue value = new CharacteristicValue();
            value.setId(rs.getLong("id"));
            String dbValue = rs.getString("value");
            String charName = rs.getString("characteristic_name");
            logger.info("DB Row: characteristic='{}', value='{}'", charName, dbValue);
            value.setValue(dbValue);

            Long productId = rs.getLong("product_id");
            if (productId != null && productId > 0) {
                Product p = new Product();
                p.setId(productId);
                value.setProduct(p);
            }

            Long optionId = rs.getLong("option_id");
            if (optionId != null && optionId > 0) {
                Characteristic characteristic = new Characteristic();
                characteristic.setId(optionId);
                characteristic.setName(rs.getString("characteristic_name"));
                value.setCharacteristic(characteristic);
            }

            return value;
        }, product.getId());
    }
}
