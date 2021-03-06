package com.mycompany.compvale.model;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author delet
 */
public class Cinema {
    private String name;
    private String genre;
    private String producer;
    private String director;
    private Set<String> categories = new HashSet<String>();

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }
    

    public String getProducer() {
        return producer;
    }

    public String getDirector() {
        return director;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    
    public void setProducer(String producer) {
        this.producer = producer;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public void addCategory(String category){
        categories.add(category);
    }
}
