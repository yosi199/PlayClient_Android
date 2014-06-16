package Messages;

/**
 * Created by Unknown on 15/06/2014.
 */
public class Song {

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getTitleName() {
        return titleName;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getIndexPos() {
        return indexPos;
    }

    public String getMessageType() {
        return messageType;
    }

    private String artistName;
    private String albumName;
    private String titleName;
    private String pathInfo;
    private String indexPos;
    private String messageType;

    public Song() {
    }


}
