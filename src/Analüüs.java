import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by Sven on 3/19/2017.
 */
public class Analüüs {

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

    public StringBuilder hashTagsOnly(StringBuilder sb) {
        String hashtagRegex = "\\B(\\#[a-zA-Z]+\\b)(?!;)"; //http://stackoverflow.com/questions/38506598/regular-expression-to-match-hashtag-but-not-hashtag-with-semicolon
        Pattern hashtagPattern = Pattern.compile(hashtagRegex);
        StringBuilder sb2 = new StringBuilder();

        Matcher m = hashtagPattern.matcher(sb);

        System.out.println("Algne sümbolite arv: " + sb.length());

      while (m.find()) {
                System.out.println(m.group(1));
                sb2.append(m.group(1)).append(" ");
            }

        System.out.println("Sümboleid hashtagidena: " + sb2.length());

        return sb2;
    }

    public HashMap<String, Integer> wordFrequency(StringBuilder sb){
        HashMap<String,Integer> frequencies = new HashMap<String,Integer>();
        String [] words = sb.toString().split(" ");

        for (String word : words) {
            Integer frequency = frequencies.get(word);
            if (frequency == null) {
                frequencies.put(word, 1);
            } else {
                frequencies.put(word, frequency + 1);
            }
        }

        return frequencies;
    }

}
