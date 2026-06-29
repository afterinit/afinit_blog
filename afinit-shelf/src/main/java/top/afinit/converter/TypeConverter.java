package top.afinit.converter;

import org.springframework.stereotype.Component;

@Component
public class TypeConverter {

    public static String toJavaType(String sqlType) {

        if (sqlType == null) return "String";

        sqlType = sqlType.toLowerCase();

        return switch (sqlType) {

            case "int", "tinyint", "smallint" -> "Integer";

            case "bigint" -> "Long";

            case "decimal", "double","float" ->"Double";

            case "datetime","timestamp" -> "LocalDateTime";

            case "date"->"LocalDate";

            case "time" -> "LocalTime";

            case "bit","boolean"-> "Boolean";

            default -> "String";
        };
    }

    public static String toImport(String javaType) {
        if (javaType == null) {
            return null;
        }

        return switch (javaType) {
            case "LocalDateTime" -> "java.time.LocalDateTime";
            case "LocalDate" -> "java.time.LocalDate";
            case "LocalTime" -> "java.time.LocalTime";
            default -> null;
        };
    }
}
