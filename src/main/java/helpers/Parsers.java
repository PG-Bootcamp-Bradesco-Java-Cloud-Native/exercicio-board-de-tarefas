package helpers;

public class Parsers {
    public static Integer tryParseInteger(String s) {
        Integer value;
        try {
            value = Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
        return value;
    }
}
