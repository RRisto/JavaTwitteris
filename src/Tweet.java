
public class Tweet {
    private String nimi;
    private String tekst;
    private Long statusId;

    public Tweet(String nimi, String tekst, Long statusId) {
        this.nimi = nimi;
        this.tekst = tekst;
        this.statusId = statusId;
    }

    @Override
    public String toString() {
        return nimi + "; " + tekst+"; "+statusId+"\n";
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
