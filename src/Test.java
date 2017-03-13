/**
 * Created by Risto on 12.03.2017.
 */

import twitter4j.*;
import twitter4j.auth.AccessToken;

public class Test {
    public static void main(String args[]) throws TwitterException {

        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("Consumer Key (API Key)", "Consumer Secret (API Secret)");

        twitter.setOAuthAccessToken(new AccessToken("Access Token",
        "\tAccess Token Secret"));
        Query query = new Query("Estonia");
        query.setCount(100);//100 tweeti
        QueryResult result = twitter.search(query);
        System.out.println(result.toString());
        for (Status status : result.getTweets()) {
            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
            System.out.println("ID:" +status.getId());
        }
    }
}
