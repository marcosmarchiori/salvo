package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private ShipRepository shiprepository;
    @Autowired
    private SalvoRepository salvoRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/games")
    public Map<String, Object> getAll(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.playerDTO());
        }
        dto.put("games", gameRepository.findAll()
                .stream()
                .map(game -> game.gameDTO())
                .collect(Collectors.toList()));
        return dto;
    }


    @RequestMapping("/listGames")
    public List<Long> getId() {
        return gameRepository.findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList());
    }


    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable Long gamePlayerId, Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<Map<String, Object>>(makeMap("error", "Missing data"), HttpStatus.UNAUTHORIZED);
        }
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();
        Player player = playerRepository.findByUserName(authentication.getName());

        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "Name already in use"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer == null) {
            return new ResponseEntity<>(makeMap("error", "Name already in use"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "Name already in use"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer opponent = this.getOpponent(gamePlayer);

        Map<String, Object> dto = new LinkedHashMap<>();
        Map<String, Object> hits = new HashMap<>();
        hits.put("self", this.DTOIterador(gamePlayer));
        hits.put("opponent", this.DTOIterador(gamePlayer.getOponnent()));
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getCreationDate());
        dto.put("gameState", this.getGameState(gamePlayer));
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(gamePlayer2 -> gamePlayer2.gamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(ship -> ship.shipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(gamePlayer1 -> gamePlayer1.getSalvos())
                .flatMap(salvos -> salvos.stream()).map(salvo -> salvo.salvoDTO())
                .collect(Collectors.toList()));
        dto.put("hits", hits);

        addScore(gamePlayer, getGameState(gamePlayer));

        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);

    }

    public void addScore(GamePlayer gamePlayer, String stateNew){
        if(scoreRepository.findAll()
                .stream()
                .filter(score -> gamePlayer.getGame()
                        .getId()== score.getGame().getId()).count()<1){
            if(stateNew == "WON"){
                Score score1 = new Score( gamePlayer.getPlayer(),gamePlayer.getGame(),1.0F, LocalDateTime.now());
                Score score2 = new Score( gamePlayer.getOponnent().getPlayer(),gamePlayer.getGame(),0.0F, LocalDateTime.now());

                scoreRepository.save(score1);
                scoreRepository.save(score2);
            }
            if(stateNew == "LOST"){
                Score score1 = new Score( gamePlayer.getPlayer(),gamePlayer.getGame(),0.0F, LocalDateTime.now());
                Score score2 = new Score( gamePlayer.getOponnent().getPlayer(),gamePlayer.getGame(),1.0F, LocalDateTime.now());

                scoreRepository.save(score1);
                scoreRepository.save(score2);
            }
            if(stateNew == "TIE"){
                Score score1 = new Score( gamePlayer.getPlayer(),gamePlayer.getGame(),0.5F, LocalDateTime.now());
                Score score2 = new Score( gamePlayer.getOponnent().getPlayer(),gamePlayer.getGame(),0.5F, LocalDateTime.now());

                scoreRepository.save(score1);
                scoreRepository.save(score2);
            }
        }
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {
        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Usted no esta logueado."), HttpStatus.UNAUTHORIZED);

        Player player = playerRepository.findByUserName(authentication.getName());

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        if (gamePlayer == null)
            return new ResponseEntity<>(makeMap("error", "no hay player."), HttpStatus.UNAUTHORIZED);

        if (gamePlayer.getPlayer().getId() != player.getId())
            return new ResponseEntity<>(makeMap("error", "error gameplayer."), HttpStatus.UNAUTHORIZED);

        if (gamePlayer.getShips().size() > 0)
            return new ResponseEntity<>(makeMap("error", "Already has ships placed")
                    , HttpStatus.FORBIDDEN);

        ships.forEach(ship -> ship.setGamePlayer(gamePlayer));
        ships.forEach(ship -> shiprepository.save(ship));

        return new ResponseEntity<>(makeMap("OK", "Let´s go!"), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createSalvos(@PathVariable long gamePlayerId, @RequestBody Salvo salvos, Authentication authentication) {

        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Usted no esta logueado."), HttpStatus.UNAUTHORIZED);

        Player player = playerRepository.findByUserName(authentication.getName());

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        if (gamePlayer == null)
            return new ResponseEntity<>(makeMap("error", "player no logueado"), HttpStatus.UNAUTHORIZED);


        if (gamePlayer.getPlayer().getId() != player.getId())
            return new ResponseEntity<>(makeMap("error", "Problemas con el gamePlayer")
                    , HttpStatus.UNAUTHORIZED);


        if (gamePlayer.getSalvos().stream().anyMatch(salvo -> salvo.getTurnNumber() == salvos.getTurnNumber())) {
            return new ResponseEntity<>(makeMap("error", "Already has salvoes done")
                    , HttpStatus.FORBIDDEN);
        }

        salvos.setGamePlayer(gamePlayer);
        salvoRepository.save(salvos);

        if (gamePlayer.getSalvos().isEmpty()) {
            salvos.setTurnNumber(1);
        }

        GamePlayer opponent = getOpponent(gamePlayer);
        if (opponent != null) {
            if (gamePlayer.getSalvos().size() <= opponent.getSalvos().size()) {
                salvos.setTurnNumber(gamePlayer.getSalvos().size() + 1);
                salvos.setGamePlayer(gamePlayer);
            } else {
                return new ResponseEntity<>(makeMap("error", "ya tienes salvos"), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(makeMap("error", "no hay oponentes"), HttpStatus.FORBIDDEN);
        }

        salvoRepository.save(salvos);
        return new ResponseEntity<>(makeMap("OK", "Salvo created"), HttpStatus.CREATED);
    }

    public GamePlayer getOpponent(GamePlayer self) {
        return self.getGame().getGamePlayers()
                .stream()
                .filter(gamePlayer1 -> gamePlayer1.getId() != self.getId())
                .findFirst()
                .orElse(null);
    }

    //////////////////////////////////////////////////////////
    //POST MAPPING

    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> registerGame(Authentication authentication) {
        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Usted no esta logueado."), HttpStatus.UNAUTHORIZED);

        Player player = playerRepository.findByUserName(authentication.getName());

        if (player == null)
            return new ResponseEntity<>(makeMap("error", "Usted no esta logueado, INAUTORIZADO PARA CONTINUAR."), HttpStatus.UNAUTHORIZED);

        Game game = gameRepository.save(new Game(LocalDateTime.now()));
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, player, LocalDateTime.now()));
        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/game/{game_id}/players")
    public ResponseEntity<Map<String, Object>> registerJoinGame(@PathVariable Long game_id, Authentication authentication) {
        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Usted no esta logueado."), HttpStatus.UNAUTHORIZED);

        Player player = playerRepository.findByUserName(authentication.getName());

        Game game = gameRepository.findById(game_id).get();

        if (game == null)
            return new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.FORBIDDEN);

        //recorro Game para ver si esta completo (size) y le digo que compare si es mayor o igual a 2, retorna un full
        if (game.getGamePlayers().size() >= 2) {
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, player, LocalDateTime.now()));
        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);

    }


    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /////////////////////////////////////////////////////////////////////////////
    //Realizo las funciones de getShipsLocations y get SalvoLocationsOpponent para poder tener los HITS


    public List<String> getShipsLocations(GamePlayer gamePlayer) {
        return gamePlayer.getShips().stream().map(ship -> ship.getLocations())
                .flatMap(l -> l.stream())
                .collect(Collectors.toList());
    }

    public List<String> getSalvoLocationsOpponent(int turno, GamePlayer gamePlayer) {
        Optional<Salvo> salvo = getOpponent(gamePlayer).getSalvos()
                .stream()
                .filter(s -> s.getTurnNumber() == turno)
                .findFirst();
        if (salvo.isPresent())
            return salvo.get().getSalvoLocations();
        return new ArrayList<>();
    }

    //Hits: Matcheo entre los barcos de un gamePlayer y las localizaciones
    // de los salvoes del oponente en un turno
    public List<String> getHitsPorTurno(GamePlayer gamePlayer, int turno) {
        return
                getShipsLocations(gamePlayer)
                        .stream()
                        .filter(s -> getSalvoLocationsOpponent(turno, gamePlayer)
                                .stream().anyMatch(so -> so.equals(s)))
                        .collect(Collectors.toList());
    }

    //Matcheo entre los barcos de un gamePlayer y todas las locaciones de los
    //salvoes hasta un turno
    public List<String> getHitsHastaNTurno(GamePlayer gamePlayer, int turno) {
        List<String> lista = new ArrayList<>();
        int i = 1;
        while (i <= turno) {
            lista.addAll(getHitsPorTurno(gamePlayer, i));
            i++;
        }
        return lista;
    }


    // Forma el map de hit
    public Map<String, Object> hitPorTurnoDTO(GamePlayer gamePlayer, int turno) {
        Map<String, Object> dto = new LinkedHashMap<>();
        List<String> hits = this.getHitsPorTurno(gamePlayer, turno);
        dto.put("turn", turno);
        dto.put("hitLocations", hits);
        dto.put("damages", this.getDamages(gamePlayer, turno));
        dto.put("missed", 5 - this.getHitsPorTurno(gamePlayer, turno).size());
        return dto;
    }

    //Forma la lista de los map de hits (de un gameplayer)
    public List<Map<String, Object>> DTOIterador(GamePlayer gamePlayer) {
        List<Map<String, Object>> listaDeDtos = new ArrayList<>();
        int i = 1;
        if(gamePlayer != null){
            while ( i <= gamePlayer.getSalvos().size()) {
                listaDeDtos.add(hitPorTurnoDTO(gamePlayer, i));
                i++;
            }
        }
        return listaDeDtos;
    }


    //Forma el map para obtener los DAMAGES
    public Map<String, Object> getDamages(GamePlayer gamePlayer, int turno) {
        Map<String, Object> damages = new LinkedHashMap<>();
        List<String> hits = this.getHitsPorTurno(gamePlayer, turno);
        List<String> hitsHastaNTurno = this.getHitsHastaNTurno(gamePlayer, turno);
        gamePlayer.getShips().forEach(ship -> damages.put(ship.getType() + "Hits", ship.getHitsLocation(hits)));
        gamePlayer.getShips().forEach(ship -> damages.put(ship.getType(), ship.getHitsLocation(hitsHastaNTurno)));
        return damages;
    }

    public String getGameState(GamePlayer gamePlayer) {

            GamePlayer gamePlayerOpponent = getOpponent(gamePlayer);
           // float newScore = gamePlayer.getGame().getScores().stream().count();

            if (gamePlayer.getShips().isEmpty()) {
                return "PLACESHIPS";
            }

            if (getOpponent(gamePlayer) == null || getOpponent(gamePlayer).getShips().isEmpty()
            )
                return "WAITINGFOROPP";

            if(gamePlayer.getSalvos().size() == gamePlayerOpponent.getSalvos().size()
                && gamePlayer.perdioTodosSusBarcos()
                &&  gamePlayer.getOponnent().perdioTodosSusBarcos()) {

               System.out.println("empate");
                System.out.println(gamePlayer.perdioTodosSusBarcos());
                System.out.println(gamePlayer.cantidadDeBarcosHundidos());

                System.out.println(gamePlayer.getOponnent().perdioTodosSusBarcos());
                System.out.println(gamePlayer.getOponnent().cantidadDeBarcosHundidos());

                System.out.println(gamePlayer.getPlayer().getUserName());
                System.out.println(gamePlayerOpponent.getPlayer().getUserName());
                return "TIE";
            }


            System.out.println( "else  self "+gamePlayer.cantidadDeBarcosHundidos());
            System.out.println( "else  opponent "+gamePlayerOpponent.cantidadDeBarcosHundidos());

            if (getOpponent(gamePlayer).perdioTodosSusBarcos())
                return "WON";

            if (gamePlayer.perdioTodosSusBarcos())
                return "LOST";

            if (gamePlayer.getSalvos().size() <= getOpponent(gamePlayer).getSalvos().size())
                return "PLAY";

            else if (gamePlayer.getSalvos().size() > getOpponent(gamePlayer).getSalvos().size())
                return "WAIT";
                System.out.println("No entro en ninguna");
            return "WAIT";
        }
}


