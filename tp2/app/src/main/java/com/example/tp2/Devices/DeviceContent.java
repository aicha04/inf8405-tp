package com.example.tp2.Devices;

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
public class DeviceContent {

    /**
     * An array of sample Device items.
     */
    public static final List<DeviceItem> ITEMS = new ArrayList<DeviceItem>();

    /**
     * A map of sample Device items, by ID.
     */
    public static final Map<String, DeviceItem> ITEM_MAP = new HashMap<String, DeviceItem>();

    private static final int COUNT = 1;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDeviceItem(i, "Device " + i, "01:00:5E:xx:xx:xx"));
        }
    }

    private static void addItem(DeviceItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DeviceItem createDeviceItem(int position, String deviceName, String deviceMAC) {
        return new DeviceItem(String.valueOf(position), deviceName, deviceMAC ,makeDetails(position));
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
     * A Device item representing a piece of content.
     */
    public static class DeviceItem {
        public final String id;
        public final String deviceName;
        public final String details;
        public final String deviceMAC;

        public DeviceItem(String id, String deviceName, String deviceMAC ,String details) {
            this.id = id;
            this.deviceName = deviceName;
            this.details = details;
            this.deviceMAC = deviceMAC;
        }

        @Override
        public String toString() {
            return deviceName;
        }
    }
}