/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Subhra
 */
public class Song implements Serializable{
    private static final long serialVersionUID = 3403340764899391441L;
    
    private final String path;
    
    private final String title;
    
    private final String artistString;
    
    private final String albumString;
    
    private Artist artist;
    
    private Album album; 

    public Song(String path, String title, String artist, String album) {
        this.path = path;
        this.title = title;
        this.artistString = artist;
        this.albumString = album;
        prepare();
    }
    
    private void prepare(){
        artist = new Artist(artistString);
        album = new Album(albumString);
        album.addSong(this);
        artist.addAlbum(album);
        artist = MetaManager.addArtist(artist);
        album = MetaManager.getAlbum(album);
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtistString() {
        return artistString;
    }

    public String getAlbumString() {
        return albumString;
    }
    
    
    
}
