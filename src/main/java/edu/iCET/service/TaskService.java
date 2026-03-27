package edu.iCET.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.iCET.db.AppwriteClient;
import edu.iCET.model.Task;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    private static TaskService instance;
    private final Gson gson = new Gson();
    private boolean isInitialized = false;

    private TaskService() {}

    public static TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }
        return instance;
    }

    private synchronized void ensureInitialized() {
        if (isInitialized) return;
        
        String tableUrl = String.format("/databases/%s/tables/%s",
                AppwriteClient.getDatabaseId(), AppwriteClient.getTableId());

        Request checkRequest = AppwriteClient.getRequestBuilder(tableUrl).get().build();
        try (Response response = AppwriteClient.getClient().newCall(checkRequest).execute()) {
            if (response.code() == 404) {
                createTableAndColumns();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        isInitialized = true;
    }

    private void createTableAndColumns() {
        String baseUrl = String.format("/databases/%s/tables", AppwriteClient.getDatabaseId());
        
        // 1. Create Table
        JsonObject tableBody = new JsonObject();
        tableBody.addProperty("tableId", AppwriteClient.getTableId());
        tableBody.addProperty("name", "TasksTable");
        
        // Add default permissions (Public for simplicity in this project)
        JsonArray permissions = new JsonArray();
        permissions.add("read(\"any\")");
        permissions.add("create(\"any\")");
        permissions.add("update(\"any\")");
        permissions.add("delete(\"any\")");
        tableBody.add("permissions", permissions);

        Request createTableReq = AppwriteClient.getRequestBuilder(baseUrl)
                .post(RequestBody.create(tableBody.toString(), MediaType.get("application/json")))
                .build();

        try (Response res = AppwriteClient.getClient().newCall(createTableReq).execute()) {
            if (res.isSuccessful()) {
                System.out.println("Cloud Table Created Successfully.");
                // Give Appwrite a small moment to process table creation
                Thread.sleep(2000);
                
                // 2. Create Columns
                createStringColumn("task_title", 255);
                createStringColumn("task_status", 50);
                createStringColumn("completion_date", 50);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createStringColumn(String key, int size) {
        String url = String.format("/databases/%s/tables/%s/columns/string",
                AppwriteClient.getDatabaseId(), AppwriteClient.getTableId());

        JsonObject body = new JsonObject();
        body.addProperty("key", key);
        body.addProperty("size", size);
        body.addProperty("required", true);

        Request req = AppwriteClient.getRequestBuilder(url)
                .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .build();

        try (Response res = AppwriteClient.getClient().newCall(req).execute()) {
            if (res.isSuccessful()) {
                System.out.println("Column Created: " + key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addTask(String title) {
        ensureInitialized();
        String url = String.format("/databases/%s/tables/%s/rows",
                AppwriteClient.getDatabaseId(), AppwriteClient.getTableId());

        JsonObject data = new JsonObject();
        data.addProperty("task_title", title);
        data.addProperty("task_status", "Active");
        data.addProperty("completion_date", LocalDate.now().toString());

        JsonObject body = new JsonObject();
        body.addProperty("rowId", "unique()");
        body.add("data", data);

        Request request = AppwriteClient.getRequestBuilder(url)
                .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .build();

        try (Response response = AppwriteClient.getClient().newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> getTasksByStatus(String status) {
        ensureInitialized();
        List<String> tasks = new ArrayList<>();
        String url = String.format("/databases/%s/tables/%s/rows?queries[]=equal(\"task_status\", [\"%s\"])",
                AppwriteClient.getDatabaseId(), AppwriteClient.getTableId(), status);

        Request request = AppwriteClient.getRequestBuilder(url).get().build();

        try (Response response = AppwriteClient.getClient().newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                JsonArray rows = jsonResponse.getAsJsonArray("rows");
                if (rows == null) rows = jsonResponse.getAsJsonArray("documents");
                
                if (rows != null) {
                    for (JsonElement row : rows) {
                        tasks.add(row.getAsJsonObject().get("task_title").getAsString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public boolean markTaskAsDone(String title) {
        ensureInitialized();
        String rowId = findRowIdByTitle(title);
        if (rowId != null) {
            String urlUpdate = String.format("/databases/%s/tables/%s/rows/%s",
                    AppwriteClient.getDatabaseId(), AppwriteClient.getTableId(), rowId);

            JsonObject data = new JsonObject();
            data.addProperty("task_status", "Done");
            data.addProperty("completion_date", LocalDate.now().toString());

            JsonObject body = new JsonObject();
            body.add("data", data);

            Request updateRequest = AppwriteClient.getRequestBuilder(urlUpdate)
                    .patch(RequestBody.create(body.toString(), MediaType.get("application/json")))
                    .build();

            try (Response updateResponse = AppwriteClient.getClient().newCall(updateRequest).execute()) {
                return updateResponse.isSuccessful();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteTask(String title) {
        ensureInitialized();
        String rowId = findRowIdByTitle(title);
        if (rowId != null) {
            String url = String.format("/databases/%s/tables/%s/rows/%s",
                    AppwriteClient.getDatabaseId(), AppwriteClient.getTableId(), rowId);

            Request request = AppwriteClient.getRequestBuilder(url).delete().build();

            try (Response response = AppwriteClient.getClient().newCall(request).execute()) {
                return response.isSuccessful();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String findRowIdByTitle(String title) {
        String url = String.format("/databases/%s/tables/%s/rows?queries[]=equal(\"task_title\", [\"%s\"])",
                AppwriteClient.getDatabaseId(), AppwriteClient.getTableId(), title);

        Request findRequest = AppwriteClient.getRequestBuilder(url).get().build();

        try (Response findResponse = AppwriteClient.getClient().newCall(findRequest).execute()) {
            if (findResponse.isSuccessful() && findResponse.body() != null) {
                JsonObject jsonResponse = gson.fromJson(findResponse.body().string(), JsonObject.class);
                JsonArray rows = jsonResponse.getAsJsonArray("rows");
                if (rows == null) rows = jsonResponse.getAsJsonArray("documents");

                if (rows != null && rows.size() > 0) {
                    JsonObject first = rows.get(0).getAsJsonObject();
                    return first.has("$id") ? first.get("$id").getAsString() : first.get("id").getAsString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Task> getDoneTasks() {
        return fetchTasks("queries[]=equal(\"task_status\", [\"Done\"])");
    }

    public List<Task> searchDoneTasksByDate(LocalDate date) {
        return fetchTasks(String.format("queries[]=equal(\"task_status\", [\"Done\"])&queries[]=equal(\"completion_date\", [\"%s\"])", date.toString()));
    }

    private List<Task> fetchTasks(String queryPart) {
        ensureInitialized();
        List<Task> tasks = new ArrayList<>();
        String url = String.format("/databases/%s/tables/%s/rows?%s",
                AppwriteClient.getDatabaseId(), AppwriteClient.getTableId(), queryPart);

        Request request = AppwriteClient.getRequestBuilder(url).get().build();

        try (Response response = AppwriteClient.getClient().newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                JsonArray rows = jsonResponse.getAsJsonArray("rows");
                if (rows == null) rows = jsonResponse.getAsJsonArray("documents");

                if (rows != null) {
                    for (JsonElement row : rows) {
                        JsonObject d = row.getAsJsonObject();
                        String id = d.has("$id") ? d.get("$id").getAsString() : d.get("id").getAsString();
                        tasks.add(new Task(
                                id,
                                d.get("task_title").getAsString(),
                                d.get("task_status").getAsString(),
                                LocalDate.parse(d.get("completion_date").getAsString())
                        ));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
