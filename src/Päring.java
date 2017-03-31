import twitter4j.*;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Päring {
    static ArrayList<Tweet> tweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada
    static ArrayList<Status> toorTweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada
    static String otsisõna;
    static int tweetideArv;//tweetide arv, mida kasutaja tahaks päringuga saada
    static String failinimi;

    Twitter twitter = TwitterFactory.getSingleton();

    public ArrayList<Tweet> päring() {
        Query query = new Query(this.otsisõna);
        Long lastID = Long.MAX_VALUE;
        ArrayList<Status> tweets = new ArrayList<>();//siia toorandmed JSONis
        //pärib tweedid, max päringuga 100, kui jääb puudu otsib juurde tweete
        while (tweets.size() < this.tweetideArv) {
            if (this.tweetideArv - tweets.size() > 100) {
                query.setCount(100);//korraga üle 100 ei saa
            } else {
                query.setCount(this.tweetideArv - tweets.size());
            } try { //try aitab exceptioneid kinni püüda
                QueryResult result = twitter.search(query);
                tweets.addAll(result.getTweets());
                System.out.println("Kogusin " + tweets.size() + " tweeti");

                for (Status tweet : tweets) {
                    //paneb vajaliku info kodukootud klassi
                    tweedid.add(new Tweet(tweet.getUser().getScreenName(), tweet.getText(), tweet.getId()));
                    toorTweedid.add(tweet);
                    if (tweet.getId() < lastID) lastID = tweet.getId();
                }
            } catch (TwitterException te) {
                System.out.println("Ei suutnud ühenduda: " + te);
            }
            query.setMaxId(lastID - 1);//muudab tweedi max id ära, et leida eelnevaid tweete
        }
        return tweedid;
    }

    public void salvestaFaili() throws Exception {
        //salvestame faili
        java.io.File fail = new java.io.File(this.failinimi);
        //java.io.PrintWriter pw = new java.io.PrintWriter(fail, "UTF-8", true); //kirjutab faili üle
        java.io.PrintWriter pw = new java.io.PrintWriter(new FileOutputStream(fail, true)); //kirjutab faili lõppu juurde

        for (Tweet tweet : tweedid) {
            pw.print(tweet.getNimi().replaceAll("[\n]",""));//peab puhastama, muidu paneb faili tühje ridu, mis
            //sisselugemisel hakkab exceptioneid andma
            pw.print("; ");
            pw.print(tweet.getTekst().replaceAll("[\n]",""));
            pw.print("; ");
            pw.print(tweet.getStatusId());
            pw.print("\n");
        }
        pw.close();
    }

    public ArrayList<Tweet> loeFailist() throws FileNotFoundException {
        ArrayList<Tweet> sisseloetudtweedid=new ArrayList<>();
        java.io.File fail = new java.io.File(this.failinimi);
        java.util.Scanner sc = new java.util.Scanner(fail, "UTF-8");
        while (sc.hasNextLine()) {
            String rida = sc.nextLine();
            String[] tükid = rida.split("; ");
            sisseloetudtweedid.add(new Tweet(tükid[0], tükid[1], Long.parseLong(tükid[2])));
    }
    return sisseloetudtweedid;
    }

    public boolean failOlemas(){
        File fail=new File(this.failinimi);
        return fail.exists();
    }

    public void kysiOtsisõna(String tekst, String title){
        String otsisõna=JOptionPane.showInputDialog(null, tekst, title,
                JOptionPane.QUESTION_MESSAGE);
        this.otsisõna=otsisõna;
    }

    public void kysiTweetideArv(String tekst, String title){
        int tweetideArv=Integer.parseInt(JOptionPane.showInputDialog(null, tekst, title,
                JOptionPane.QUESTION_MESSAGE));
        this.tweetideArv=tweetideArv;
    }

    public void kysiFailinimi(String tekst, String title){
        String failinimi=JOptionPane.showInputDialog(null, tekst, title,
                JOptionPane.QUESTION_MESSAGE);
        this.failinimi=failinimi;
    }

    public static String getOtsisõna() {
        return otsisõna;
    }

    public static int getTweetideArv() {
        return tweetideArv;
    }

    public static String getFailinimi() {
        return failinimi;
    }

    public ArrayList<Tweet> kysiFailistLugemist() throws FileNotFoundException {
        ArrayList<Tweet> failistLoetud = this.tweedid;
        String vastus = JOptionPane.showInputDialog(null,
                "Kas loen eelnevalt salvestatud andmed failist '" + getFailinimi() + "'? (jah/ei)",
                "Andmed failist", JOptionPane.QUESTION_MESSAGE);

        while (!((vastus.equalsIgnoreCase("ei")) || (vastus.equalsIgnoreCase("jah")))) {
            vastus = JOptionPane.showInputDialog(null,
                    "Vale sisend. Kas loen eelnevalt salvestatud andmed failist " + getFailinimi() + "? (jah/ei)",
                    "Andmete sisestamine",
                    JOptionPane.QUESTION_MESSAGE);
        }
        if (vastus.equalsIgnoreCase("jah")) {
            failistLoetud = loeFailist();
            System.out.println("Failist " + getFailinimi() + " loeti " + failistLoetud.size() + " tweeti");
        }
        return failistLoetud;
    }

    public String getTekst() {
        StringBuilder tekst =new StringBuilder();
        for (Tweet tweet : tweedid) {
            tekst.append(tweet);
            tekst.append(' ');
        }
        return tekst.toString();
    }

}
