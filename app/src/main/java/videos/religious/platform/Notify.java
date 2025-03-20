package videos.religious.platform;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Notify {
    @Id
    private long Id;
    private String  Contenturl,
            ChannelId,
            ContentId,
            NotifyType,
            NotifyImage,
            NotifyDate,
            Notification,
            Contentv,
            Contentl,
            Contentd,
            NotificationId,
            channelownerid,
            videourl,
            TotalVideos,
            PosterName;

    public long getId(){
        return this.Id;
    }
    public void setId(long id){
        this.Id = id;
    }
    public String getContenturl() {
        return Contenturl;
    }

    public void setContenturl(String contenturl) {
        this.Contenturl = contenturl;
    }
    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String Channelid) {
        this.ChannelId = Channelid;
    }
    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        this.ContentId = contentId;
    }

    public String getChannelownerid() {
        return channelownerid;
    }

    public void setChannelownerid(String channelownerid) {
        this.channelownerid = channelownerid;
    }

    public String getNotifyType() {
        return NotifyType;
    }

    public void setNotifyType(String NotifyType) {
        this.NotifyType = NotifyType;
    }

    public String getNotifyDate() {
        return NotifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.NotifyDate = notifyDate;
    }

    public String getNotification() {
        return Notification;
    }

    public void setNotification(String notification) {
        this.Notification = notification;
    }

    public String getContentv() {
        return Contentv;
    }

    public void setContentv(String Contentv) {
        this.Contentv = Contentv;
    }

    public String getContentl() {
        return Contentl;
    }

    public void setContentl(String Contentl) {
        this.Contentl = Contentl;
    }

    public String getContentd() {
        return Contentd;
    }

    public void setContentd(String Contentd) {
        this.Contentd = Contentd;
    }

    public String getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(String notificationId) {
        this.NotificationId = notificationId;
    }
    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getTotalVideos() {
        return TotalVideos;
    }

    public void setTotalVideos(String totalVideos) {
        this.TotalVideos = totalVideos;
    }

    public String getPosterName() {
        return PosterName;
    }
    public void setNotifyImage(String notifyImage)
    {
        this.NotifyImage = notifyImage;
    }
    public String getNotifyImage() {
        return NotifyImage;
    }
    public void setPosterName(String posterName) {
        this.PosterName = posterName;
    }
}
