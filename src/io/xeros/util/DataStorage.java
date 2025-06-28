package io.xeros.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.annotate.PostInit;
import io.xeros.sql.dailytracker.DailyDataTracker;
import io.xeros.sql.dailytracker.TrackerType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 18/03/2024
 */
public class DataStorage {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DATA_FILE = Paths.get("C:/Users/"+Configuration.SAVES+"/Dropbox/etc/cfg/data.json");
    private static final Map<String, Object> dataMap = new HashMap<>();

    public static void saveData(String category, Object data) {
        dataMap.put(category, data);
        saveToFile();
    }

    public static <T> T loadData(String category, Class<T> dataType) {
        loadFromFile();
        T loadedData = dataType.cast(dataMap.get(category));
        return loadedData;
    }

    private static void saveToFile() {
        try {
            String jsonData = gson.toJson(dataMap);
            Files.write(DATA_FILE, jsonData.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFromFile() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        try {
            byte[] jsonData = Files.readAllBytes(DATA_FILE);
            String jsonString = new String(jsonData);
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> dataObjectMap = gson.fromJson(jsonString, type);

            // Clear the existing data map
            dataMap.clear();

            // Iterate over the entries in the deserialized map
            for (Map.Entry<String, Object> entry : dataObjectMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // Handle different types of data and cast them accordingly
                if (value instanceof String || value instanceof Integer || value instanceof Long) {
                    // For String, Integer, and Long, no casting needed
                    dataMap.put(key, value);
                } else if (value instanceof Double) {
                    // For Double, convert to Integer if possible
                    dataMap.put(key, ((Double) value).intValue());
                } else if (value instanceof Map) {
                    // For nested maps, Gson may have deserialized them as LinkedHashMaps
                    // You might need to handle nested maps differently if needed
                    // Here we assume it's a LocalDate
                    dataMap.put(key, LocalDate.of(
                            ((Double) ((Map) value).get("year")).intValue(),
                            ((Double) ((Map) value).get("month")).intValue(),
                            ((Double) ((Map) value).get("day")).intValue()));
                } else {
                    // Handle other types as needed
                    // This is a fallback, you might need to add specific handling for other types
                    // For now, just log the unsupported type
                    System.out.println("Unsupported data type for key: " + key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostInit
    public static void loadDataSpecific() {
        DailyDataTracker.today = loadData("today", LocalDate.class);
        for (TrackerType trackerType : TrackerType.values()) {
            Integer trackerData = loadData(trackerType.name(), Integer.class);
            if (trackerData != null) {
                trackerType.setTrackerData(trackerData);
            }
        }
    }
}
