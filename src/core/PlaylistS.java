/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Subhra
 */
public class PlaylistS implements Serializable{
    
    private Song[] songs = new Song[0];
    private final String name;
    private final int pos;
    
    public PlaylistS(Playlist playlist){
        songs = playlist.getAllSongs().toArray(songs);
        name = playlist.getName();
        pos = playlist.getPos(playlist.getCurrentSong());
    }
    
    public Playlist getPlaylist(){
        return new Playlist(songs,name,pos);
    }
    
}
