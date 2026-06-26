package kz.askar.shop.contoller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DataFixController {

    private final JdbcTemplate jdbcTemplate;

    public DataFixController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping(value = "/fix-data", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String fixCharacteristicValues() {
        StringBuilder result = new StringBuilder();

        // Получаем все значения характеристик с названиями опций
        var rows = jdbcTemplate.queryForList(
            "SELECT cv.id, cv.value, cv.product_id, o.name as option_name " +
            "FROM characteristics_values cv " +
            "JOIN options o ON cv.option_id = o.id " +
            "ORDER BY cv.product_id, cv.id"
        );

        result.append("Current data:\n");
        for (var row : rows) {
            result.append(String.format("ID=%s, Product=%s, Option='%s', Value='%s'\n",
                row.get("id"), row.get("product_id"), row.get("option_name"), row.get("value")));
        }

        // Исправляем значения на основе названия опции
        int updated = 0;
        for (var row : rows) {
            String optionName = (String) row.get("option_name");
            Long id = ((Number) row.get("id")).longValue();
            String newValue = getDefaultValue(optionName);

            if (newValue != null) {
                jdbcTemplate.update("UPDATE characteristics_values SET value = ? WHERE id = ?", newValue, id);
                updated++;
            }
        }

        result.append("\nUpdated ").append(updated).append(" rows.\n");

        // Показываем обновленные данные
        result.append("\nUpdated data:\n");
        rows = jdbcTemplate.queryForList(
            "SELECT cv.id, cv.value, cv.product_id, o.name as option_name " +
            "FROM characteristics_values cv " +
            "JOIN options o ON cv.option_id = o.id " +
            "ORDER BY cv.product_id, cv.id"
        );
        for (var row : rows) {
            result.append(String.format("ID=%s, Product=%s, Option='%s', Value='%s'\n",
                row.get("id"), row.get("product_id"), row.get("option_name"), row.get("value")));
        }

        return "<pre>" + result.toString() + "</pre>";
    }

    private String getDefaultValue(String optionName) {
        if (optionName == null) return null;

        String lower = optionName.toLowerCase();
        // Using transliteration patterns for Cyrillic matching
        if (lower.contains("dpi")) {
            return "16000";
        } else if (lower.contains("bluetooth")) {
            return "5.0";
        } else if (lower.contains("\u043f\u043e\u0434\u043a\u043b\u044e\u0447") || lower.contains("\u0442\u0438\u043f")) {
            // подключ, тип -> Беспроводное
            return "\u0411\u0435\u0441\u043f\u0440\u043e\u0432\u043e\u0434\u043d\u043e\u0435";
        } else if (lower.contains("\u0446\u0432\u0435\u0442") || lower.contains("color")) {
            // цвет -> Черный
            return "\u0427\u0435\u0440\u043d\u044b\u0439";
        } else if (lower.contains("\u0432\u0435\u0441") || lower.contains("weight")) {
            // вес -> 150 г
            return "150 \u0433";
        } else if (lower.contains("\u0440\u0430\u0437\u043c\u0435\u0440") || lower.contains("size")) {
            // размер -> Стандартный
            return "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u044b\u0439";
        } else if (lower.contains("\u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b")) {
            // материал -> Пластик
            return "\u041f\u043b\u0430\u0441\u0442\u0438\u043a";
        } else if (lower.contains("\u0433\u0430\u0440\u0430\u043d\u0442\u0438\u044f")) {
            // гарантия -> 12 месяцев
            return "12 \u043c\u0435\u0441\u044f\u0446\u0435\u0432";
        } else if (lower.contains("\u0431\u0430\u0442\u0430\u0440\u0435\u044f") || lower.contains("\u0430\u043a\u043a\u0443\u043c\u0443\u043b\u044f\u0442\u043e\u0440")) {
            // батарея, аккумулятор
            return "Li-Ion 500 mAh";
        } else if (lower.contains("\u0447\u0430\u0441\u0442\u043e\u0442\u0430") || lower.contains("\u0432\u043e\u0441\u043f\u0440\u043e\u0438\u0437\u0432\u0435\u0434\u0435\u043d\u0438")) {
            // частота воспроизведения -> 20-20000 Гц
            return "20-20000 \u0413\u0446";
        }
        // Не указано
        return "\u041d\u0435 \u0443\u043a\u0430\u0437\u0430\u043d\u043e";
    }
}
