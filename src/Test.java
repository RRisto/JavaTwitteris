import info.debatty.java.stringsimilarity.NGram;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String args[]) throws Exception {
        //NB! selleks, et tööle hakkaks, tee fail twitter4j.properties ja salvesta kausta, kus
        // on tweedid.txt fail.
        // Mida sinna panna, vaata siit: punkt 1 (via twitter4j.properties): http://twitter4j.org/en/configuration.html
        //NB! seda faili ära giti pane, see info pole avalikkusele
        Päring päring = new Päring();
        Analüüs analüüs = new Analüüs();

        päring.kysiOtsisõna("Sisesta sõna, mille järgi twitterist säutse otsida ", "Otsisõna");
        päring.kysiTweetideArv("Sisesta täisarv, mitu tweeti tahad pärida: ", "Tweetide arv");
//        päring.kysiFailinimi("Sisesta failinimi, kuhu salvestada päringu '" + päring.getOtsisõna() + "' tulemused", "Failinimi");

//        System.out.println("on olemas?" + päring.failOlemas());
        päring.päring();
        päring.salvestaFaili();
        ArrayList<Tweet> failistLoetud = päring.kysiFailistLugemist();

        // Küsime, millised sõnad pilvest välistada, lisaks otsisõnale
        String exclude = JOptionPane.showInputDialog(null,
                "Sisesta tühikutega eraldatud sõnad, mida soovid otsingust välistada (lisaks otsisõnale)",
                "Välistused",
                JOptionPane.QUESTION_MESSAGE);
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
        // praegu jääb sisse palju whitespace'i, ma ei tea, kas peaks veel eraldi topelttühikud ka eraldama?
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

        // teeme sagedustabeli hashtagidest (et oleks ülevaatlikum)
        System.out.println("Sagedustabel:");
        HashMap<String, Integer> frequencies = analüüs.wordFrequency(sb);
        System.out.println(frequencies);

        // sagedustabel sorditud kujul (arraylist)
        System.out.println("Sagedustabel, sorditud:");
        System.out.println(analüüs.descFrequency(frequencies));

        System.out.println("Sõnad sageduse järjekorras");
        ArrayList<Map.Entry<String, Integer>> frequenciesOrg = analüüs.descFrequency(frequencies);
        for (Map.Entry<String, Integer> stringIntegerEntry : frequenciesOrg) {
            System.out.println(stringIntegerEntry);
        }
        //Sõnapilv
        Sõnapilv.teeSõnapilv(päring.getTekst(), päring.getFailinimi() + "_sõnapilv.png");

        //sarnasuse arvutamine
        String kysimus = JOptionPane.showInputDialog(null,
                "Kas tahad arvutada tekstide sarnasust? (jah/ei)", "Tekstide sarnasus",
                JOptionPane.QUESTION_MESSAGE);
//        https://github.com/rrice/java-string-similarity
        if (kysimus.equalsIgnoreCase("jah")) {
            Päring päringTekst1 = new Päring();
            päringTekst1.kysiFailinimi("Sisesta 1. faili nimi, mille tweete tahad võrrelda", "Tekstide sarnasus");
            Päring päringTekst2 = new Päring();
            päringTekst2.kysiFailinimi("Sisesta 2. faili nimi, mille tweete tahad võrrelda", "Tekstide sarnasus");
            ArrayList<Tweet>tweedid1=päringTekst1.loeFailist();
            ArrayList<Tweet>tweedid2=päringTekst2.loeFailist();

            SimilarityStrategy strategy = new JaroWinklerStrategy();
            StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
            String tekst1 = päringTekst1.getTekst(tweedid1);
            String tekst2 = päringTekst2.getTekst(tweedid2);
            double score = service.score(tekst1, tekst2);
            System.out.println("Tekstide " + päringTekst1.getFailinimi() + " ja " + päringTekst2.getFailinimi() + " sarnasuse skoor on kasutades Jaro-Winkleri kaugust " + score);
//            https://github.com/tdebatty/java-string-similarity#normalized-levenshtein
            NormalizedLevenshtein l = new NormalizedLevenshtein();
            System.out.println("Tekstide " + päringTekst1.getFailinimi() + " ja " + päringTekst2.getFailinimi() +
                    " sarnasuse skoor on kasutades normaliseeritud Levenshteini kaugust " + l.distance(tekst1, tekst2));
            NGram ngram = new NGram(4);
            System.out.println("Tekstide " + päringTekst1.getFailinimi() + " ja " + päringTekst2.getFailinimi() +
                    " sarnasuse skoor on kasutades ngrami "+ngram.distance(tekst1, tekst2));

        }

    }
}


