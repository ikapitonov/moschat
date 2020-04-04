package ru.chat.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class Html {

    public static String fullDecode(String html) {
        return html == null ? null : Jsoup.parse(html).text();
    }

    public static String decodeParseLines(String html) {
        String result;

        if (html == null)
            return null;

        result = Jsoup.clean(html, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));

        while (result.contains("\n\n\n"))
            result = result.replace("\n\n\n", "\n\n");
        return result;
    }
}
