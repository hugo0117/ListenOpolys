/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listenopolys.models;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author husoeur
 */
public class Playlist {
    private String title;
    private Time duration;
    private Set<Track> playlist;
    
    public Playlist(String title){
        this.title=title;
        this.duration=new Time(0,0,0);
        playlist=new TreeSet<>();
    }
   
    public void addTrack(Track t){
            playlist.add(t);
    }
    
    public void removeTrack(Track t){
        playlist.remove(t);
    }
    
    public Set<Track> getTracks(){
        return playlist;
    }
    
    public String getTitle(){
        return this.title;
    }
    
    public boolean equals(Object o){
        if(o==null){
            return false;
        }
        if(this==o){
            return true;
        }
        if(this.getClass()!=o.getClass()){
            return false;
        }
        Playlist p = (Playlist) o;
        if(title != p.getTitle())
            return false;
        return title.equals(p.title);
                
    }
}