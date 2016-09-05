package com.lohyenjeong.mybuddy.dummy;

/**
 * Created by lohyenjeong on 5/9/16.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class GestureContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<GestureItem> ITEMS = new ArrayList<GestureItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, GestureItem> ITEM_MAP = new HashMap<String, GestureItem>();

    private static final int COUNT = 25;

    static {

        GestureItem d1 = new GestureItem("1", "01.09.2016", 1, "23:42:01");
        ITEMS.add(d1);
        ITEM_MAP.put(d1.id, d1);

        GestureItem d2 = new GestureItem("1", "01.09.2016", 1, "23:42:11");
        ITEMS.add(d2);
        ITEM_MAP.put(d2.id, d2);

        GestureItem d3 = new GestureItem("1", "01.09.2016", 6, "23:42:13");
        ITEMS.add(d3);
        ITEM_MAP.put(d3.id, d3);

        GestureItem d4 = new GestureItem("1", "01.09.2016", 4, "23:42:26");
        ITEMS.add(d4);
        ITEM_MAP.put(d4.id, d4);

        GestureItem d5 = new GestureItem("1", "01.09.2016", 2, "23:43:03");
        ITEMS.add(d5);
        ITEM_MAP.put(d5.id, d5);

        GestureItem d6 = new GestureItem("1", "01.09.2016", 3, "23:43:17");
        ITEMS.add(d6);
        ITEM_MAP.put(d6.id, d6);

        GestureItem d7 = new GestureItem("1", "01.09.2016", 6, "23:43:20");
        ITEMS.add(d7);
        ITEM_MAP.put(d7.id, d7);

        GestureItem d8 = new GestureItem("1", "01.09.2016", 5, "23:43:49");
        ITEMS.add(d8);
        ITEM_MAP.put(d8.id, d8);

        GestureItem d9 = new GestureItem("1", "01.09.2016", 0, "23:44:");
        ITEMS.add(d9);
        ITEM_MAP.put(d9.id, d9);
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
    public static class GestureItem {
        public final String id;
        public final int type;
        public final String date;
        public final String time;

        public GestureItem(String id, String date, int type, String time) {
            this.type = type;
            this.id = id;
            this.date = date;
            this.time = time;
        }

    }
}
