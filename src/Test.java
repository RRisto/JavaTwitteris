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
import java.util.Scanner;

public class Test {
    public static void main(String args[]) throws Exception {
        ArrayList<Tweet> tweedid = new ArrayList<>();//kodukootud klass, lihtsam salvestada
        Twitter twitter = TwitterFactory.getSingleton();
        //NB! selleks, et tööle hakkaks, tee fail twitter4j.properties ja salvesta kausta, kus
        // on tweedid.txt fail.
        // Mida sinna panna, vaata siit: punkt 1 (via twitter4j.properties): http://twitter4j.org/en/configuration.html
        //NB! seda faili ära giti pane, see info pole avalikkusele

        Scanner sc = new Scanner(System.in);
        Analüüs analüüs = new Analüüs();

        System.out.println("Sisesta sõna, mille järgi tweete otsida: ");
        String otsisõna = sc.nextLine();

        System.out.println("Sisesta täisarv, mitu tweeti tahad pärida: ");
        int tweetideArv = sc.nextInt();
        sc.nextLine();

        System.out.println("Sisesta failinimi, kuhu salvestada otsisõna '" + otsisõna + "' tulemused: ");
        String failiNimi = sc.nextLine();

        Päring päring = new Päring();
        päring.päring(otsisõna, tweetideArv);

        päring.salvestaFaili(failiNimi);

        ArrayList<Tweet> failistLoetud = päring.tweedid;
        System.out.println("Kas loen eelnevalt salvestatud andmed failist " + failiNimi + "? (jah/ei)");
        String vastus = sc.nextLine();
        System.out.println(vastus);
        System.out.println(!vastus.equalsIgnoreCase("jah"));

        while (!((vastus.equalsIgnoreCase("ei")) || (vastus.equalsIgnoreCase("jah")))) {
            System.out.println("Vale sisend. Kas loen eelnevalt salvestatud andmed failist " + failiNimi + "? (jah/ei)");
            vastus = sc.next();
        }
        if (vastus.equalsIgnoreCase("jah")) {
            failistLoetud = päring.loeFailist(failiNimi);
            System.out.println("Failist " + failiNimi + " loeti " + failistLoetud.size() + " tweeti");
        }



        // Küsime, millised sõnad pilvest välistada, lisaks otsisõnale

        System.out.println("Sisesta tühikutega eraldatud sõnad, mida soovid otsingust välistada (lisaks otsisõnale)");
        String exclude = sc.nextLine() + " " + otsisõna;
        sc.close();
        System.out.println("Välistan analüüsist: " + exclude);
        String excludeRegex = analüüs.buildExcludeRegex(exclude);
        System.out.println("excluderegex: " + excludeRegex);


        System.out.println("Selline tweetide stringbuilder:");

        StringBuilder sb = analüüs.tweet2SB(failistLoetud);
        System.out.println(sb);

        // eemaldame lingid
        System.out.println("Peale linkide eemaldamist:");
        String httpRegex = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
        sb = analüüs.cleanText(sb, httpRegex, "");
        System.out.println(sb);

        // eemaldame kirjavahemärgid http://www.ocpsoft.org/tutorials/regular-expressions/java-visual-regex-tester/
        System.out.println("Peale kirjavahemärkide eemaldamist:");
        String punctuationRegex = "[-.,!?:]|'\\w";
        sb = analüüs.cleanText(sb, punctuationRegex, " ");
        System.out.println(sb);

        // eemaldame välistatud sõnad
        System.out.println("Peale välistatud sõnade eemaldamist:");
        sb = analüüs.cleanText(sb, excludeRegex, "");
        System.out.println(sb);

        // proovime ainult hashtage eraldada
        sb = analüüs.hashTagsOnly(sb);
        System.out.println(sb);

        //salvestame tweedid sõnapilve jaoks faili
        java.io.File failSonapilv = new java.io.File("sonapilv.txt");
        java.io.PrintWriter pw = new java.io.PrintWriter(failSonapilv, "UTF-8"); //kirjutab faili üle

        for (
                Tweet tweet : failistLoetud) {
            pw.print(tweet.getTekst().replaceAll("[\n]", ""));
        }
        pw.close();

        //Sõnapilv
        //http://kennycason.com/posts/2014-07-03-kumo-wordcloud.html
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        File initialFile = new File("sonapilv.txt");
        InputStream targetStream = new FileInputStream(initialFile);
        //final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("sonapilv.txt"));
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(targetStream);
        final Dimension dimension = new Dimension(200, 200);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        wordCloud.setPadding(0);
        wordCloud.setBackground(new

                RectangleBackground(dimension));
        wordCloud.setColorPalette(new ColorPalette(new Color(0xD5CFFA), new Color(0xBBB1FA), new
                Color(0x9A8CF5), new Color(0x806EF5)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile("wordcloud_rectangle.png");
    }
}


