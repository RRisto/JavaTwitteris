package sample;

import twitter4j.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Päring {
    private ArrayList<Tweet> tweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada
    private String otsisõna;
    private int soovitudTweetideArv;//tweetide arv, mida kasutaja tahaks päringuga saada

    public Päring(String otsisõna, int tweetideArv) {
        this.otsisõna = otsisõna;
        this.soovitudTweetideArv = tweetideArv;
    }

    public int getTweetideArv() {
        return tweedid.size();
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

                for (Status tweet : tweets) {//otsime tweedi ID, et leida järgmise päringu max tweedi ID
                    if (tweet.getId() < lastID) lastID = tweet.getId();
                }
            } catch (TwitterException te) { //kui twitteri poolt tuleb mingi erind, püüab kinni
                System.out.println("Ei suutnud ühenduda: " + te);
                erinditeArv += 1;
                //kui 3+ erindit on saanud päringus, siis pole kas netiühendust või on API limiit täis, lõpetame ära
                if (erinditeArv > 3) {
                    break;
                }
            }
            query.setMaxId(lastID - 1);//muudab tweedi max id ära, et leida eelnevaid tweete
        }
        for (Status tweet : tweets) {
            //paneb vajaliku info kodukootud klassi
            tweedid.add(new Tweet(tweet.getUser().getScreenName(), tweet.getText(), tweet.getId()));
        }
        return tweedid;
    }

    public void salvestaFaili(String failinimi) throws Exception {
        //salvestame faili
        File fail = new File(failinimi);
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


    public String puhastaTekst(String exclude) throws FileNotFoundException {

        //puhastame teksti
        Analüüs analüüs = new Analüüs();
        StringBuilder sb = analüüs.tweet2SB(tweedid);
        String httpRegex = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
        sb = analüüs.cleanText(sb, httpRegex, "");
        String punctuationRegex = "[-.,!?:]|'\\w";
        sb = analüüs.cleanText(sb, punctuationRegex, " ");
        String stopwords = analüüs.loadStopwords() + exclude;
        String excludeRegex = analüüs.buildExcludeRegex(stopwords);

        sb = analüüs.cleanText(sb, excludeRegex, "");
        return sb.toString();

    }


    public String getOtsisõna() {
        return this.otsisõna;
    }


    public List<String> getTekst() {
        ArrayList<String> tekst = new ArrayList<>();
        for (Tweet tweet : this.tweedid) {
            tekst.add(tweet.getTekst());
        }
        return tekst;
    }

    public void lisaFailist(String failinimi) throws FileNotFoundException {
        ArrayList<Tweet> sisseloetudtweedid = new ArrayList<>();
        File fail = new File(failinimi);
        java.util.Scanner sc = new java.util.Scanner(fail, "UTF-8");
        while (sc.hasNextLine()) {
            String rida = sc.nextLine();
            String[] tükid = rida.split("; ");
            sisseloetudtweedid.add(new Tweet(tükid[0], tükid[1], Long.parseLong(tükid[2])));
        }
        this.tweedid.addAll(sisseloetudtweedid);
    }

}
