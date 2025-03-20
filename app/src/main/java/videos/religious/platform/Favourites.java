package videos.religious.platform;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Favourites {
    @Id
    private long id;
    private String  ChannelName,
    ChannelId,
    ChannelProfile,
    numOfVideos,
    poster,
    postDate,
    VideoDesc,
    numOfBlesses,
    numOfAmens,
    numOfComments,
    VideoId,
    channelownerid,
    videourl,
    TotalVideos,
    PosterName;

    public long getId() {
        return id;
    }
    public void setId(long Id) {
        this.id = Id;
    }
    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String ChannelName) {
        this.ChannelName = ChannelName;
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

    public void setPostDate(String poster) {
        this.postDate = postDate;
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

    public void setVideoId(String videourl) {
        this.VideoId = VideoId;
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
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public String getPoster() {
        return poster;
    }

    public void setPosterName(String posterName) {
        this.PosterName = posterName;
    }
}
