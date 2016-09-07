package com.lohyenjeong.mybuddy.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class AlertContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<AlertItem> ITEMS = new ArrayList<AlertItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, AlertItem> ITEM_MAP = new HashMap<String, AlertItem>();

    private static final int COUNT = 25;

    static {

        AlertItem d1 = new AlertItem("1", "05.09.2016", 1, "17:34:32", "user@mybuddy.com");
        ITEMS.add(d1);
        ITEM_MAP.put(d1.id, d1);

        AlertItem d2 = new AlertItem("1", "05.09.2016", 1, "17:34:43", "user@mybuddy.com");
        ITEMS.add(d2);
        ITEM_MAP.put(d2.id, d2);

        AlertItem d3 = new AlertItem("1", "05.09.2016", 2, "17:36:07", "user@mybuddy.com");
        ITEMS.add(d3);
        ITEM_MAP.put(d3.id, d3);

        AlertItem d4 = new AlertItem("1", "05.09.2016", 2, "17:36:17", "user@mybuddy.com");
        ITEMS.add(d4);
        ITEM_MAP.put(d4.id, d4);

        AlertItem d5 = new AlertItem("1", "05.09.2016", 0, "17:36:30", "user@mybuddy.com");
        ITEMS.add(d5);
        ITEM_MAP.put(d5.id, d5);

        AlertItem d6 = new AlertItem("1", "05.09.2016", 2, "17:40:22", "user2@mybuddy.com");
        ITEMS.add(d6);
        ITEM_MAP.put(d6.id, d6);

        AlertItem d7 = new AlertItem("1", "05.09.2016", 0, "17:40:32", "user2@mybuddy.com");
        ITEMS.add(d7);
        ITEM_MAP.put(d7.id, d7);

        AlertItem d8 = new AlertItem("1", "05.09.2016", 0, "17:40:42", "user2@mybuddy.com");
        ITEMS.add(d8);
        ITEM_MAP.put(d8.id, d8);

    }



    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class AlertItem {
        public final String id;
        public final int type;
        public final String date;
        public final String time;
        public final String name;

        public AlertItem(String id, String date, int type, String time, String name) {
            this.type = type;
            this.id = id;
            this.date = date;
            this.time = time;
            this.name = name;
        }
    }
}
