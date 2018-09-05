package alves.thiago.agenda;

/**
 * @author Alhaytham Alfeel on 10/10/2016.
 */

public class CardModel {
    private String imageId;
    private String titleId;
    private String subtitleId;
    private String channel;
    private String data;

    public CardModel(String imageId, String titleId, String subtitleId, String channel, String data) {
        this.imageId = imageId;
        this.titleId = titleId;
        this.subtitleId = subtitleId;
        this.channel = channel;
        this.data = data;
    }

    public String getImageId() {
        return imageId;
    }

    public String getTitle() { return titleId; }

    public String getSubtitle() {
        return subtitleId;
    }

    public String getChannel() {
        return channel;
    }

    public String getDataYT() {
        return data;
    }
}