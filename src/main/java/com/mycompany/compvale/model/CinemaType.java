package com.mycompany.compvale.model;

/**
 *
 * @author delet
 */
public enum CinemaType {
    FILM, SERIAL, ANIMATION, ANIMATED_SERIAL;
    
    
    public String wikiName(){
        switch (this) {
            case FILM:
                    return "Фильм";
            case SERIAL:
                    return "Телесериал";
            case ANIMATION:
                    return "Мультфильм";
            case ANIMATED_SERIAL:
                    return "Мультсериал";  
            
        }
        return null;
    }
    
    public static CinemaType valueOfWikiName(String name){
        switch (name) {
            case "Фильм":
                    return FILM;
            case "Телесериал":
                    return SERIAL;
            case "Мультфильм":
                    return ANIMATION;
            case "Мультсериал":
                    return ANIMATED_SERIAL;  
            
        }
        return null;
    }
}
