package com.mycompany.compvale.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stagirs.docextractor.wiki.LemmaParser;
import com.github.stagirs.docextractor.wiki.WikiParser;
import com.github.stagirs.docextractor.wiki.model.Command;
import com.github.stagirs.docextractor.wiki.model.Link;
import com.mycompany.compvale.model.Cinema;
import com.mycompany.compvale.model.CinemaType;
import com.mycompany.compvale.model.Film;
import com.mycompany.compvale.model.FilmArticle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang.math.NumberUtils.toInt;

/**
 *
 * @author delet
 */
public class ArticleParser {
    public static final String SOURCE_DIR = "c:/docs/docs";
    public static final String FILTERED_ARTICLE_DIR = "c:/compvale/filtered_sources/";
     public static final String FILM_MODEL = "c:/compvale/model/";
    
    public static void parseFilms() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(FILTERED_ARTICLE_DIR, CinemaType.FILM + ".txt")), "utf-8"));
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String line;
            while((line = br.readLine()) != null){
                Film film = new Film();
                List elems = WikiParser.getElems(LemmaParser.getLems(line).iterator(), null);
                Command basicFacts = basicFacts(elems);
                
                setCategories(elems, film);
                setCinemaProperties(basicFacts, film);
                setFilmProperies(basicFacts, film);
                
                FileUtils.write(new File(FILM_MODEL, CinemaType.FILM + ".txt"), objectMapper.writeValueAsString(film) + "\n", "utf-8", true);
            }
            System.out.println("");
        }finally{
            br.close();
        }
    }
    
    public static void filterCinema() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(SOURCE_DIR)), "utf-8"));
        try{
            String line;
            while((line = br.readLine()) != null){
                List elems = WikiParser.getElems(LemmaParser.getLems(line).iterator(), null);
                CinemaType cinemaType = cinemaType(elems);
                if(cinemaType != null){
                    FileUtils.write(new File(FILTERED_ARTICLE_DIR, cinemaType.toString().toLowerCase() + ".txt"), line + "\n", "utf-8", true);
                } 
            }
        }finally{
            br.close();
        }
    }
    
    public static CinemaType cinemaType(List elems){
        for(Object elem: elems){
            if (!(elem instanceof Command)) {
                continue;
            }
            Command basicFacts = (Command) elem;
            for(Object item: basicFacts.getItems()){
                if(!(item instanceof String)){
                    continue;
                }
                String property = (String) item;

                if(property.startsWith("Фильм")){
                    return CinemaType.FILM;
                }
                if(property.startsWith("Телесериал")){
                    return CinemaType.SERIAL;
                }
                if(property.startsWith("Мультфильм")){
                    return CinemaType.ANIMATION;
                }
                if(property.startsWith("Мультсериал")){
                    return CinemaType.ANIMATED_SERIAL;
                }
            }
            return null;
        }
        return null;
    }
    
    
    public static void setCinemaProperties(Command basicFacts, Cinema cinema){
        Iterator basicFactsIterator = basicFacts.getItems().iterator();
        Object item = null;
        while(basicFactsIterator.hasNext()){
            if(item == null){
                item = basicFactsIterator.next();
            }
            if(!(item instanceof String)){
                item = basicFactsIterator.next();
                continue;
            }
            String property = (String) item;
            Link value = null;
            if(basicFactsIterator.hasNext()){
                item = basicFactsIterator.next();
                if(item instanceof Link){
                    value = (Link) item;
                }
            }
            if(property.contains("РусНаз")){
                Pattern p = Pattern.compile(".*РусНаз\\s*=\\s*([^\\t]*)\\s*.*");
                Matcher m = p.matcher(property);
                if(m.find()){
                    cinema.setName(m.group(1));
                }
            }else if(property.contains("Режиссёр") && value != null){
                cinema.setDirector(value.getLink());
                item = null;
            } else if(property.contains("Продюсер") && value != null){
                cinema.setProducer(value.getLink());
                item = null;
            }else if(property.contains("Жанр") && value != null){
                cinema.setGenre(value.getLink());
                item = null;
            }
        }
    }
    
    public static void setFilmProperies(Command basicFacts, Film film){
        Iterator basicFactsIterator = basicFacts.getItems().iterator();
        
        Object item = null;
        while(basicFactsIterator.hasNext()){
            if(item == null){
                item = basicFactsIterator.next();
            }
            
            if(!(item instanceof String)){
               item = null;
               continue;
            }
            String property = (String) item;
            
            if(property.contains("В главных ролях ")){
                item = basicFactsIterator.next();
                if(!(item instanceof Link)){
                    continue;
                }
                Link leadRole = (Link) item;
                film.addLeadRole(leadRole.getLink());
                item = basicFactsIterator.next();
                if(!(item instanceof String)){
                    continue;
                }
                String elem = (String) item;
                while(elem.equals("&lt;br&gt;")){
                    item = basicFactsIterator.next();
                    if(!(item instanceof Link)){
                        continue;
                    }
                    leadRole = (Link) item;
                    film.addLeadRole(leadRole.getLink());
                    item = basicFactsIterator.next();
                    if(!(item instanceof String)){
                        continue;
                    }
                    elem = (String) item;
                }
            }else if(property.contains("Время")){
                Pattern p = Pattern.compile(".*Время = (\\d*)[^\\d].*");
                Matcher m = p.matcher(property);
                if(m.find()){
                    film.setDuration(toInt(m.group(1)));
                }
                item = null;
            }else{
                item = null;
            }
        }
    }
    
    public void setAnimationProperies(Command basicFacts, Cinema cinema){
        
    }
    
    public void setSerialProperies(Command basicFacts, Cinema cinema){
        
    }
    
    public static Command basicFacts(List elems){
        for(Object elem: elems){
            if (!(elem instanceof Command)) {
                continue;
            }
            return (Command) elem;
        }
        
        return null;
    }
    
    public static Set<String> setCategories(List elems, Cinema cinema){
        Set<String> result = new HashSet<String>();
        
        for(Object elem: elems){
            if(!(elem instanceof Link)){
                continue;
            }
            Link category = (Link) elem;
            if(!category.getLink().contains("Категория:")){
                continue;
            }
            cinema.addCategory(category.getLink().substring(category.getLink().indexOf("Категория:") + "Категория:".length()));
        }
        
        return result;
    }
    
    
    
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
      //  filterCinema();
       parseFilms();
    }
}
