package com.codeoftheweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private long turnNumber;


    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "gamePlayer_id")
    GamePlayer gamePlayer;




    @ElementCollection
    @Column (name = "salvoLocation")
    private List<String> salvoLocations = new ArrayList<>();


    public Salvo() { }

    public Salvo(int turnNumber, List<String>locations, GamePlayer gamePlayer) {

        this.turnNumber = turnNumber;
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

    public long getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(long turnNumber) {
        this.turnNumber = turnNumber;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }



    public Map<String, Object> salvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurnNumber());
        dto.put("player", gamePlayer.getPlayer().getId());
        dto.put("locations", this.getSalvoLocations());
        return dto;
    }
}
