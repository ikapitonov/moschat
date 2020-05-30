package ru.chat.utils;

import ru.chat.model.ColorItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Color {
    public final static String[] names = {
            "Общий задний фон",
            "Цвет фона кнопок",
            "Цвет текста кнопок",
            "Цвет текста",
            "Второстепенный цвет текста",
            "Цвет ячейки сообщения при наведении мышкой"
    };

    private final static String[] defaults = {
            "#010125",
            "#201367",
            "#ffffff",
            "#ffffff",
            "#9998A8",
            "#191940"
    };

    private static final char defaultSepararot = '-';

    public static String[] getDefaults() {
        return defaults;
    }

    public static List<String> getNames() {
        return Arrays.asList(names);
    }

    public static boolean validate(String[] array) {
        if (array == null || array.length != defaults.length) {
            return false;
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i] == null || array[i].length() != 7) {
                return false;
            }

            for (int j = 0; j < array[i].length(); j++) {
                char c = Character.toLowerCase(array[i].charAt(j));

                if (j == 0 && c == '#') {
                    continue ;
                }

                if (!Character.isDigit(c) && !(c >= 'a' && c <= 'f')) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String convert(String[] array) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]).append(defaultSepararot);
        }
        builder.setLength(builder.length() - 1);

        return builder.toString();
    }

    public static String[] deConvert(String str) {
        return str.split(String.valueOf(defaultSepararot));
    }

    public static List<ColorItem> generate(String colors) {
        String[] currentColors;
        List<ColorItem> list = new ArrayList();

        if (colors == null || colors.isEmpty()) {
            currentColors = defaults;
        }
        else {
            currentColors = deConvert(colors);
        }
        for (int i = 0; i < currentColors.length; i++) {
            list.add(new ColorItem(names[i], currentColors[i]));
        }
        return list;
    }
}
