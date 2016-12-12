/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Subhra
 */
public class SearchManager {

    private static List<File> allowedLocations = new LinkedList();

    public static List<File> getAllowedLocations() {
        return allowedLocations;
    }

    public static void setAllowedLocations(List<File> allowedLocations) {
        SearchManager.allowedLocations = allowedLocations;
    }

    public static void addLocation(File f) {
        allowedLocations.add(f);
    }

    public static List<File> getAllowedLocationsInDrive(File drive) {
        List<File> qf = new LinkedList();
        allowedLocations.stream().filter((location) -> (location.getAbsolutePath().startsWith(drive.getAbsolutePath()))).forEach((location) -> {
            qf.add(location);
        });
        return qf;
    }

    public static boolean hasParentAlready(File folder) {
        return allowedLocations.stream().anyMatch((location) -> (folder.getAbsolutePath().startsWith(location.getAbsolutePath())));
    }

    public static boolean hasAlready(File c) {
        return allowedLocations.stream().anyMatch((loc) -> (loc.getAbsolutePath().equals(c.getAbsolutePath())));
    }
    
    public static void addDrive(File drive){
        allowedLocations.stream().filter((f) -> (f.getAbsolutePath().startsWith(drive.getAbsolutePath()))).forEach((f) -> {
            removeLocation(f);
        });
        addLocation(drive);
    }

    public static void removeLocation(File lo) {
        allowedLocations.remove(lo);
    }

}
