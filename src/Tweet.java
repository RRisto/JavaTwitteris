/**
 * Created by Risto on 13.03.2017.
 */
public class Tweet {
    String nimi;
    String tekst;
    Long statusId;

    public Tweet(String nimi, String tekst, Long statusId) {
        this.nimi = nimi;
        this.tekst = tekst;
        this.statusId = statusId;
    }

    @Override
    public String toString() {
        return "\nnimi: " + nimi + ",\n tekst: " + tekst;
    }

    public String getNimi() {
        return nimi;
    }

    public String getTekst() {
        return tekst;
    }

    public Long getStatusId() {
        return statusId;
    }
}
