package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class Ship{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne
    @JoinColumn (name = "gamePlayerId")
    GamePlayer gamePlayer;

    @ElementCollection
    @Column (name = "shipLocation")
    private List<String> salvoLocations = new ArrayList<>();
    @Transient
    Set<String> historialHits= new LinkedHashSet<>();

    public Ship() { }

    public Ship(String type, List<String>locations, GamePlayer gamePlayer) {

        this.type = type;
        this.salvoLocations = locations;
        this.gamePlayer= gamePlayer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocations() {
        return salvoLocations;
    }


    public void setLocations(List<String> locations) {
        this.salvoLocations = locations;
    }

    public int getHitsLocation(List<String> hitsLocation){
        List<String> locations=
        this.getLocations().stream().filter(s -> hitsLocation.stream().anyMatch(h->h.equals(s)))
                .collect(Collectors.toList());
        historialHits.addAll(locations);
        return locations.size();
     }


     public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> shipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", this.getType());
        dto.put("locations", this.getLocations());
        return dto;
    }


    public boolean isSink(){
        return this.getLocations().size() == historialHits.size();
    }
}
