package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Analüüs {

    public String loadStopwords() throws FileNotFoundException {
        File file=new File("stopwords.txt");
        Scanner sc=new Scanner(file, "UTF-8");
        StringBuilder stopwords=new StringBuilder();
        while (sc.hasNextLine()) {
            stopwords.append(sc.nextLine());
            stopwords.append(" ");
        }
        return stopwords.toString();
    }

    public StringBuilder tweet2SB(ArrayList<Tweet> tweets){
        StringBuilder sb = new StringBuilder();
        for (Tweet tweet: tweets) {
            sb.append(tweet.getTekst().replaceAll("[\n]",""));
        }
        return sb;
    }

    public String buildExcludeRegex(String exclude){
        String [] excludeWords = exclude.split(" ");
        StringBuilder sb = new StringBuilder();
        sb.append("(?i)(\\b");
        for (String word : excludeWords) {
            sb.append(word).append("\\b|");
        }
        sb.deleteCharAt(sb.length()-1).append(")");
        return sb.toString();
    }

    public StringBuilder cleanText(StringBuilder sb, String excludeRegex, String replaceRegex) {

        StringBuilder sb2 = new StringBuilder();

        String s = sb.toString().replaceAll(excludeRegex, replaceRegex);
        sb2.append(s);

        return sb2;
    }

}
