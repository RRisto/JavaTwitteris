import java.util.ArrayList;
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

    public StringBuilder deleteHTTP(StringBuilder sb) {
        String httpRegex = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
        StringBuilder sb2 = new StringBuilder();

        System.out.println("Algne sümbolite arv: " + sb.length());
        // vastav regex on pärit siit http://stackoverflow.com/questions/6038061/regular-expression-to-find-urls-within-a-string

        String s = sb.toString().replaceAll(httpRegex,"");

        sb2.append(s);
        System.out.println("Sümboleid peale linkide eemaldamist: " + sb2.length());
        return sb2;
    }

    public String buildExcludeRegex(String exclude){
        String [] excludeWords = exclude.split(" ");
        StringBuilder sb = new StringBuilder();
        sb.append("(?i)(");
        for (String word : excludeWords) {
            sb.append(word).append(" |");
        }
        sb.deleteCharAt(sb.length()-1).append(")");
        return sb.toString();
    }

    public StringBuilder deleteExcludeWords(StringBuilder sb, String excludeRegex) {

        StringBuilder sb2 = new StringBuilder();

        String s = sb.toString().replaceAll(excludeRegex, "");
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
}
