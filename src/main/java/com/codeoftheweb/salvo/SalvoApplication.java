package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean //The @Bean annotation in the code above tells Spring to run initData() and saves the value returned
    public CommandLineRunner initData(PlayerRepository repository,
                                      GameRepository gamerepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shiprepository,
                                      SalvoRepository salvoRepository,
                                      ScoreRepository scoreRepository) {
        return (args) -> {

            //PLAYERS
            Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
            Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
            Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
            Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));

            //GAME CREATION
            Game game1 = new Game(LocalDateTime.now());
            Game game2 = new Game(LocalDateTime.now().plusMinutes(30));
            Game game3 = new Game(LocalDateTime.now().plusMinutes(60));
            Game game4 = new Game(LocalDateTime.now().plusHours(3));
            Game game5 = new Game(LocalDateTime.now().plusHours(4));
            Game game6=  new Game(LocalDateTime.now().plusHours(5));


            //GAME PLAYER
            GamePlayer gamePlayer1 = new GamePlayer(game1, player1,LocalDateTime.now());
            GamePlayer gamePlayer2 = new GamePlayer(game1, player2,LocalDateTime.now().plusMinutes(30));
            GamePlayer gamePlayer3 = new GamePlayer(game2, player1,LocalDateTime.now().plusMinutes(60));
            GamePlayer gamePlayer4 = new GamePlayer(game2, player2,LocalDateTime.now().plusMinutes(60));
            GamePlayer gamePlayer5 = new GamePlayer(game3, player2,LocalDateTime.now().plusHours(2));
            GamePlayer gamePlayer6 = new GamePlayer(game3, player4,LocalDateTime.now().plusHours(2));
            GamePlayer gamePlayer7 = new GamePlayer(game4, player2,LocalDateTime.now().plusHours(3));
            GamePlayer gamePlayer8 = new GamePlayer(game4, player1,LocalDateTime.now().plusHours(3));
            GamePlayer gamePlayer9 = new GamePlayer(game5, player4,LocalDateTime.now().plusHours(4));
            GamePlayer gamePlayer10 = new GamePlayer(game5, player1,LocalDateTime.now().plusHours(4));
            GamePlayer gamePlayer11 = new GamePlayer(game6, player3,LocalDateTime.now().plusHours(5));
            GamePlayer gamePlayer12 = new GamePlayer(game6, player2 ,LocalDateTime.now().plusHours(5));

            //LOCATION SHIPS

            // game 1
            Ship ship = new Ship("destroyer", Arrays.asList("H2", "H3", "H4"), gamePlayer1);
            Ship ship2 = new Ship("submarine", Arrays.asList("E1", "F1", "G1"), gamePlayer1);
            Ship ship3 = new Ship("patrol boat", Arrays.asList("B4", "B5"), gamePlayer1);
            Ship ship4 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer2);
            Ship ship5 = new Ship("patrol boat", Arrays.asList("F1", "F2"), gamePlayer2);

            // game 2
            Ship ship6 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer3);
            Ship ship7 = new Ship("patrol boat", Arrays.asList("C6", "C7"), gamePlayer3);
            Ship ship8 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"), gamePlayer4);
            Ship ship9 = new Ship("patrol boat", Arrays.asList("G6", "H6"), gamePlayer4);

            // game 3
            Ship ship10 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer5);
            Ship ship11 = new Ship("patrol boat", Arrays.asList("C6", "C7"), gamePlayer6);
            Ship ship12 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"), gamePlayer5);
            Ship ship13 = new Ship("patrol boat", Arrays.asList("G6", "H6"), gamePlayer6);

            // game 4
            Ship ship14 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer7);
            Ship ship15 = new Ship("patrol boat", Arrays.asList("C6", "C7"), gamePlayer8);
            Ship ship16 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"), gamePlayer7);
            Ship ship17 = new Ship("patrol boat", Arrays.asList("G6", "H6"), gamePlayer8);

            // game 5
            Ship ship18 = new Ship("submarine", Arrays.asList("G6", "H6"), gamePlayer9);
            Ship ship19 = new Ship("destroyer", Arrays.asList("C6", "C7"), gamePlayer10);
            Ship ship20 = new Ship("patrol boat", Arrays.asList("B5", "C5", "D5"), gamePlayer9);
            Ship ship21 = new Ship("patrol boat", Arrays.asList("A2", "A3", "A4"), gamePlayer10);

            // game 6
            Ship ship22 = new Ship("patrol boat", Arrays.asList("G6", "H6"), gamePlayer11);
            Ship ship23 = new Ship("patrol boat", Arrays.asList("G6", "H6"), gamePlayer12);
            Ship ship24 = new Ship("patrol boat", Arrays.asList("G6", "H6"), gamePlayer11);
            Ship ship25 = new Ship("patrol boat", Arrays.asList("G6", "H6"), gamePlayer12);

            //SALVO
            Salvo salvo = new Salvo(1, Arrays.asList("B5", "C5", "D5"), gamePlayer1);
            Salvo salvo2 = new Salvo(1, Arrays.asList("B4", "B5", "B6"), gamePlayer2);

            Salvo salvo3 = new Salvo(2, Arrays.asList("F2", "D5"), gamePlayer1);
            Salvo salvo4 = new Salvo(2, Arrays.asList("E1", "H3", "A2"), gamePlayer2);

            Salvo salvo5 = new Salvo(1, Arrays.asList("A2", "A4", "G6"), gamePlayer3);
            Salvo salvo6 = new Salvo(1, Arrays.asList("B5", "D5", "C7"), gamePlayer4);

            Salvo salvo7 = new Salvo(2, Arrays.asList("A3", "H6"), gamePlayer3);
            Salvo salvo8 = new Salvo(2, Arrays.asList("C5", "C6"), gamePlayer4);

            Salvo salvo9 = new Salvo(1, Arrays.asList("G6", "H6", "A4"), gamePlayer5);
            Salvo salvo10 = new Salvo(1, Arrays.asList("H1", "H2", "H3"), gamePlayer6);

            Salvo salvo11 = new Salvo(2, Arrays.asList("A2", "A3", "D8"), gamePlayer7);
            Salvo salvo12 = new Salvo(2, Arrays.asList("E1", "F2", "G3"), gamePlayer8);

            Salvo salvo13 = new Salvo(1, Arrays.asList("A2", "A3", "D8"), gamePlayer9);
            Salvo salvo14 = new Salvo(1, Arrays.asList("E1", "F2", "G3"), gamePlayer10);

            Salvo salvo15 = new Salvo(2, Arrays.asList("A2", "A3", "D8"), gamePlayer11);
            Salvo salvo16 = new Salvo(2, Arrays.asList("E1", "F2", "G3"), gamePlayer12);

            Score score = new Score(player1, game1, 1.0F, LocalDateTime.now().plusMinutes(30));
            Score score2 = new Score(player2, game1, 0.0F, LocalDateTime.now().plusMinutes(30));
            Score score3 = new Score(player1, game2, 0.5F, LocalDateTime.now().plusMinutes(30));
            Score score4 = new Score(player2, game2, 0.5F, LocalDateTime.now().plusMinutes(30));
            Score score5 = new Score(player2, game3, 1.0F, LocalDateTime.now().plusMinutes(30));
            Score score6 = new Score(player4, game3, 0.0F, LocalDateTime.now().plusMinutes(30));
            Score score7 = new Score(player2, game4, 0.5F, LocalDateTime.now().plusMinutes(30));
            Score score8 = new Score(player1, game4, 0.5F, LocalDateTime.now().plusMinutes(30));
            //Score score9 = new Score(player4, game5, 0.0F, LocalDateTime.now().plusMinutes(30));
            //Score score10 = new Score(player1, game5, 0.0F, LocalDateTime.now().plusMinutes(30));
            //Score score11 = new Score(player1, game6, 0.0F, LocalDateTime.now().plusMinutes(30));
            //Score score12 = new Score(player1, game6, 0.0F, LocalDateTime.now().plusMinutes(30));

            //REPOSITORIES
            repository.save(player1);
            repository.save(player2);

            //TIE
            Game game7=  new Game(LocalDateTime.now().plusHours(5));
            gamerepository.save(game7);

            GamePlayer gamePlayer13 = new GamePlayer(game7, player1,LocalDateTime.now());
            GamePlayer gamePlayer14 = new GamePlayer(game7, player2 ,LocalDateTime.now());
            gamePlayerRepository.save(gamePlayer13);
            gamePlayerRepository.save(gamePlayer14);

            Ship shipTied1 = new Ship("destroyer", Arrays.asList("H2", "H3", "H4"), gamePlayer13);
            Ship shipTied2 = new Ship("submarine", Arrays.asList("E1", "F1", "G1"), gamePlayer14);
            Salvo salvoTied1 = new Salvo(1, Arrays.asList("E1", "F1", "G1"), gamePlayer13);
            Salvo salvoTied2 = new Salvo(1, Arrays.asList("H2", "H3", "H4"), gamePlayer14);
            // Player

            repository.save(player3);
            repository.save(player4);



            shiprepository.save(shipTied1);
            shiprepository.save(shipTied2);
            salvoRepository.save(salvoTied1);
            salvoRepository.save(salvoTied2);





            // Game
            gamerepository.save(game1);
            gamerepository.save(game2);
            gamerepository.save(game3);
            gamerepository.save(game4);
            gamerepository.save(game5);
            gamerepository.save(game6);


            // Game Player
            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);
            gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
            gamePlayerRepository.save(gamePlayer5);
            gamePlayerRepository.save(gamePlayer6);
            gamePlayerRepository.save(gamePlayer7);
            gamePlayerRepository.save(gamePlayer8);
            gamePlayerRepository.save(gamePlayer9);
            gamePlayerRepository.save(gamePlayer10);
            gamePlayerRepository.save(gamePlayer11);
            gamePlayerRepository.save(gamePlayer12);


            // Ships
            shiprepository.save(ship);
            shiprepository.save(ship2);
            shiprepository.save(ship3);
            shiprepository.save(ship4);
            shiprepository.save(ship5);
            shiprepository.save(ship6);
            shiprepository.save(ship7);
            shiprepository.save(ship8);
            shiprepository.save(ship9);
            shiprepository.save(ship10);
            shiprepository.save(ship11);
            shiprepository.save(ship12);
            shiprepository.save(ship13);
            shiprepository.save(ship14);
            shiprepository.save(ship15);
            shiprepository.save(ship16);
            shiprepository.save(ship17);
            shiprepository.save(ship18);
            shiprepository.save(ship19);
            shiprepository.save(ship20);
            shiprepository.save(ship21);
            shiprepository.save(ship22);
            shiprepository.save(ship23);
            shiprepository.save(ship24);
            shiprepository.save(ship25);

            // Salvoes
            salvoRepository.save(salvo);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);
            salvoRepository.save(salvo6);
            salvoRepository.save(salvo7);
            salvoRepository.save(salvo8);
            salvoRepository.save(salvo9);
            salvoRepository.save(salvo10);
            salvoRepository.save(salvo11);
            salvoRepository.save(salvo12);
            salvoRepository.save(salvo13);
            salvoRepository.save(salvo14);
            salvoRepository.save(salvo15);
            salvoRepository.save(salvo16);
            //Scores
            scoreRepository.save(score);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);
            scoreRepository.save(score7);
            scoreRepository.save(score8);
           // scoreRepository.save(score9);
           // scoreRepository.save(score10);
           // scoreRepository.save(score11);
           // scoreRepository.save(score12);
        };

    }

}

