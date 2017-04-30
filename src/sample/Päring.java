package sample;

import twitter4j.*;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Päring {
    private ArrayList<Tweet> tweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada
    private ArrayList<Status> toorTweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada
    private String otsisõna;
    private int soovitudTweetideArv;//tweetide arv, mida kasutaja tahaks päringuga saada
    private String failinimi;

    public Päring(String otsisõna, int tweetideArv) {
        this.otsisõna = otsisõna;
        this.soovitudTweetideArv = tweetideArv;

    }

    public Päring() {
    }


    public int getTweetideArv() {
        return tweedid.size();
    }

    public String prindiTweedid() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tweedid.size(); i++) {
            sb.append(tweedid.get(i));
            sb.append(" \n");
        }
        return sb.toString();
    }


    Twitter twitter = TwitterFactory.getSingleton();

    public ArrayList<Tweet> päring() {
        Query query = new Query(this.otsisõna);
        Long lastID = Long.MAX_VALUE;//viimase tweedi id, seda vaja, kui peame tegema mitu päringut, et tweetide arv täis saada
        ArrayList<Status> tweets = new ArrayList<>();//siia toorandmed JSONis
        //pärib tweedid, max päringuga 100, kui jääb puudu otsib juurde tweete
        int erinditeArv = 0;
        while (tweets.size() < this.soovitudTweetideArv) {
            if (this.soovitudTweetideArv - tweets.size() > 100) {
                query.setCount(100);//korraga üle 100 ei saa
            } else {
                query.setCount(this.soovitudTweetideArv - tweets.size());
            }
            try { //try aitab exceptioneid kinni püüda
                QueryResult result = twitter.search(query);
                //tweetid sai otsa või polnud ühtegi
                if (result.getTweets().size() == 0) {
                    break;
                }
                tweets.addAll(result.getTweets());
//                System.out.println("Kogusin " + tweets.size() + " tweeti");

                for (Status tweet : tweets) {
                    //paneb vajaliku info kodukootud klassi
                    tweedid.add(new Tweet(tweet.getUser().getScreenName(), tweet.getText(), tweet.getId()));
                    toorTweedid.add(tweet);
                    if (tweet.getId() < lastID) lastID = tweet.getId();
                }
            } catch (TwitterException te) { //kui twitteri poolt tuleb mingi erind, püüab kinni
                System.out.println("Ei suutnud ühenduda: " + te);
                erinditeArv += 1;
                //kui 3 erindit on saanud päringus, siis pole kas netiühendust või on API limiit täis, lõpetame ära
                if (erinditeArv>3) {
                    break;
                }
            }
            query.setMaxId(lastID - 1);//muudab tweedi max id ära, et leida eelnevaid tweete
        }
        return tweedid;
    }

    public void salvestaFaili() throws Exception {
        //salvestame faili
        File fail = new File(this.failinimi);
        //java.io.PrintWriter pw = new java.io.PrintWriter(fail, "UTF-8", true); //kirjutab faili üle
        java.io.PrintWriter pw = new java.io.PrintWriter(new FileOutputStream(fail, true)); //kirjutab faili lõppu juurde

        for (Tweet tweet : tweedid) {
            pw.print(tweet.getNimi().replaceAll("[\n]", ""));//peab puhastama, muidu paneb faili tühje ridu, mis
            //sisselugemisel hakkab exceptioneid andma
            pw.print("; ");
            pw.print(tweet.getTekst().replaceAll("[\n]|;", ""));
            pw.print("; ");
            pw.print(tweet.getStatusId());
            pw.print("\n");
        }
        pw.close();
    }

    public void setFailinimi(String failinimi) {
        this.failinimi = failinimi;
    }

    public void salvestaPuhasTekst(String exclude) throws FileNotFoundException {
        File fail = new File(this.otsisõna + "_puhas.txt");
        //java.io.PrintWriter pw = new java.io.PrintWriter(fail, "UTF-8", true); //kirjutab faili üle
        java.io.PrintWriter pw = new java.io.PrintWriter(new FileOutputStream(fail, true));
        //puhastame teksti
        Analüüs analüüs = new Analüüs();
        StringBuilder sb = analüüs.tweet2SB(tweedid);
        String httpRegex = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
        sb = analüüs.cleanText(sb, httpRegex, "");
        String punctuationRegex = "[-.,!?:]|'\\w";
        sb = analüüs.cleanText(sb, punctuationRegex, " ");
        String stopwords = analüüs.loadStopwords() + exclude;
        System.out.println("Stopwords " + stopwords);
        String excludeRegex = analüüs.buildExcludeRegex(stopwords);

        sb = analüüs.cleanText(sb, excludeRegex, "");
        pw.print(sb.toString());
        pw.close();
    }

    public String puhastaTekst(String exclude) throws FileNotFoundException {
//        File fail = new File(this.otsisõna + "_puhas.txt");
        //java.io.PrintWriter pw = new java.io.PrintWriter(fail, "UTF-8", true); //kirjutab faili üle
//        java.io.PrintWriter pw = new java.io.PrintWriter(new FileOutputStream(fail, true));
        //puhastame teksti
        Analüüs analüüs = new Analüüs();
        StringBuilder sb = analüüs.tweet2SB(tweedid);
        String httpRegex = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
        sb = analüüs.cleanText(sb, httpRegex, "");
        String punctuationRegex = "[-.,!?:]|'\\w";
        sb = analüüs.cleanText(sb, punctuationRegex, " ");
        String stopwords = analüüs.loadStopwords() + exclude;
        System.out.println("Stopwords " + stopwords);
        String excludeRegex = analüüs.buildExcludeRegex(stopwords);

        sb = analüüs.cleanText(sb, excludeRegex, "");
        return sb.toString();
//        pw.print(sb.toString());
//        pw.close();
    }

    public ArrayList<Tweet> loeFailist() throws FileNotFoundException {
        ArrayList<Tweet> sisseloetudtweedid = new ArrayList<>();
        File fail = new File(this.failinimi);
        java.util.Scanner sc = new java.util.Scanner(fail, "UTF-8");
        while (sc.hasNextLine()) {
            String rida = sc.nextLine();
            String[] tükid = rida.split("; ");
            sisseloetudtweedid.add(new Tweet(tükid[0], tükid[1], Long.parseLong(tükid[2])));
        }
        return sisseloetudtweedid;
    }

    public String loePuhtastFailist() throws FileNotFoundException {
        StringBuilder sisseloetudTekst = new StringBuilder();
        File fail = new File(this.failinimi);
        java.util.Scanner sc = new java.util.Scanner(fail, "UTF-8");
        while (sc.hasNextLine()) {
            String rida = sc.nextLine();
            sisseloetudTekst.append(rida);
        }
        return sisseloetudTekst.toString();
    }

    public String loePuhtastFailist(String failinimi) throws FileNotFoundException {
        StringBuilder sisseloetudTekst = new StringBuilder();
        File fail = new File(failinimi);
        java.util.Scanner sc = new java.util.Scanner(fail, "UTF-8");
        while (sc.hasNextLine()) {
            String rida = sc.nextLine();
            sisseloetudTekst.append(rida);
        }
        return sisseloetudTekst.toString();
    }

