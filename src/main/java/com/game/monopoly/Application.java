package com.game.monopoly;

import com.game.monopoly.controller.MonopolyController;
import com.game.monopoly.logic.Monopoly;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void  main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        MonopolyController.gameInstance = new Monopoly();
    }

}
