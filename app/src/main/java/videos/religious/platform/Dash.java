package videos.religious.platform;

public class Dash {
    private int
            ChannelId,
            ContentId,
            DashImage;
    public int getChannelId() {
        return ChannelId;
    }

    public void setChannelId(int Channelid) {
        this.ChannelId = Channelid;
    }

    public int getContentId() {
        return ContentId;
    }

    public void setContentId(int contentId) {
        this.ContentId = contentId;
    }
    public void setDashImage(int dashImage) {
        this.DashImage = dashImage;
    }

    public int getDashImage() {
        return DashImage;
    }
}