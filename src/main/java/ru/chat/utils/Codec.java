package ru.chat.utils;

public class Codec {
    public static final String STRING_SEPARATOR = "@|$|@";
    public static final String STRING_REPLACE = "@|$";
    public static final String STRING_SEPARATOR_REGEX = "@\\|\\$\\|@";

    public static String mergeStrings(String[] array) {
        if (array == null || array.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {

            while (array[i].contains(STRING_SEPARATOR)) {
                array[i].replace(STRING_SEPARATOR, STRING_REPLACE);
            }

            sb.append(array[i]);

            if (i + 1 < array.length) {
                sb.append(STRING_SEPARATOR);
            }
        }
        return sb.toString();
    }

    public static String[] unmergeStrings(String s) {
        return s != null && s.length() != 0 ? s.split(STRING_SEPARATOR_REGEX) : null;
    }

    public static String[] generate(String[] array) {
        int len = array.length > 5 ? 5 : array.length;
        String[] result = new String[len];

        for (int i = 0; i < len; i++) {
            array[i] = Html.fullDecode(array[i]);
            result[i] = array[i].length() > 50 ? array[i].substring(0, 50) : array[i];
        }
        return result;
    }
}
