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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.InputMismatchException;

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

    @FXML
    private void setNewPäring() throws IOException {
        Integer soovitudArv=0;
        try {
            soovitudArv = Integer.parseInt(soovitudArvVäli.getCharacters().toString());
            if (soovitudArv <= 0) {
                throw new InputMismatchException();
            }

            Päring päring = new Päring(otsisõnaVäli.getCharacters().toString(), soovitudArv);
            päring.päring();

            if (päring.getTweetideArv() == 0) {
                olekLabel.setText("Leidsin 0 säutsu\nProovi mõnda muud sõna");
            } else {
                String ajatempel = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String failinimi=päring.getOtsisõna()+"_"+ajatempel+".txt";
                try {
                    päring.salvestaFaili(failinimi);
                } catch (Exception e) {
                    olekLabel.setText("Leidsin " + päring.getTweetideArv() + " säutsu, \nkuid ei suutnud faili salvestada");
                }
                olekLabel.setText("Leidsin " + päring.getTweetideArv() + " säutsu\nSalvestasin need faili:\n"+failinimi);

                ObservableList<String> tekstList = FXCollections.observableArrayList(päring.getTekst());
                tweetideList.setItems(tekstList);

                Sõnapilv pilv = new Sõnapilv();
                pilv.teeSõnapilv(päring.puhastaTekst(päring.getOtsisõna()));
                File file = new File("sõnapilv.png");
                Image image = new Image(file.toURI().toString(), 500, 500, false, false);
                sõnapilv.setImage(image);
                sõnapilv.getImage();
            }
        } catch (InputMismatchException|NumberFormatException exception) {
            olekLabel.setText("Tweetide arv peab olema positiivne täisarv");
        }

    }

}
