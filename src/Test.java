/**
 * Created by Risto on 12.03.2017.
 */

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;

public class Test {
    public static void main(String args[]) throws TwitterException {
        ArrayList<Tweet> tweedid =new ArrayList<>();
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("Consumer Key (API Key)", "Consumer Secret (API Secret)");

        twitter.setOAuthAccessToken(new AccessToken("Access Token",
                "\tAccess Token Secret"));
        Query query = new Query("Estonia");
        query.setCount(5);//100 tweeti
        QueryResult result = twitter.search(query);
        System.out.println(result.toString());
        for (Status status : result.getTweets()) {
           // System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
           // System.out.println("ID:" +status.getId());
            tweedid.add(new Tweet(status.getUser().getScreenName(), status.getText(), status.getId()));
        }
        Query query2=result.nextQuery();
        QueryResult result2 = twitter.search(query2);
        System.out.println("uus "+result2.toString());
        for (Status status : result2.getTweets()) {
            //System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
           // System.out.println("ID:" +status.getId());
            tweedid.add(new Tweet(status.getUser().getScreenName(), status.getText(), status.getId()));
        }

        System.out.println(tweedid.toString());
    }
}
