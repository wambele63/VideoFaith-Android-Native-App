package videos.religious.platform;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ChannelVideo {
    @Id
    public long id;
    private String  ChannelName,
    ChannelId,
    ChannelProfile,
    numOfVideos,
    poster,
    postDate,
    VideoDesc,
    numOfBlesses,
    numOfAmens,
    vDuration,
    numOfComments,
    VideoId,
    likeda,
    subscribed,
    favoured,
    channelownerid,
    subscriptions,
    videourl,
    downloads,
    TotalVideos,
    videosize,
    PosterName;

    public String getFavoured() {
        return favoured;
    }
    public void setFavoured(String Favoured){
        this.favoured = Favoured;
    }
    public String getLikeda() {
        return likeda;
    }
    public void setLikeda(String Likedv){
        this.likeda = Likedv;
    }
    public String getLiked() {
        return null;
    }
    public void setLiked(String Liked){
    }
    public String getDownloads() {
        return downloads;
    }
    public void setDownloads(String Downloads){
        this.downloads = Downloads;
    }

    public String getSubscribed() {
        return subscribed;
    }
    public void setSubscribed(String Subscribed){
        this.subscribed = Subscribed;
    }
    public String getVideosize() {
        return videosize;
    }
    public void setVideosize(String Videosize){
        this.videosize = Videosize;
    }
    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String ChannelName) {
        this.ChannelName = ChannelName;
    }

    public String getVDuration() {
        return vDuration;
    }

    public void setVDuration(String VDuration) {
        this.vDuration = VDuration;
    }
    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String Channelid) {
        this.ChannelId = Channelid;
    }
    public String getChannelProfile() {
        return ChannelProfile;
    }

    public void setChannelProfile(String channelProfile) {
        this.ChannelProfile = channelProfile;
    }

    public String getChannelownerid() {
        return channelownerid;
    }

    public void setChannelownerid(String channelownerid) {
        this.channelownerid = channelownerid;
    }

    public String getNumOfVideos() {
        return numOfVideos;
    }

    public void setNumOfVideos(String numOfVideos) {
        this.numOfVideos = numOfVideos;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String PostDate) {
        this.postDate = PostDate;
    }

    public String getVideoDesc() {
        return VideoDesc;
    }

    public void setVideoDesc(String VideoDesc) {
        this.VideoDesc = VideoDesc;
    }

    public String getNumOfBlesses() {
        return numOfBlesses;
    }

    public void setNumOfBlesses(String numOfBlesses) {
        this.numOfBlesses = numOfBlesses;
    }

    public String getNumOfAmens() {
        return numOfAmens;
    }

    public void setNumOfAmens(String numOfAmens) {
        this.numOfAmens = numOfAmens;
    }

    public String getNumOfComments() {
        return numOfComments;
    }

    public void setNumOfComments(String numOfComments) {
        this.numOfComments = numOfComments;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        this.VideoId = videoId;
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
    public String getSubscriptions() {
        return subscriptions;
    }
    public void setSubscriptions(String Subscriptions) {
        this.subscriptions = Subscriptions;
    }

    public String getPosterName() {
        return PosterName;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public String getPoster() {
        return poster;
    }

    public void setPosterName(String posterName) {
        this.PosterName = posterName;
    }

    public long getId(){
        return this.id;
    }
}
