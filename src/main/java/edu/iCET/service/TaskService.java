package edu.iCET.service;

import edu.iCET.db.DBConnection;
import edu.iCET.model.Task;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    private static TaskService instance;

    private TaskService() {}

    public static TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }
        return instance;
    }

    public boolean addTask(String title) throws SQLException {
        String sql = "INSERT INTO Tasks (task_title, task_status, completion_date) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, title);
            pstm.setString(2, "Active");
            pstm.setObject(3, LocalDate.now());
            return pstm.executeUpdate() > 0;
        }
    }

    public List<String> getTasksByStatus(String status) throws SQLException {
        List<String> tasks = new ArrayList<>();
        String sql = "SELECT task_title FROM Tasks WHERE task_status = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, status);
            try (ResultSet resultSet = pstm.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(resultSet.getString("task_title"));
                }
            }
        }
        return tasks;
    }

    public boolean markTaskAsDone(String title) throws SQLException {
        String sql = "UPDATE Tasks SET task_status = 'Done', completion_date = ? WHERE task_title = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setObject(1, LocalDate.now());
            pstm.setString(2, title);
            return pstm.executeUpdate() > 0;
        }
    }

    public List<Task> getDoneTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM Tasks WHERE task_status = 'Done'";
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                tasks.add(new Task(
                        resultSet.getInt("task_id"),
                        resultSet.getString("task_title"),
                        resultSet.getString("task_status"),
                        resultSet.getDate("completion_date").toLocalDate()
                ));
            }
        }
        return tasks;
    }

    public List<Task> searchDoneTasksByDate(LocalDate date) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM Tasks WHERE task_status = 'Done' AND completion_date = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setObject(1, date);
            try (ResultSet resultSet = pstm.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(new Task(
                            resultSet.getInt("task_id"),
                            resultSet.getString("task_title"),
                            resultSet.getString("task_status"),
                            resultSet.getDate("completion_date").toLocalDate()
                    ));
                }
            }
        }
        return tasks;
    }
}
