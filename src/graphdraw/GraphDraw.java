package graphdraw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author havra
*/
public class GraphDraw extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        ((FXMLDocumentController)loader.getController()).setStage(stage);
        stage.setTitle("GraphDrawing");
	stage.getIcons().add(new Image("icon.png"));
        stage.setScene(scene);
        stage.show();
	stage.setMinHeight(626);
	stage.setMinWidth(806);
        stage.setResizable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