// SEGURIDAD

    @Configuration
    class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        PlayerRepository playerRepository;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(inputUserName -> {
                Player player = playerRepository.findByUserName(inputUserName);
                if (player != null) {
                    return new User(player.getUserName(), player.getPassword(),
                            AuthorityUtils.createAuthorityList("USER"));
                } else {
                    throw new UsernameNotFoundException("Unknown user: " + inputUserName);
                }
            });
        }
    }

    @EnableWebSecurity
    @Configuration
    class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/api/games").permitAll()
                    .antMatchers("/api/players").permitAll()
                    .antMatchers("/web/**").permitAll()
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/**").permitAll()
                    .antMatchers("/api/game_view/*").hasAuthority("USER")
                    .and()

                    .formLogin()
                    .usernameParameter("name")
                    .passwordParameter("pwd")
                    .loginPage("/api/login");

            http.logout().logoutUrl("/api/logout");

            // turn off checking for CSRF tokens
            http.csrf().disable();
            http.headers().frameOptions().disable();//allow use of frame to same origin urls

            // if user is not authenticated, just send an authentication failure response
            http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

            // if login is successful, just clear the flags asking for authentication
            http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

            // if login fails, just send an authentication failure response
            http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

            // if logout is successful, just send a success response
            http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        }

        private void clearAuthenticationAttributes(HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }
        }
    }
