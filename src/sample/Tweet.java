package sample;

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
        return this.nimi + "; " + this.tekst+"; "+this.statusId+"\n";
    }

    public String getNimi() {
        return this.nimi;
    }

    public String getTekst() {
        return this.tekst;
    }

    public Long getStatusId() {
        return this.statusId;
    }

}
