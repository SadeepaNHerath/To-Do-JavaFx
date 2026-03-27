package edu.iCET.controller;

import com.jfoenix.controls.JFXButton;
import edu.iCET.model.Task;
import edu.iCET.service.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewHistoryController implements Initializable {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private TableColumn<Task, String> colId;

    @FXML
    private TableColumn<Task, String> colTitle;

    @FXML
    private TableColumn<Task, String> colStatus;

    @FXML
    private TableColumn<Task, String> colDate;

    @FXML
    private TableView<Task> tblTask;

    @FXML
    private DatePicker dtpck;

    private static Stage stage;

    @FXML
    void btnCloseOnAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    void btnMinimizeOnAction(ActionEvent event) {
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadTable();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("completionDate"));
    }

    private void loadTable() {
        new Thread(() -> {
            java.util.List<Task> tasks = TaskService.getInstance().getDoneTasks();
            javafx.application.Platform.runLater(() -> {
                ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);
                tblTask.setItems(taskObservableList);
            });
        }).start();
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws Exception {
        changeScene("/view/dash_form.fxml");
    }

    @FXML
    void btnSearchOnAction(ActionEvent actionEvent) {
        if (dtpck.getValue() != null) {
            btnSearch.setDisable(true);
            new Thread(() -> {
                java.util.List<Task> tasks = TaskService.getInstance().searchDoneTasksByDate(dtpck.getValue());
                javafx.application.Platform.runLater(() -> {
                    btnSearch.setDisable(false);
                    ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);
                    tblTask.setItems(taskObservableList);
                });
            }).start();
        } else {
            loadTable();
        }
    }

    public void changeScene(String fxmlFile) throws Exception {
        Parent pane = FXMLLoader.load(getClass().getResource(fxmlFile));
        stage.getScene().setRoot(pane);
    }

    public static void setStage(Stage stage) {
        ViewHistoryController.stage = stage;
    }
}
