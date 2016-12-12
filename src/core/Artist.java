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
public class Artist implements Serializable {

    private String name;
    private List<Album> albums;

    public Artist(String n) {
        name = n;
        albums = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    
    private boolean found = false;

    public void addAlbum(Album a) {
        found = false;
        albums.forEach(al -> {
            if (al.getName().equals(a.getName())) {
                al.addSong(a.getSongs().get(0));
                found = true;
            }
        });
        if (!found) {
            albums.add(a);
        }
    }
    
    public List<Album> getAlbums(){
        return albums;
    }

}
