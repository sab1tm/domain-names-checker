package kz.sab1tm.domainnames.util;

public class StringUtils {

    private StringUtils() {
    }

    public static boolean isCyrillic(String s) {
        return s.chars()
                .mapToObj(Character.UnicodeBlock::of)
                .anyMatch(b -> b.equals(Character.UnicodeBlock.CYRILLIC));
    }
}
