package top.afinit.converter;

import org.springframework.stereotype.Component;

@Component
public class MpIdTypeConverter {

    public static String resolve(String sqlType, Boolean primary) {

        if (!Boolean.TRUE.equals(primary)) {
            return null;
        }

        if (sqlType == null) {
            return "AUTO";
        }

        sqlType = sqlType.toLowerCase();

        return switch (sqlType) {

            case "int", "tinyint", "smallint" -> "AUTO";

            default -> "ASSIGN_ID";
        };
    }
}
