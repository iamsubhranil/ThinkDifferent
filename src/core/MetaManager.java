/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Subhra
 */
public class MetaManager {
    
    private static final List<Artist> artists = new ArrayList<>();
    private static Artist ret = new Artist("tdudfar");
    private static final List<Album> albums = new ArrayList<>();
    private static Album retal = new Album("tdudfal");
    
    public static Artist addArtist(Artist a) {
        ret = a;
        artists.forEach(ar -> {
            if (ar.getName().equals(a.getName())) {
                ar.addAlbum(a.getAlbums().get(0));
                albums.add(a.getAlbums().get(0));
                ret = ar;
            }
        });
        if (ret.equals(a)) {
            artists.add(a);
            albums.add(a.getAlbums().get(0));
        }
        return ret;
    }
    
    public static Album getAlbum(Album a) {
        retal = a;
        albums.forEach(ar -> {
            if (ar.getName().equals(a.getName())) {
                retal = ar;
            }
        });
        return retal;
    }
    
    public static void printArtists() {
        artists.sort((Artist o1, Artist o2) -> {
            return o1.getName().compareTo(o2.getName());
        });
        albums.sort((Album o1, Album o2) -> {
            return o1.getName().compareTo(o2.getName());
        });
        
        System.out.println("Total Artists : " + artists.size());
        System.out.println("Total Albums : " + albums.size());
        artists.forEach((a) -> {
            a.getAlbums().sort((Album o1, Album o2) -> {
                return o1.getName().compareTo(o2.getName());
            });
            System.out.println("\n\nArtist : " + a.getName());
            System.out.println("Total albums : " + a.getAlbums().size());
            a.getAlbums().forEach(al -> {
                System.out.println("Album : " + al.getName());
                System.out.println("Total songs : " + al.getSongs().size());
            });
        });
    }
    
}
