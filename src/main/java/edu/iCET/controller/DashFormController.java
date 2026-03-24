package edu.iCET.controller;

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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashFormController implements Initializable {

    @FXML
    private JFXListView<String> lstDone;

    @FXML
    private JFXListView<String> lstToDo;

    @FXML
    private JFXTextField txtTask;

    private static Stage stage;

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String taskText = txtTask.getText();
        if (taskText != null && !taskText.trim().isEmpty()) {
            try {
                if (TaskService.getInstance().addTask(taskText.trim())) {
                    new Alert(Alert.AlertType.INFORMATION, "Task Added!").show();
                }
                txtTask.setText("");
                refreshLists();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnViewHistoryOnAction(ActionEvent event) throws Exception {
        changeScene("/view/view_history_form.fxml");
    }

    private void refreshLists() {
        setTodoList();
        setDoneList();
    }

    private void setTodoList() {
        try {
            ObservableList<String> todoItems = FXCollections.observableArrayList(
                    TaskService.getInstance().getTasksByStatus("Active")
            );
            lstToDo.setItems(todoItems);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lstToDo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    private final HBox hbox = new HBox();
                    private final Text text = new Text();
                    private final CheckBox checkBox = new CheckBox();

                    {
                        hbox.getChildren().addAll(text, checkBox);
                        hbox.setSpacing(10);
                        checkBox.setOnAction(event -> {
                            if (checkBox.isSelected()) {
                                try {
                                    if (TaskService.getInstance().markTaskAsDone(text.getText())) {
                                        new Alert(Alert.AlertType.INFORMATION, "Task Done!").show();
                                    }
                                    refreshLists();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            text.setText(item);
                            checkBox.setSelected(false);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void setDoneList() {
        try {
            ObservableList<String> doneItems = FXCollections.observableArrayList(
                    TaskService.getInstance().getTasksByStatus("Done")
            );
            lstDone.setItems(doneItems);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
