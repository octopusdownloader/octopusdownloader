package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {


    public void newDownloadDialog() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("new-download-dialog.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("New Download");
            stage.setScene(new Scene(root1));
            stage.show();
            }catch (Exception e){
            System.out.println("can't load the window");
            }

    }
}
