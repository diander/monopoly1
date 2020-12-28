package com.game.monopoly.controller;

/*************************************************************************************
 * 										CONTROLLER									 *
 *************************************************************************************/

import com.game.monopoly.domain.User;
import com.game.monopoly.logic.Monopoly;
import com.game.monopoly.logic.TokenType;
import com.game.monopoly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
//@EnableAutoConfiguration
public class MonopolyController {
    
	public static Monopoly gameInstance;

    @Autowired
    private UserService userService;

	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource get() {
	    //if (gameInstance == null) gameInstance = new Monopoly();

		return getHttp("game.html");
    }

    public FileSystemResource getHttp(String fileName) {
        return new FileSystemResource("./src/main/resources/static/" + fileName);
    }

    @RequestMapping(value = "/static/css/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getHttpCss(@PathVariable("file_name") String fileName) {
        return new FileSystemResource("./src/main/resources/static/css/" + fileName);
    }

    @RequestMapping(value = "/static/img/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getHttpImg(@PathVariable("file_name") String fileName) {
        return new FileSystemResource("./src/main/resources/static/img/" + fileName);
    }

    @RequestMapping(value = "/static/js/{file_name}", method = RequestMethod.GET, produces = "text/javascript")
    @ResponseBody
    public FileSystemResource getHttpJs(@PathVariable("file_name") String fileName) {
        return new FileSystemResource("./src/main/resources/static/js/" + fileName);
    }

    @RequestMapping(value = "/static/ttf/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getHttpTtf(@PathVariable("file_name") String fileName) {
        return new FileSystemResource("./src/main/resources/static/ttf/" + fileName);
    }

    @RequestMapping(value = "/getUsername", method = RequestMethod.GET)
    public @ResponseBody
    String getUsername(
            @AuthenticationPrincipal User user
    ){
        return user.getUsername();
    }
    
    // Operations
    @RequestMapping(value = "/state", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly getState() {
    	return gameInstance;
    }

    @RequestMapping(value = "/join", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly join(
            @AuthenticationPrincipal User user,
    		@RequestParam String name,
    		@RequestParam TokenType token
    ) {
    	gameInstance.join(user.getUsername(), token);
    	return gameInstance;
    }

    @RequestMapping(value = "/quit", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly quit(
            @RequestParam String name) {
        gameInstance.quit(name);
        return gameInstance;
    }
    
    @RequestMapping(value = "/start", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly start(@RequestParam int timelimit) {
    	gameInstance.start(timelimit);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/endturn", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly endTurn() {
    	gameInstance.endTurn();
    	return gameInstance;
    }
    
    @RequestMapping(value = "/buyproperty", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly buyProperty() {
    	gameInstance.buyProperty();
    	return gameInstance;
    }
    
    @RequestMapping(value = "/selltobank", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly sellToBank(
    		@RequestParam int propertyIndex) {
    	gameInstance.sellToBank(propertyIndex);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/selltoplayers", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly sellToPlayers(
    		@RequestParam int propertyIndex,
    		@RequestParam double startingBid) {
    	gameInstance.sellToPlayers(propertyIndex, startingBid);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/passproperty", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly passProperty(
    		@RequestParam int tileIndex) {
    	gameInstance.auctionProperty(gameInstance.getBank(), tileIndex, 0.0);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/setbid", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly setBid(
    		@RequestParam String username,
    		@RequestParam double bid) {
    	gameInstance.setBid(username, bid);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/buymortgage", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly buyMortgage() {
    	gameInstance.buyMortgage();
    	return gameInstance;
    }
    
    @RequestMapping(value = "/liftmortgage", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly liftMortgage(
    		@RequestParam int propertyIndex) {
    	gameInstance.liftMortgage(propertyIndex);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/upgradeprop", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly upgradeProperty(
    		@RequestParam int index) {
    	gameInstance.upgradeProperty(index);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/degradeprop", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly degradeProperty(
    		@RequestParam int index) {
    	gameInstance.degradeProperty(index);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/jailchoice", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly jailChoice(
    		@RequestParam boolean choice) {
    	gameInstance.jailChoice(choice);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/usefreecard", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly useFreeCard() {
    	gameInstance.useFreeCard();
    	return gameInstance;
    }
    
    @RequestMapping(value = "/hackedroll", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly hackedRoll(
    		@RequestParam int val1,
    		@RequestParam int val2) {
    	gameInstance.hackedRoll(val1, val2);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/setcheatmodeon", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly hackedRoll(
    		@RequestParam boolean val) {
    	gameInstance.setCheatModeOn(val);
    	return gameInstance;
    }
    
    @RequestMapping(value = "/resetgame", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly resetGame() {
    	gameInstance = new Monopoly();
    	return gameInstance;
    }
    
    @RequestMapping(value = "/endgame", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly endGame(
    ){
	    if (!gameInstance.isEndGame()){
	        gameInstance.endGame();
        }

	    if (!gameInstance.isWinnerCounted()){

            String winner = gameInstance.getWinner();

            userService.winCount(winner);

            List<String> defeatedUsers = gameInstance.getDefeatedPlayersNames();
            userService.defineDefeats(defeatedUsers);

            gameInstance.setWinnerCounted(true);
        }

    	return gameInstance;
    }
    
    @RequestMapping(value = "/ackcard", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Monopoly ackCard() {
    	gameInstance.acknowledgeCard();
    	return gameInstance;
    }

}