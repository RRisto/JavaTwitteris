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
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.InputMismatchException;

public class Controller {
    String failinimi;

    @FXML
    private Label failiNimiLabel;

    @FXML
    private Label olekLabel;

    @FXML
    private Button otsiNupp;

    @FXML
    private Button valiFailNupp;

    @FXML
    private ListView<String> tweetideList;

    @FXML
    private ImageView sõnapilv;

    @FXML
    private TextField otsisõnaVäli;

    @FXML
    private TextField soovitudArvVäli;

    @FXML
    private void initialize() {
        soovitudArvVäli.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {

                try {
                    setNewPäring();
                } catch (IOException e) {
                    olekLabel.setText("Juhtus viga: " + e.getMessage());
                }
            }
        });

        valiFailNupp.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    setNewPäring();
                } catch (IOException e) {
                    olekLabel.setText("Juhtus viga, kontakteeru adminniga");
                }
            }
        });
    }

    @FXML
    protected void locateFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(new Stage());
        failinimi = file.getName();
        failiNimiLabel.setText("Valitud fail: \n" + failinimi);
    }

    @FXML
    private void setNewPäring() throws IOException {

        Integer soovitudArv = 0;
        if (failinimi != null) {
            if (!failinimi.endsWith(".txt")) {
                olekLabel.setText("Faililaiend on vale, peab olema .txt");
                return;
            }
        }

        try {
            String otsisona = otsisõnaVäli.getCharacters().toString();
            if (otsisona.length() < 2) {
                throw new InputMismatchException("Otsisõna peab olema vähemalt 2 tähemärki pikk");
            }
            soovitudArv = Integer.parseInt(soovitudArvVäli.getCharacters().toString());
            if (soovitudArv <= 0) {
                throw new InputMismatchException("tweetide arv peab olema positiivne täisarv");
            }

            Päring päring = new Päring(otsisona, soovitudArv);
            päring.päring();

            if (päring.getTweetideArv() == 0) {
                olekLabel.setText("Leidsin 0 säutsu\nProovi mõnda muud sõna");
            } else {
                String ajatempel = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String fail = "tweedid/" + päring.getOtsisõna() + "_" + ajatempel + ".txt";
                try {
                    päring.salvestaFaili(fail);
                } catch (Exception e) {
                    olekLabel.setText("Leidsin " + päring.getTweetideArv() + " säutsu, \nkuid ei suutnud faili salvestada");
                }
                olekLabel.setText("Leidsin " + päring.getTweetideArv() + " säutsu\nSalvestasin need faili:\n" + fail);

                if (failinimi != null) { //loeme failist tweedid
                    try {
                        päring.lisaFailist("tweedid/" + failinimi);
                    } catch (Exception e) {
                        olekLabel.setText("midagi läks viltu failist lugemisega");
                    }
                }

                ObservableList<String> tekstList = FXCollections.observableArrayList(päring.getTekst());
                tweetideList.setItems(tekstList);

                Sõnapilv pilv = new Sõnapilv();

                pilv.teeSõnapilv(päring.puhastaTekst(päring.getOtsisõna()));
                File file = new File("sõnapilv.png");
                Image image = new Image(file.toURI().toString(), 500, 500, false, false);
                sõnapilv.setImage(image);
                sõnapilv.getImage();
            }
        } catch (InputMismatchException | NumberFormatException exception) {
            olekLabel.setText(exception.getMessage());
        }
    }

}
