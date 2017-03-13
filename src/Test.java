/**
 * Created by Risto on 12.03.2017.
 */

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class Test {
    public static void main(String args[]) throws TwitterException, Exception {
        ArrayList<Tweet> tweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada
        Twitter twitter = TwitterFactory.getSingleton();

        twitter.setOAuthConsumer("", "");
        twitter.setOAuthAccessToken(new AccessToken("",""));

        Query query = new Query("Estonia");
        int tweetideArv = 150;
        Long lastID = Long.MAX_VALUE;
        ArrayList<Status> tweets = new ArrayList<Status>();//siia toorandmed JSONis
        //pärib tweedid, max päringuga 100, kui jääb puudu otsib juurde tweete
        while (tweets.size() < tweetideArv) {
            if (tweetideArv - tweets.size() > 100) {
                query.setCount(100);//korraga üle 100 ei saa
                } else {
                query.setCount(tweetideArv - tweets.size());
            } try { //try aitab exceptioneid kinni püüda
                QueryResult result = twitter.search(query);
                tweets.addAll(result.getTweets());
                System.out.println("Kogusin " + tweets.size() + " tweeti");

                for (Status tweet : tweets) {
                    //paneb vajaliku info kodukootud klassi
                    tweedid.add(new Tweet(tweet.getUser().getScreenName(), tweet.getText(), tweet.getId()));
                    if (tweet.getId() < lastID) lastID = tweet.getId();
                }
            } catch (TwitterException te) {
                System.out.println("Ei suutnud ühenduda: " + te);
            } ;
            query.setMaxId(lastID - 1);//muudab tweedi max id ära, et leida eelnevaid tweete
        }

        System.out.println(tweets.toString());
        System.out.println("Kogusin "+tweedid.size()+" tweeti");
        System.out.println(tweedid.toString());

        //salvestame faili
        java.io.File fail = new java.io.File("tweedid.txt");
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

        //sisselugemine
        ArrayList<Tweet> sisseloetudtweedid=new ArrayList<>();
        java.util.Scanner sc = new java.util.Scanner(fail, "UTF-8");
        System.out.println("Nüüd olen siin\n");

        while (sc.hasNextLine()) {
            String rida = sc.nextLine();
            String[] tükid = rida.split("; ");
            System.out.println("rida "+rida);
            System.out.println("nimi: "+ tükid[0]+",\n tekst: "+tükid[1]+",\n id: ");
            sisseloetudtweedid.add(new Tweet(tükid[0], tükid[1], Long.parseLong(tükid[2])));
        }

        System.out.println("Sisseloetud tweedid"+ sisseloetudtweedid.toString());
        System.out.println("Algselt oli "+ tweedid.size()+" tweeti");
        System.out.println("Nüüd on "+sisseloetudtweedid.size()+" tweeti");//kui kirjutad juurde faili, peaks see olema suurem
    }
}


