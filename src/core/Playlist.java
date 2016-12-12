/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Subhra
 */
public class Playlist implements Serializable{
    private static final long serialVersionUID = -6406216802291770945L;

    private final List<Song> songs;
    private int pos = -1;
    private String name;
    
    public Playlist(List<Song> s,String n) {
        songs = s;
        name = n;
    }

    public Playlist(String n) {
        songs = new ArrayList<>();
        name = n;
    }
    
    public void rename(String n){
        name = n;
    }

    public void addSong(Song s) {
        songs.add(s);
    }

    public void addSongToPlayNext(Song s) {
        songs.add(pos + 1, s);
    }
    
    public Song getCurrentSong(){
        if(pos==-1){
            return null;
        }
        return songs.get(pos);
    }

    public Song getNextSong() {
        if (pos < songs.size() - 1) {
            pos++;
            return songs.get(pos);
        }
        return null;
    }

    public void removeSong(Song s) {
        songs.remove(s);
    }

    public Song getPrevSong() {
        if (pos > 0) {
            pos--;
            return songs.get(pos);
        }
        return null;
    }
    
    public int getPos(Song s){
        return songs.indexOf(s);
    }
    
    public int size(){
        return songs.size();
    }
    
    public void addSongs(List<Song> s){
        songs.addAll(s);
    }
    
    public List<Song> getAllSongs(){
        return songs;
    }

    public String getName(){
        return name;
    }
    
    public Playlist(Song[] s,String name,int p){
        songs = new ArrayList(Arrays.asList(s));
        this.name = name;
        pos = p;
    }
}
