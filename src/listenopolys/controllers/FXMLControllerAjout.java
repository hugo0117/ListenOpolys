package listenopolys.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import listenopolys.models.Playlist;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLControllerAjout implements Initializable {

    @FXML
    TextField textFieldTitle;

    FXMLController controller = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * add the playlist to the player
     */
    public void buttonAddPlaylistClicked(){
        if(controller!=null && textFieldTitle.getText()!=""){
            controller.addPlaylist(new Playlist(textFieldTitle.getText()));
        }
        Stage stage = (Stage) textFieldTitle.getScene().getWindow();
        stage.close();
    }

    /**
     * set the controller to use for adding the playlist
     * @param controller controller to use for adding the playlist
     */
    public void setController(FXMLController controller){
        this.controller = controller;
    }
}
