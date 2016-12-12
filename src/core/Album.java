/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Subhra
 */
public class Album implements Serializable{
    
    private String name;
    private List<Song> songs;
    
    public Album(String n){
        name = n;
        songs = new ArrayList<>();
    }
    
    public String getName(){
        return name;
    }
    
    public void addSong(Song song){
        songs.add(song);
    }
    
    public List<Song> getSongs(){
        return songs;
    }
    
}
