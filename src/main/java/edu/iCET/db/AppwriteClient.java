package edu.iCET.db;

import edu.iCET.util.EnvLoader;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

public class AppwriteClient {
    private static OkHttpClient client;

    private AppwriteClient() {}

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    public static Request.Builder getRequestBuilder(String path) {
        String endpoint = EnvLoader.get("APPWRITE_ENDPOINT");
        String project = EnvLoader.get("APPWRITE_PROJECT_ID");
        String key = EnvLoader.get("APPWRITE_API_KEY");

        if (project.contains("your_actual") || key.contains("your_actual")) {
             System.err.println("WARNING: Using placeholder credentials in .env. Please update them with your Appwrite project details.");
        }

        return new Request.Builder()
                .url(endpoint + path)
                .addHeader("X-Appwrite-Project", project)
                .addHeader("X-Appwrite-Key", key)
                .addHeader("Content-Type", "application/json");
    }

    public static String getDatabaseId() {
        return EnvLoader.get("APPWRITE_DATABASE_ID");
    }

    public static String getTableId() {
        return EnvLoader.get("APPWRITE_TABLE_ID");
    }
}
