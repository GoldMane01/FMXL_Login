package com.mycompany.fxmllogin;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import models.Tarea;
import models.Usuario;
import org.hibernate.Session;

public class SecondaryController implements Initializable {

    @FXML
    private Label label1;
    @FXML
    private TableView<Tarea> tabla;
    @FXML
    private TableColumn<Tarea, String> colId;
    @FXML
    private TableColumn<Tarea, String> colTarea;
    @FXML
    private TableColumn<Tarea, String> colPrioridad;
    @FXML
    private Label label2;
    @FXML
    private Button btnSalir;
    @FXML
    private Button btnNueva;
    @FXML
    private MenuItem menuNueva;
    @FXML
    private MenuItem menuSalir;
    @FXML
    private MenuItem menuAcerca;
    @FXML
    private Label hora;

    @FXML
    private void switchToPrimary() throws IOException {
        SessionData.setUsuarioActual(null);
        App.setRoot("primary");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<Tarea> contenido = FXCollections.observableArrayList();
        tabla.setItems(contenido);

        //colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTarea.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));

        colId.setCellValueFactory(
                (var t) -> {
                    String salida = "["+t.getValue().getId()+"]";
                    return new SimpleStringProperty(salida);
                }
        );

        //contenido.add( new Tarea(0L,"Hacer cosas","alta", new Usuario() ));
        Session s = HibernateUtil.getSessionFactory().openSession();
        Usuario u = s.load(Usuario.class, SessionData.getUsuarioActual().getId());
        SessionData.setUsuarioActual(u);

        contenido.addAll(u.getTareas());

        label1.setText(u.getUsername() + " " + u.getEmail());
        label2.setText(u.getTareas().size() + " tareas");

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Date fecha = new Date();
                    hora.setText(fecha.toString());
                    Session s = HibernateUtil.getSessionFactory().openSession();
                    Usuario u = s.load(Usuario.class, SessionData.getUsuarioActual().getId());
                    SessionData.setUsuarioActual(u);
                    contenido.clear();
                    contenido.addAll(u.getTareas());
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    @FXML
    private void seleccionar(MouseEvent event) {
        Tarea t = tabla.getSelectionModel().getSelectedItem();
        if (t != null) {
//           Alert alert = new Alert(Alert.AlertType.INFORMATION);
//           alert.setHeaderText( t.getNombre() );
//           alert.setContentText( t.getPrioridad());
//           alert.showAndWait();
            SessionData.setTareaActual(t);
            try {
                App.setRoot("editorTarea");
            } catch (IOException ex) {
                Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void nueva(ActionEvent event) {

        SessionData.setTareaActual(null);
        try {
            App.setRoot("editorTarea");
        } catch (IOException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void acerca(ActionEvent event) {

        Alert info = new Alert(AlertType.INFORMATION);
        info.setContentText("Programa realizado con dos huevos");
        info.showAndWait();

    }

}
