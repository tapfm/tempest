package WeatherApp.Controllers;

import WeatherApp.model.Field;
import WeatherApp.service.FieldStore;
import WeatherApp.service.SettingsStore;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable{
    @FXML private Button editButton;
    @FXML private Button settingsButton;
    @FXML private Button addButton;
    @FXML private VBox dbContent = new VBox();
    @FXML private ScrollPane dashboardList = new ScrollPane(dbContent);
    private List<Field> fList;
    private FieldStore store;
    private boolean editmode = false;

    /*
    initialize function called when dashboard is created, loads all of the fields from the store
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        store = new FieldStore(Paths.get("stores/fieldStore.json"));
        try {
            updatelist(store.getFields());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Function switch between edit mode and normal mode when edit button is clicked
     */
    @FXML
    private void editClicked() throws IOException{
        editmode = !editmode;
        if(editmode) {
            //switch the scrollpane to contain the editfieldcapsules
            editButton.setText("Done");
            addButton.setVisible(false);
            editupdate();
        }
        else {
            editButton.setText("Edit");
            addButton.setVisible(true);
            //switch the scrollpane to contain the fieldcapsules again
            //send the flist off to the fieldstore

            //clear dbContent

            store.setFields(fList);
            store.save();

            //update the scrollpane with the new edited fieldlist
            updatelist(fList);
        }
    }


    /*
    settingsClicked loads the settings page - triggered by the settings button
     */
    @FXML
    private void settingsClicked() throws IOException {
        Stage stage = (Stage)addButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/GeneralSettings.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /*
    addClicked loads the add field page - triggered by the Add field button
     */
    @FXML
    private void addClicked() throws IOException{
        Stage stage = (Stage)addButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/fieldPositionChoice.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /*
    Function that adds a farm to the list displayed
     */
    public void addFarm(Field field) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/FieldCapsuleTemplate.fxml"));
        Node fieldCapNode = loader.load();

        dbContent.getChildren().add(fieldCapNode);
        dashboardList.setContent(dbContent);

        FieldCapController capController = loader.<FieldCapController>getController();
        capController.setField(field);
    }

    /*
    Function that updates the display with each of the fields
     */
    private void updatelist(List<Field> fieldlist) throws IOException {
        //updates the dashboard's field list by reading through the inputed fieldlist
        fList = fieldlist;

        SettingsStore settingsStore = new SettingsStore(Paths.get("stores/generalSettingsStore.json"));

        dbContent = new VBox();
        // for each field load the template and create the node for the field capsule
        for(Field field: fieldlist) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/FieldCapsuleTemplate.fxml"));
            Node fieldCapNode = loader.load();

            // add click event handler
            fieldCapNode.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        /*
                        on click load the more info page
                         */
                        Stage stage = (Stage) editButton.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/moreInfo.fxml"));
                        Parent root = loader.load();
                        Scene scene = new Scene(root);

                        MoreInfoController moreInfoController = loader.<MoreInfoController>getController();
                        moreInfoController.setField(field);

                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            // add the capsule to dashboard content
            dbContent.getChildren().add(fieldCapNode);

            FieldCapController capController = loader.<FieldCapController>getController();
            capController.setSettingsStore(settingsStore);
            capController.setField(field);
            capController.updateToCurrentWeather();
        }

        dashboardList.setContent(dbContent);
    }

    /*
    sets the fList variable
     */
    public void setfList(List<Field> fList) {
        this.fList = fList;
    }

    /*
    updates the dashboard for edit mode
     */
    public void editupdate() throws IOException {
        //updates the edit panels
        VBox dbContentNew = new VBox();

        for(Field field: fList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/editmodeCapsule.fxml"));
            Node editCapNode = loader.load();
            dbContentNew.getChildren().add(editCapNode);
            EditFieldCapController capController = loader.<EditFieldCapController>getController();
            capController.setField(field);
            capController.passlist(fList);
            capController.passparent(this);
        }
        dashboardList.setContent(dbContentNew);
    }

    /*
    removes a farm from the list
     */
    public void removeFarm(Field field) throws IOException {
        fList.remove(field);
        editupdate();
    }
}