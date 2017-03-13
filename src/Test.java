import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String args[]) throws Exception {
        ArrayList<Tweet> tweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada
        Twitter twitter = TwitterFactory.getSingleton();
        //NB! selleks, et tööle hakkaks, tee fail twitter4j.properties ja salvesta kausta, kus
        // on tweedid.txt fail.
        // Mida sinna panna, vaata siit: punkt 1 (via twitter4j.properties): http://twitter4j.org/en/configuration.html
        //NB! seda faili ära giti pane, see info pole avalikkusele

        //algne variant, kogu mant peameetodis
//        twitter.setOAuthConsumer("", "");
//        twitter.setOAuthAccessToken(new AccessToken("",  ""));

//        Query query = new Query("Estonia");
//        int tweetideArv = 150;
//        Long lastID = Long.MAX_VALUE;
//        ArrayList<Status> tweets = new ArrayList<>();//siia toorandmed JSONis
//        //pärib tweedid, max päringuga 100, kui jääb puudu otsib juurde tweete
//        while (tweets.size() < tweetideArv) {
//            if (tweetideArv - tweets.size() > 100) {
//                query.setCount(100);//korraga üle 100 ei saa
//                } else {
//                query.setCount(tweetideArv - tweets.size());
//            } try { //try aitab exceptioneid kinni püüda
//                QueryResult result = twitter.search(query);
//                tweets.addAll(result.getTweets());
//                System.out.println("Kogusin " + tweets.size() + " tweeti");
//
//                for (Status tweet : tweets) {
//                    //paneb vajaliku info kodukootud klassi
//                    tweedid.add(new Tweet(tweet.getUser().getScreenName(), tweet.getText(), tweet.getId()));
//                    if (tweet.getId() < lastID) lastID = tweet.getId();
//                }
//            } catch (TwitterException te) {
//                System.out.println("Ei suutnud ühenduda: " + te);
//            }
//            query.setMaxId(lastID - 1);//muudab tweedi max id ära, et leida eelnevaid tweete
//        }
//
//        System.out.println(tweets.toString());
//        System.out.println("Kogusin "+tweedid.size()+" tweeti");
//        System.out.println(tweedid.toString());
//
//        //salvestame faili
//        java.io.File fail = new java.io.File("tweedid.txt");
//        //java.io.PrintWriter pw = new java.io.PrintWriter(fail, "UTF-8"); //kirjutab faili üle
//        java.io.PrintWriter pw = new java.io.PrintWriter(new FileOutputStream(fail, true)); //kirjutab faili lõppu juurde
//
//        for (Tweet tweet : tweedid) {
//            pw.print(tweet.getNimi().replaceAll("[\n]",""));//peab puhastama, muidu paneb faili tühje ridu, mis
//            //sisselugemisel hakkab exceptioneid andma
//            pw.print("; ");
//            pw.print(tweet.getTekst().replaceAll("[\n]",""));
//            pw.print("; ");
//            pw.print(tweet.getStatusId());
//            pw.print("\n");
//        }
//        pw.close();
//
//        //sisselugemine
//        ArrayList<Tweet> sisseloetudtweedid=new ArrayList<>();
//        java.util.Scanner sc = new java.util.Scanner(fail, "UTF-8");
//        System.out.println("Nüüd olen siin\n");
//
//        while (sc.hasNextLine()) {
//            String rida = sc.nextLine();
//            String[] tükid = rida.split("; ");
//            System.out.println("rida "+rida);
//            System.out.println("nimi: "+ tükid[0]+",\n tekst: "+tükid[1]+",\n id: ");
//            sisseloetudtweedid.add(new Tweet(tükid[0], tükid[1], Long.parseLong(tükid[2])));
//        }
//
//        System.out.println("Sisseloetud tweedid"+ sisseloetudtweedid.toString());
//        System.out.println("Algselt oli "+ tweedid.size()+" tweeti");
//        System.out.println("Nüüd on "+sisseloetudtweedid.size()+" tweeti");//kui kirjutad juurde faili, peaks see olema suurem

        //klassidega
        System.out.println("Proovin klasssidega");
        Päring päring=new Päring();
        päring.päring("Estonia", 5);
        //System.out.println(päring.tweedid.toString());
        päring.salvestaFaili("tweedid.txt");
        ArrayList<Tweet> failistLoetud=päring.loeFailist("tweedid.txt");

        System.out.println("Failist loeti "+failistLoetud.size()+" tweeti");
        //System.out.println(failistLoetud.toString());

        //salvestame tweedid sõnapilve jaoks faili
        java.io.File failSonapilv = new java.io.File("sonapilv.txt");
        java.io.PrintWriter pw = new java.io.PrintWriter(failSonapilv, "UTF-8"); //kirjutab faili üle

        for (Tweet tweet : failistLoetud) {
            pw.print(tweet.getTekst().replaceAll("[\n]",""));
        }
        pw.close();

        //Sõnapilv
        //http://kennycason.com/posts/2014-07-03-kumo-wordcloud.html
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        File initialFile = new File("sonapilv.txt");
        InputStream targetStream = new FileInputStream(initialFile);
        //final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("sonapilv.txt"));
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(targetStream);
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        wordCloud.setPadding(0);
        wordCloud.setBackground(new RectangleBackground(dimension));
        wordCloud.setColorPalette(new ColorPalette(new Color(0xD5CFFA), new Color(0xBBB1FA), new Color(0x9A8CF5), new Color(0x806EF5)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile("wordcloud_rectangle.png");
    }
}


