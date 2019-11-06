package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> GamePlayers;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Salvo> salvos = new HashSet<>();

    public GamePlayer() { }

    public GamePlayer(Game game, Player player){
        this.game= game;
        this.player= player;

    }
    public GamePlayer(Game game, Player player,LocalDateTime localDateTime){
        this.game= game;
        this.player= player;
        this.joinDate= localDateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Set<GamePlayer> getGamePlayers() {
        return GamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        GamePlayers = gamePlayers;
    }

    public Set<Ship> getShips() {

        return ships;
    }

    public void setShips(Set<Ship> ships) {

        this.ships = ships;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }


    public Map<String, Object> gamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().playerDTO());
        return dto;
    }
    public boolean perdioTodosSusBarcos(){
        System.out.println();
       return this.getShips().stream().allMatch(ship -> ship.isSink());
    }
    public int cantidadDeBarcosHundidos(){
        return this.getShips().stream().filter(ship -> ship.isSink())
                .collect(Collectors.toList()).size();
    }

    public GamePlayer getOponnent() {
        return this.getGame().getGamePlayers()
                .stream()
                .filter(gp -> gp.getId() != this.getId())
                .findFirst()
                .orElse(null);
    }

    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }

}