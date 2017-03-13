import twitter4j.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Päring {

    static ArrayList<Tweet> tweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada

    Twitter twitter = TwitterFactory.getSingleton();

    public ArrayList<Tweet> päring(String otsisõna, int tweetideArv) {
        Query query = new Query(otsisõna);
        //int tweetideArv = 150;
        Long lastID = Long.MAX_VALUE;
        ArrayList<Status> tweets = new ArrayList<>();//siia toorandmed JSONis
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
            }
            query.setMaxId(lastID - 1);//muudab tweedi max id ära, et leida eelnevaid tweete
        }
        return tweedid;
    }

    public void salvestaFaili(String failiNimi) throws Exception {
        //salvestame faili
        java.io.File fail = new java.io.File(failiNimi);
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

    public ArrayList<Tweet> loeFailist(String failiNimi) throws FileNotFoundException {
        ArrayList<Tweet> sisseloetudtweedid=new ArrayList<>();
        java.io.File fail = new java.io.File(failiNimi);
        java.util.Scanner sc = new java.util.Scanner(fail, "UTF-8");
        //System.out.println("Nüüd olen siin\n");

        while (sc.hasNextLine()) {
            String rida = sc.nextLine();
            String[] tükid = rida.split("; ");
//            System.out.println("rida "+rida);
//            System.out.println("nimi: "+ tükid[0]+",\n tekst: "+tükid[1]+",\n id: ");
            sisseloetudtweedid.add(new Tweet(tükid[0], tükid[1], Long.parseLong(tükid[2])));
    }
    return sisseloetudtweedid;
    }

}
