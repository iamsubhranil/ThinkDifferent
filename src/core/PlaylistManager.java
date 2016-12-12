/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Subhra
 */
public class PlaylistManager {
    
    private static Playlist currentPlaylist;
    
    private static final Playlist library = new Playlist("all songs");
    
    private static List<Playlist> allPlaylists;
    
    public static void addToLibrary(Song s){
        if(allPlaylists==null){
            allPlaylists = new ArrayList();
        }
        allPlaylists.add(library);
        library.addSong(s);
    }
    
    public static void loadAllPlaylists(){
        try {
            ObjectInputStream oos = new ObjectInputStream(new FileInputStream("playlists.data"));
            allPlaylists = (List<Playlist>)oos.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(PlaylistManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void loadCurrentPlaylist(){
        try {
            ObjectInputStream oos = new ObjectInputStream(new FileInputStream("playlist.data"));
            currentPlaylist = (Playlist)oos.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(PlaylistManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Playlist getCurrentPlaylist(){
        if(currentPlaylist==null){
            return library;
        }
        return currentPlaylist;
    }
    
    public static void addPlaylist(Playlist p){
        allPlaylists.add(p);
    }
    
    public static void removePlaylist(Playlist p){
        allPlaylists.remove(p);
    }
    
    public static List<Playlist> getAllPlaylists(){
        return allPlaylists;
    }
    
}
