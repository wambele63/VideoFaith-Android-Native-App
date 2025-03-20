package videos.religious.platform;

public class iconData {
    private String description;
    private int imgId;
    private int imgPos;

    public iconData(String str, int i, int i2) {
        this.description = str;
        this.imgId = i;
        this.imgPos = i2;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public int getImgPos() {
        return this.imgPos;
    }

    public int getImgId() {
        return this.imgId;
    }

    public void setImgId(int i) {
        this.imgId = i;
    }
}
