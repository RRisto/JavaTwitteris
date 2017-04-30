package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    private Label myLabel;

    @FXML
    private Label olekLabel;

    @FXML
    private Button otsiNupp;

    @FXML
    private ListView<String> tweetideList;

    @FXML
    private ImageView sõnapilv;

    @FXML
    private TextField otsisõnaVäli;

    @FXML
    private TextField soovitudArvVäli;

//    @FXML
//    private void initialize() {
//        otsiNupp.setOnKeyPressed(event -> {
//            if (event.getCode().equals(KeyCode.ENTER)) {
//                System.out.println("mina");
//                try {
//                    setNewPäring();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    @FXML
    private void setNewPäring() throws IOException {
        Päring päring = new Päring(otsisõnaVäli.getCharacters().toString(), Integer.parseInt(soovitudArvVäli.getCharacters().toString()));
        päring.päring();

        if (päring.getTweetideArv() == 0) {
            olekLabel.setText("Leidsin 0 säutsu\n proovi mõnda muud sõna või proovi hiljem uuesti");
        } else {
            olekLabel.setText("Leidsin " + päring.getTweetideArv() + " säutsu");

            ObservableList<String> tekstList = FXCollections.observableArrayList(päring.getTekst());
            tweetideList.setItems(tekstList);

            Sõnapilv pilv = new Sõnapilv();
            pilv.teeSõnapilv(päring.puhastaTekst(päring.getOtsisõna()));
            File file = new File("sõnapilv.png");
            Image image = new Image(file.toURI().toString(), 500,500, false,false);
            sõnapilv.setImage(image);
            sõnapilv.getImage();
        }
    }

}
