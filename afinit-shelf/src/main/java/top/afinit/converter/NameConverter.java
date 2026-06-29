package top.afinit.converter;

import org.springframework.stereotype.Component;

@Component
public class NameConverter {
    /**
     * 表名 → Java类名
     */
    public static String toClassName(String tableName) {
        StringBuilder sb = new StringBuilder();
        String normalized = tableName == null ? "" : tableName.trim();
        String[] parts = normalized.split("[_\\-\\s.]+");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase());
        }

        return sb.isEmpty() ? "Generated" : sb.toString();
    }

    /**
     * 字段名 → Java属性名（驼峰）
     */
    public static String toCamel(String columnName) {

        String className = toClassName(columnName);

        return Character.toLowerCase(className.charAt(0))
                + className.substring(1);
    }
}
