package videos.religious.platform;

public class ChannelResults {
    public long id;
    private String
            contentHead,
            contentId,
            contentPicture,
            tag,
            contentUrl,
            contentDesc;

    public String getContentHeader() {
        return contentHead;
    }

    public void setContentHeader(String ContentHead) {
        this.contentHead = ContentHead;
    }
    public String getContentPicture() {
        return contentPicture;
    }

    public void setContentPicture(String ContentPicture) {
        this.contentPicture = ContentPicture;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String ContentId) {
        this.contentId = ContentId;
    }
    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String ContentUrl) {
        this.contentUrl = ContentUrl;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String ContentDesc) {
        this.contentDesc = ContentDesc;
    }

    public void setTag(String Tag) {
        this.tag = Tag;
    }
    public String getTag() {
        return tag;
    }
    public long getId(){
        return this.id;
    }
}
