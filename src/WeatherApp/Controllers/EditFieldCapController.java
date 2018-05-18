package WeatherApp.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import WeatherApp.model.Field;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

public class EditFieldCapController implements Initializable{
	
	@FXML private Button mUp;
	@FXML private Button mDown;
	@FXML private Button deleteButton;
	@FXML private Button editImageButton;
	@FXML private TextField defName;
	//the field that this edit panel corresponds to
	private Field field;
	//the current list of fields in the dashboard
	private List<Field> fList;
	//the main Controller so we can call methods from it
	private Controller parent;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		defName.setText("Oops....");
	}
	
	public void passlist(List<Field> fList) {
		this.fList = fList;
		movementbuttons();
	}
	
	public void deleteClicked() throws IOException {
		//first check are you sure with a popup alert:
		Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + field.getName() + "?", ButtonType.YES, ButtonType.CANCEL);
		alert.showAndWait();
		if(alert.getResult() == ButtonType.YES) {
			//go ahead and delete the field
			try {
				//remove the field from the list
				fList.remove(field);
				//update the displayed fields
				parent.editupdate();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if(alert.getResult() == ButtonType.CANCEL) {
			//delete cancelled: close the alert
			alert.close();
		}
	}
	
	public void moveUp() throws IOException {
		int currentindex = fList.indexOf(field);
		if(currentindex != 0) {
			Collections.swap(fList, currentindex, currentindex-1);
		}
		parent.editupdate();
	}
	
	public void moveDown() throws IOException {
		int currentindex = fList.indexOf(field);
		if(currentindex < fList.size()-1) {
			Collections.swap(fList, currentindex, currentindex+1);
		}
		parent.editupdate();
	}
	
	public void imageClicked() {
		//TODO: image editing
	}
	
	public void nameEdit() {
		field.setName(defName.getText());
	}
	
	public void setField(Field field) {
		this.field = field;
		this.defName.setText(field.getName());
		movementbuttons();
	}

	public void passparent(Controller controller) {
		parent = controller;
		
	}
	
	private void movementbuttons() {
		//sets up and down arrows invisible if edit panel cannot be moved in that direction i.e. at top or bottom of scrollpane
		//must be done after field and flist are set
		if(fList != null && field != null) {
			if(fList.indexOf(field) == 0) {
				mUp.setVisible(false);
			}
			else if(fList.indexOf(field) == fList.size()-1) {
				mDown.setVisible(false);
			}
		}	
	}

}
