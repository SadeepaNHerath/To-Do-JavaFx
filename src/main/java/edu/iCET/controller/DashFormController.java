package edu.iCET.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import edu.iCET.service.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class DashFormController implements Initializable {


    @FXML
    private JFXListView<String> lstToDo;

    @FXML
    private JFXTextField txtTask;

    private static Stage stage;

    @FXML
    void btnCloseOnAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    void btnMinimizeOnAction(ActionEvent event) {
        stage.setIconified(true);
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String taskText = txtTask.getText();
        if (taskText != null && !taskText.trim().isEmpty()) {
            txtTask.setDisable(true);
            new Thread(() -> {
                boolean success = TaskService.getInstance().addTask(taskText.trim());
                javafx.application.Platform.runLater(() -> {
                    txtTask.setDisable(false);
                    if (success) {
                        txtTask.setText("");
                        refreshLists();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to connect to Cloud Database. Please check your .env settings and network.").show();
                    }
                });
            }).start();
        }
    }

    @FXML
    void btnViewHistoryOnAction(ActionEvent event) throws Exception {
        changeScene("/view/view_history_form.fxml");
    }

    private void refreshLists() {
        setTodoList();
    }

    private void setTodoList() {
        new Thread(() -> {
            java.util.List<String> tasks = TaskService.getInstance().getTasksByStatus("Active");
            javafx.application.Platform.runLater(() -> {
                ObservableList<String> todoItems = FXCollections.observableArrayList(tasks);
                lstToDo.setItems(todoItems);
            });
        }).start();

        lstToDo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    private final HBox hbox = new HBox();
                    private final Text text = new Text();
                    private final CheckBox checkBox = new CheckBox();
                    private final JFXButton btnDelete = new JFXButton("✕");

                    {
                        btnDelete.setStyle("-fx-text-fill: #ff4757; -fx-font-weight: bold; -fx-cursor: hand;");
                        hbox.getChildren().addAll(checkBox, text, new javafx.scene.layout.Region(), btnDelete);
                        HBox.setHgrow(text, javafx.scene.layout.Priority.ALWAYS);
                        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                        hbox.setSpacing(15);
                        hbox.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));
                        
                        checkBox.setOnAction(event -> {
                            if (checkBox.isSelected()) {
                                checkBox.setDisable(true);
                                new Thread(() -> {
                                    boolean success = TaskService.getInstance().markTaskAsDone(text.getText());
                                    javafx.application.Platform.runLater(() -> {
                                        if (success) {
                                            refreshLists();
                                        } else {
                                            checkBox.setDisable(false);
                                            checkBox.setSelected(false);
                                            new Alert(Alert.AlertType.ERROR, "Failed to update task status.").show();
                                        }
                                    });
                                }).start();
                            }
                        });

                        btnDelete.setOnAction(event -> {
                            btnDelete.setDisable(true);
                            new Thread(() -> {
                                boolean success = TaskService.getInstance().deleteTask(text.getText());
                                javafx.application.Platform.runLater(() -> {
                                    if (success) {
                                        refreshLists();
                                    } else {
                                        btnDelete.setDisable(false);
                                        new Alert(Alert.AlertType.ERROR, "Failed to delete task.").show();
                                    }
                                });
                            }).start();
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            text.setText(item);
                            text.setStyle("-fx-fill: white; -fx-font-size: 14px;");
                            checkBox.setSelected(false);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }


    public void changeScene(String fxmlFile) throws Exception {
        Parent pane = FXMLLoader.load(getClass().getResource(fxmlFile));
        stage.getScene().setRoot(pane);
    }

    public static void setStage(Stage stage) {
        DashFormController.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshLists();
    }
}