//    public boolean failOlemas() {
//        File fail = new File(this.failinimi);
//        return fail.exists();
//    }

    public void kysiOtsisõna(String tekst, String title) {
        String otsisõna = JOptionPane.showInputDialog(null, tekst, title,
                JOptionPane.QUESTION_MESSAGE);
        this.otsisõna = otsisõna;
        this.failinimi = otsisõna + ".txt";
    }

    public void kysiTweetideArv(String tekst, String title) {
        int tweetideArv = Integer.parseInt(JOptionPane.showInputDialog(null, tekst, title,
                JOptionPane.QUESTION_MESSAGE));
        this.soovitudTweetideArv = tweetideArv;
    }

    public void kysiFailinimi(String tekst, String title) {
        String failinimi = JOptionPane.showInputDialog(null, tekst, title,
                JOptionPane.QUESTION_MESSAGE);
        this.failinimi = failinimi;
    }

    public String getOtsisõna() {
        return this.otsisõna;
    }

//    public int getSoovitudTweetideArv() {
//        return this.soovitudTweetideArv;
//    }

    public String getFailinimi() {
        return this.failinimi;
    }

    public ArrayList<Tweet> getTweedid() {
        return tweedid;
    }

    public ArrayList<Status> getToorTweedid() {
        return toorTweedid;
    }

    public ArrayList<Tweet> kysiFailistLugemist() throws FileNotFoundException {
        ArrayList<Tweet> failistLoetud = this.tweedid;
        int vastus = JOptionPane.showConfirmDialog(null,
                "Kas loen eelnevalt salvestatud andmed failist '" + getFailinimi() + "'? (jah/ei)",
                "Andmed failist", JOptionPane.YES_OPTION);

        if (vastus == JOptionPane.YES_OPTION) {
            failistLoetud = loeFailist();
            System.out.println("Failist " + getFailinimi() + " loeti " + failistLoetud.size() + " tweeti");
        }
        return failistLoetud;
    }

    public List<String> getTekst() {
        ArrayList<String> tekst = new ArrayList<>();
        for (Tweet tweet : this.tweedid) {
            tekst.add(tweet.getTekst());
        }
        return tekst;
    }

//    public String getTekst(ArrayList<Tweet> tweedid) {
//        StringBuilder tekst = new StringBuilder();
//        for (Tweet tweet : tweedid) {
//            tekst.append(tweet);
//            tekst.append(' ');
//        }
//        return tekst.toString();
//    }

}
