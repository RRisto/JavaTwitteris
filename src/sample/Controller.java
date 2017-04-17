package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;

public class Controller {
    @FXML
    private Label myLabel;

    @FXML
    private Label olekLabel;

    @FXML
    private Button myButton;

    @FXML
    private Label tweedidLabel;

    @FXML
    private ImageView sõnapilv;

//    @FXML
//    private void setNewText(){
//        myLabel.setText("Pelmo "+(int)Math.round(Math.random()*100+0));
//    }

    @FXML
    private TextField mytextField;

    @FXML
    private TextField mytextField2;

//    @FXML
//    private void setNewText(int arv) {
//        olek.setText("Leidsin "+arv+" säutsu");
//
//    }

    @FXML
    private void setNewPäring() {
        Päring päring=new Päring(mytextField.getCharacters().toString(), Integer.parseInt(mytextField2.getCharacters().toString()));
        päring.päring();
//        myLabel.setText(päring.getTekst());
        olekLabel.setText("Leidsin "+päring.getTweetideArv()+" säutsu");
        tweedidLabel.setText(päring.prindiTweedid(3));


    }

    public static class Main extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("sample/sample.fxml"));
            primaryStage.setTitle("Hello World");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        }


        public static void main(String[] args) {
            launch(args);
        }
    }
}
