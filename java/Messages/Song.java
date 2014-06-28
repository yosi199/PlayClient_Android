package Messages;

/**
 * Created by Unknown on 15/06/2014.
 */
public class Song {

    private String artistName = "Unknown Artist";
    private String albumName;
    private String titleName = "Unknown Title";
    private String pathInfo;
    private String indexPos;
    private String messageType;

    public Song() {
    }

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


}
