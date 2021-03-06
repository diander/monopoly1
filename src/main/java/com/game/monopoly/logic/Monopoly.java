package com.game.monopoly.logic;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*************************************************************************************
 * 										FACADE										 *
 *************************************************************************************/

public class Monopoly {

	private GamePhase phase = GamePhase.WAITING;

	private Board board;

	private Bank bank;

	private ArrayList<Player> players;

	private int currentPlayerIndex;

	private int numberBankrupt = 0;

	private boolean inAuction;

	private double highestBid = 0;

	private int highestBidderIndex = -1;

	private int auctionTimeLeft = 10;

	private String nameOfAuctionTile = "";

	private String cardString = "";
	private Card currentCard;

	private int rolledValue = 0;

	private boolean rolledDoubles = false;

	private int numberOfHouses = 0;

	private LocalTime endTime;

	private Timer gameTimer;

	private int timeLeft;

	private Timer turnTimer;
	private int turnTimeLeft;

	private String winner;

	private boolean isWinnerCounted = false;

	private boolean cheatModeOn = false;

	public Monopoly() {
		board = new Board();
		bank = new Bank();
		players = new ArrayList<Player>();
		gameTimer = new Timer();
		turnTimer = new Timer();
	}

	public GamePhase getPhase() {
		return phase;
	}

	public Board getBoard() {
		return board;
	}

	public double getHighestBid() {
		return highestBid;
	}

	public double getAuctionTimeLeft() {
		return auctionTimeLeft;
	}

	public String getNameOfAuctionTile() {
		return nameOfAuctionTile;
	}

	public int getHighestBidderIndex() {
		return highestBidderIndex;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public int getTurnTimeLeft() {
		return turnTimeLeft;
	}

	public Bank getBank() {
		return bank;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}

	public String getCardString() {
		return cardString;
	}

	public String getWinner() {
		return winner;
	}

	public boolean isWinnerCounted() { return isWinnerCounted; }

	public void setWinnerCounted(boolean winnerCounted) { isWinnerCounted = winnerCounted; }

	public boolean getCheatModeOn() {
		return cheatModeOn;
	}

	public void setCheatModeOn(boolean val) {
		cheatModeOn = val;
	}

	public void useFreeCard(){

		Player currentPlayer = players.get(currentPlayerIndex);

		if(currentPlayer.getHasFreeJailCard() && currentPlayer.isJailed()){

			currentPlayer.remainingTurnsJailed = 0;
			currentPlayer.setHasFreeJailCard(false);
			currentPlayer.getToken().moveBy(1);
			endTurn();

		}

	}

	public void landOnChance(Card chanceCard){

		if (phase == GamePhase.ENDGAME){
			return;
		}

		Player currentPlayer = players.get(currentPlayerIndex);

		int currentTileIndex = currentPlayer.getToken().getTileIndex();

		Tile currentTile = board.getTiles().get(currentTileIndex);

		//Player draws chance card that gives them credit
		if(chanceCard.getPayment() > 0){ 

			bank.transfer(currentPlayer, (double)chanceCard.getPayment());

		}

		//Player draws chance card in which they either have to pay bank or other players a penalty
		if(chanceCard.getCardIndex() != 5 && chanceCard.getDefPent() > 0){

			//When player draws card at index 3 they have to pay each player $50
			if(chanceCard.getCardIndex() == 3){

				for(Player p : players){

					if(!p.equals(currentPlayer)){

						currentPlayer.transfer(p, (double)chanceCard.getDefPent());

					}

				}

			}else{

				currentPlayer.transfer(bank, (double)chanceCard.getDefPent());

			}


		}

		if(chanceCard.getCardIndex() == 5){

			//When player draws the chance card at index 5
			int totalPenalty = 0;

			int sumHouses = 0;

			int sumHotel = 0;

			for(int i : currentPlayer.getDeeds()){

				sumHouses += board.getTiles().get(i).getNumHouses();

				if(board.getTiles().get(i).getHasHotel()){

					sumHotel++;

				}

			}

			totalPenalty = (sumHouses*chanceCard.getDefPent()) + (sumHotel*chanceCard.getSecdPent());

			//If player doesn't have hotels or houses, they receive no penalty
			currentPlayer.transfer(bank, (double) totalPenalty);

		}

		//Move player's token on board depending on which card they draw.
		if(chanceCard.moveToIndex() != -1){

			double currentMoney = currentPlayer.getMoney();

			if(chanceCard.getCardIndex() == 0){

				//Cannot collect $200 when player goes to jail

				currentPlayer.getToken().moveTo(chanceCard.moveToIndex());

				if(currentMoney == currentMoney + 200){

					currentPlayer.transfer(bank, 200.0);

				}

				//Player is now in jail and jail rules apply				
				currentPlayer.jail();

			}

			if(chanceCard.getCardIndex() == 12){
				currentPlayer.getToken().moveBy(-1*chanceCard.moveToIndex());
				currentTileIndex = currentPlayer.getToken().getTileIndex();
				currentTile = board.getTiles().get(currentTileIndex);
				tileOperation(currentTile, currentPlayer);
				return;
			}else{
				currentPlayer.getToken().moveTo(chanceCard.moveToIndex());
				currentTileIndex = currentPlayer.getToken().getTileIndex();
				currentTile = board.getTiles().get(currentTileIndex);
				tileOperation(currentTile, currentPlayer);
				return;
			}

		}

		//Player draws get-out-of-jail-free card
		if(chanceCard.getCardIndex() == 2){

			currentPlayer.setHasFreeJailCard(true);

		}

		//Utility indices are: 12, 28
		if(chanceCard.getCardIndex() == 4){
			if(currentTileIndex < 12 || currentTileIndex >= 28){
				currentPlayer.getToken().moveTo(12);
				currentTileIndex = currentPlayer.getToken().getTileIndex();
				currentTile = board.getTiles().get(currentTileIndex);
				if (currentTile.hasOwner()) {
					if (currentTile.getOwnerIndex() != currentPlayerIndex) {
						payRent();
					}
				}
				else {
					phase = GamePhase.BUY_PROPERTY;
					return;
				}

			}else{
				currentPlayer.getToken().moveTo(28);
				currentTileIndex = currentPlayer.getToken().getTileIndex();
				currentTile = board.getTiles().get(currentTileIndex);
				if (currentTile.hasOwner()) {
					if (currentTile.getOwnerIndex() != currentPlayerIndex) {
						payRent();
					}
				}
				else {
					phase = GamePhase.BUY_PROPERTY;
					return;
				}

			}

		}

		//Railroad Indices are: 5, 15, 25, 35
		if(chanceCard.getCardIndex() == 6){

			if(currentTileIndex < 5 || currentTileIndex >= 35)
				currentPlayer.getToken().moveTo(5);
			else if(currentTileIndex < 15 || currentTileIndex >= 5)
				currentPlayer.getToken().moveTo(15);
			else if(currentTileIndex < 25 || currentTileIndex >= 15)
				currentPlayer.getToken().moveTo(25);
			else
				currentPlayer.getToken().moveTo(35);

			currentTileIndex = currentPlayer.getToken().getTileIndex();
			currentTile = board.getTiles().get(currentTileIndex);
			if (currentTile.hasOwner()) {
				if (currentTile.getOwnerIndex() != currentPlayerIndex) {
					payRent();
					payRent();
				}
			}
			else {
				phase = GamePhase.BUY_PROPERTY;
				return;
			}

		}

		startManagement();

	}

	public void landOnCommunity(Card comChestCard){

		Player currentPlayer = players.get(currentPlayerIndex);

		//Player draws community chest card that gives them credit
		if(comChestCard.getPayment() > 0){ 

			bank.transfer(currentPlayer, (double)comChestCard.getPayment());

		}

		//Player draws community chest card in which they either have to pay bank or other players a penalty
		if(comChestCard.getCardIndex() != 5 && comChestCard.getDefPent() > 0){

			//When player draws card at index 3, each other player pays him $50
			if(comChestCard.getCardIndex() == 3){

				for(Player p : players){

					if(!p.equals(currentPlayer)){

						p.transfer(currentPlayer, (double)comChestCard.getDefPent());

					}

				}

			}else{

				currentPlayer.transfer(bank, (double)comChestCard.getDefPent());

			}			

		}

		//Player draws get-out-of-jail-free card
		if(comChestCard.getCardIndex() == 2){

			currentPlayer.setHasFreeJailCard(true);

		}

		if(comChestCard.getCardIndex() == 5){

			//When player draws the chance card at index 5
			int totalPenalty = 0;

			int sumHouses = 0;

			int sumHotel = 0;

			for(int i : currentPlayer.getDeeds()){

				sumHouses += board.getTiles().get(i).getNumHouses();

				if(board.getTiles().get(i).getHasHotel()){

					sumHotel++;

				}

			}

			totalPenalty = (sumHouses*comChestCard.getDefPent()) + (sumHotel*comChestCard.getSecdPent());

			//If player doesn't have hotels or houses, they receive no penalty
			currentPlayer.transfer(bank, (double) totalPenalty);

		}

		//Move player's token on board depending on which card they draw.
		if(comChestCard.moveToIndex() != -1){

			double currentMoney = currentPlayer.getMoney();

			if(comChestCard.getCardIndex() == 0){

				//Cannot collect $200 when player goes to jail

				if(currentMoney == currentMoney + 200){

					currentPlayer.transfer(bank, 200.0);

				}

				//Player is now in jail and jail rules apply				
				currentPlayer.jail();

			}

			currentPlayer.getToken().moveTo(comChestCard.moveToIndex());

		}

		//Player draws get-out-of-jail-free card
		if(comChestCard.getCardIndex() == 2){

			currentPlayer.setHasFreeJailCard(true);

		}

		startManagement();

	}

	public void setBid(String username, double bid)
	{
		int playerIndex = -1;
		bid = Math.round(bid);
		for (Player player : players){
			if (player.getName().equals(username)){
				playerIndex = players.indexOf(player);
				break;
			}
		}

		if (playerIndex == -1 || players.get(playerIndex).getMoney() < bid){
			return;
		}

		players.get(playerIndex).setBid(bid);
		if(bid > highestBid)
		{
			auctionTimeLeft = 10;
			highestBid = bid;
			highestBidderIndex = playerIndex;
		}
	}

	public boolean isEndGame(){
		return phase == GamePhase.ENDGAME;
	}

	public ArrayList<String> getDefeatedPlayersNames(){
		ArrayList<String> result = new ArrayList<>();
		for (Player player : players){
			if (!player.getName().equals(winner))
				result.add(player.getName());
		}
		return result;
	}

	public void join(String name, TokenType token) {

		for (Player player : players) {
			if (player.getName().equals(name)) {
				return;
				//throw new IllegalArgumentException("Player name already exists.");
			}
			/*if(player.getToken().getType() == token) {
				throw new IllegalArgumentException("Player token already exists.");
			}*/
		}

		if (phase != GamePhase.ENDGAME && phase != GamePhase.WAITING) {
			throw new IllegalStateException("Cannot join unless in the waiting or end phase.");
		}

		Player player = new Player(name, token);
		player.getToken().moveTo(0);
		player.index = players.size();
		players.add(player);
	}


	public void quit(String name) {

		Player quitPlayer = null;

		for (Player player : players) {
			if (player.getName().equals(name)) {
				quitPlayer = player;
				//players.remove(quitPlayer);
				break;
			}
		}

		if (quitPlayer == null) return;

		if (players.size() == 1) {
			resetGame();
			return;
		}

		// player gets removed from list
		players.remove(quitPlayer);

		// if it's quitting player's turn
		if (quitPlayer.index == currentPlayerIndex){

			// evaluate indexes of the rest players
			int i = 0;
			for (Player tempPlayer : players){
				tempPlayer.index = i;
				i++;
			}
			// moving current index left
			currentPlayerIndex--;
			// getting valid index of player whose turn comes after removed one
			currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

			if (phase != GamePhase.ENDGAME){
				startTurn();
			}
		}
		else{

			// all indexes *after* removing player will be moved to left,
			// so if current index is to the right of removing one,
			// it moves too as well as the current player
			if (quitPlayer.index < currentPlayerIndex){
				currentPlayerIndex--;
			}

			// evaluate indexes of the rest players
			int i = 0;
			for (Player tempPlayer : players){
				tempPlayer.index = i;
				i++;
			}
		}
	}

	public void start(int timeLimit) {
		if (phase == GamePhase.ENDGAME){
			return;
		}
		if (phase != GamePhase.WAITING) {
			throw new IllegalStateException("Cannot start the game unless in the waiting phase.");
		}

		endTime = LocalTime.now().plusMinutes(timeLimit);
		timeLeft = timeLimit;
		gameTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(timeLeft > 0)
					timeLeft--;
			}
		}, 60000, 60000);
		
		turnTimeLeft = 60;
		turnTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (phase != GamePhase.TURN && phase != GamePhase.BUY_PROPERTY &&
					phase != GamePhase.JAILED && phase != GamePhase.SHOWCARD) {
					return;
				}

				if(turnTimeLeft > 0)
					turnTimeLeft--;
				else
					endTurn();
			}
		}, 1000, 1000);

		if(players.size() < 2){
			throw new IllegalStateException("Must have more than 2 players to start a game.");
			//Must have more than 2 players to start a game
		}else if(players.size() > 4){
			throw new IllegalStateException("Must have 4 or less players.");
			//Must have 4 or less players
		}

		//For testing purposes
		//currentPlayerIndex = 0;

		currentPlayerIndex = (new Random()).nextInt(players.size());

		// Reset player positions on board
		for (Player player : players) {
			player.getToken().moveTo(0);
		}

		// Automatically roll the dice for the first player
		startTurn();
	}

	private void startTurn() {

		if (phase == GamePhase.ENDGAME){
			return;
		}

		if(LocalTime.now().isAfter(endTime)){
			endGame();
			return;
		}

		turnTimeLeft = 60;

		Player currentPlayer = players.get(currentPlayerIndex);

		if (currentPlayer.isJailed()) {
			phase = GamePhase.JAILED;
			return;
		}
		else if (cheatModeOn) {
			phase = GamePhase.CHEAT_ROLL;
		}
		else {
			rollDice();
		}
	}

	private void rollDice(){
		if (phase == GamePhase.ENDGAME){
			return;
		}

		phase = GamePhase.ROLLING;
		Player currentPlayer = players.get(currentPlayerIndex);

		int dieOneValue = board.getDice()[0].roll();
		int dieTwoValue = board.getDice()[1].roll();

		rolledDoubles = dieOneValue == dieTwoValue;
		rolledValue = dieOneValue + dieTwoValue;

		if (currentPlayer.isJailed())
		{
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							endJailRoll(currentPlayer, rolledDoubles);
						}
					}, 
					3000 
					);	
		}
		else {
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							doTile(currentPlayer, rolledValue);
						}
					}, 
					3000 
					);	
		}
	}

	public void hackedRoll(int val1, int val2) {
		Player currentPlayer = players.get(currentPlayerIndex);

		int dieOneValue = val1;
		int dieTwoValue = val2;
		rolledDoubles = dieOneValue == dieTwoValue;
		rolledValue = dieOneValue + dieTwoValue;

		if (currentPlayer.isJailed())
		{
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							endJailRoll(currentPlayer, rolledDoubles);
						}
					}, 
					0 
					);	
		}
		else {
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							doTile(currentPlayer, rolledValue);
						}
					}, 
					0 
					);	
		}
	}

	private void endJailRoll(Player currentPlayer, boolean rolledDoubles) {
		if (rolledDoubles) {
			currentPlayer.remainingTurnsJailed = 0;
			rolledDoubles = false;
			startManagement();
		}
		else {
			currentPlayer.remainingTurnsJailed--;
			startManagement();
			return;
		}
	}

	private void doTile(Player currentPlayer, int rolledValue) {

		int previousTileIndex = currentPlayer.getToken().getTileIndex();
		currentPlayer.getToken().moveBy(rolledValue);
		int currentTileIndex = currentPlayer.getToken().getTileIndex();
		Tile currentTile = board.getTiles().get(currentTileIndex);

		// Pass go
		if(previousTileIndex > currentTileIndex) {
			bank.transfer(currentPlayer, 200.0);
		}

		tileOperation(currentTile, currentPlayer);

	}

	private void tileOperation(Tile currentTile, Player currentPlayer){

		if (phase == GamePhase.ENDGAME){
			return;
		}

		switch (currentTile.getType()){
		case PROPERTY:
		case UTILITY:
		case RAILROAD:
			if (currentTile.hasOwner()) {
				if (currentTile.getOwnerIndex() != currentPlayerIndex) {
					payRent();
				}
			}
			else {
				phase = GamePhase.BUY_PROPERTY;
				return;
			}
			break;
		case TAXES:
			bank.payTax(currentPlayer, currentTile);
			break;
		case GOTOJAIL:
			currentPlayer.jail();
			endTurn();
			return;
		case FREEPARKING:
			bank.awardFreeParking(currentPlayer);		
			break;
		case COMMUNITYCHEST:
			boolean ignoreTwoCom = false;
			for(Player p : players){
				if(p.getHasFreeJailCard()){
					ignoreTwoCom = true;
				}					
			}
			currentCard = new Card(cardType.COMMUNITY, ignoreTwoCom);
			cardString = currentCard.getCardDesc();
			phase = GamePhase.SHOWCARD;
			return;
		case CHANCE:
			boolean ignoreTwo = false;
			for(Player p : players){
				if(p.getHasFreeJailCard()){
					ignoreTwo = true;
				}					
			}
			currentCard = new Card(cardType.CHANCE, ignoreTwo);
			cardString = currentCard.getCardDesc();
			phase = GamePhase.SHOWCARD;
			return;
		default:
			break;
		}
		
		startManagement();
	}

	void startManagement()
	{
		if (players.get(currentPlayerIndex).getDeeds().isEmpty())
		{
			endTurn();
		}
		else
		{
			phase = GamePhase.TURN;
		}
	}

	public void buyProperty(){

		Player currentPlayer = players.get(currentPlayerIndex);

		Tile currentTile = board.getTiles().get(currentPlayer.getToken().getTileIndex());

		if(!currentTile.isMortgaged()) {
			currentPlayer.transfer(bank, currentTile.propertyCost);
			currentPlayer.getDeeds().add(currentPlayer.getToken().getTileIndex());
			currentTile.setOwnerIndex(currentPlayerIndex);

			if(currentTile.isRailRoad()){

				currentPlayer.setNumRailRoadsOwned(currentPlayer.getNumRailRoadsOwned() + 1);

			}				

			if(currentTile.isUtility()){

				currentPlayer.setUtilitiesOwned(currentPlayer.getNumUtilitiesOwned() + 1);

			}

		}

		startManagement();
	}

	public void auctionProperty(Owner seller, int tileIndex, double startingBid){
		inAuction = true;
		phase = GamePhase.AUCTION;

		startingBid = (double)board.getTiles().get(tileIndex).propertyCost;
		
		highestBid = startingBid;
		highestBidderIndex = -1;
		auctionTimeLeft = 10;
		nameOfAuctionTile = board.getTiles().get(tileIndex).getName();

		while (inAuction) {
			if(auctionTimeLeft > 0)
			{
				auctionTimeLeft--;
			}
			else
			{
				if (highestBid > startingBid)
				{
					Player winner = players.get(highestBidderIndex);
					Tile tile = board.getTiles().get(tileIndex);

					seller.transfer(winner, tile, highestBid);
				}
				
				inAuction = false;

			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (Player p : players){
			p.setBid(0);
		}
		startManagement();
	}

	public void sellProperty(int propertyIndex, int recIndex, double amount){

		if(phase != GamePhase.TURN){
			throw new IllegalStateException("Not currently in TURN phase.");
		}

		Tile property = board.getTiles().get(propertyIndex);
		Player recipient = players.get(recIndex);
		Player currentPlayer = players.get(currentPlayerIndex);
		int propIndex = currentPlayer.getDeeds().indexOf(property);

		//Cannot sell a property with houses/hotels on it.
		if(property.getHasHotel() || property.numHouses > 0){
			return;
		}

		//Cannot sell a property with a mortgage on it
		if(property.isMortgaged()){
			return;
		}

		if(recipient.equals(bank)){
			recipient.transfer(currentPlayer, property.propertyCost/2);
			property.setOwnerIndex(-1);
		}else{
			recipient.transfer(currentPlayer, amount);
			recipient.getDeeds().add(propIndex);
		}

		if(board.getTiles().get(propIndex).getType() == TileType.RAILROAD) {
			int numOwned = currentPlayer.getNumRailRoadsOwned();
			currentPlayer.setNumRailRoadsOwned(numOwned - 1);

			if(!recipient.equals(bank)){
				int numOwnedRecipient = recipient.getNumRailRoadsOwned();
				recipient.setNumRailRoadsOwned(numOwnedRecipient + 1);
			}
		} else if (board.getTiles().get(propIndex).getType() == TileType.UTILITY) {
			int numOwned = currentPlayer.getNumUtilitiesOwned();
			currentPlayer.setUtilitiesOwned(numOwned - 1);

			if(!recipient.equals(bank)){
				int numOwnedRecipient = recipient.getNumUtilitiesOwned();
				recipient.setUtilitiesOwned(numOwnedRecipient + 1);
			}
		}

		currentPlayer.getDeeds().remove(propIndex);
	}

	public void upgradeProperty(int index){
		//This method will add houses and hotels depending on some cases
		if(phase != GamePhase.TURN){

			throw new IllegalStateException("Can only upgrade property in TURN phase.");

		}

		//Bank is out of houses
		if(numberOfHouses >= bank.getNumberOfHouses())
			return;

		Player currentPlayer = players.get(currentPlayerIndex);

		Tile tile = board.getTiles().get(index);

		String curTileColor = tile.color;

		if(!tile.isProperty()){
			return;
		}

		//Extract properties with same color group
		ArrayList<Tile> properties = new ArrayList<Tile>();

		//Loop through the whole board and add tiles with same color to arraylist
		for(int i = 0; i < board.getTiles().size(); i++){

			if(curTileColor.equals(board.getTiles().get(i).color)){

				properties.add(board.getTiles().get(i));

			}

		}

		properties.remove(tile);

		//Check to see if player owns all the properties in same color group
		for(Tile temp : properties){

			if(!currentPlayer.getDeeds().contains(board.getTiles().indexOf(temp))){
				return;
			}

		}

		//Can't add additional houses until houses are on other properties
		for(Tile temp : properties){

			if(tile.numHouses > temp.numHouses){
				return;
			}

		}

		if(tile.numHouses == 4){

			currentPlayer.transfer(bank, tile.hotelCost);
			tile.setHotel(true);

		}else{
			currentPlayer.transfer(bank, tile.houseCost);
			tile.numHouses++;
		}

		numberOfHouses++;

	}

	public void degradeProperty(int index){

		//This method will remove houses and hotels depending on some cases
		if(phase != GamePhase.TURN){

			throw new IllegalStateException("Can only degrade property in TURN phase.");

		}

		Player currentPlayer = players.get(currentPlayerIndex);

		Tile currentTile = board.getTiles().get(index);

		String curTileColor = currentTile.color;

		if(!currentTile.isProperty()) return;

		//Extract properties with same color group
		ArrayList<Tile> properties = new ArrayList<Tile>();

		//Loop through the whole board and add tiles with same color to arraylist
		for(int i = 0; i < board.getTiles().size(); i++){

			if(curTileColor.equals(board.getTiles().get(i).color)){

				properties.add(board.getTiles().get(i));

			}

		}

		//If there's no houses, you can't degrade property
		if(currentTile.numHouses == 0){
			return;
		}


		properties.remove(currentTile);

		//Can't remove houses unless the number of houses on all properties are same
		for(Tile temp : properties){

			if(currentTile.numHouses < temp.numHouses){
				return;
			}

		}

		if(currentTile.getHasHotel()){

			bank.transfer(currentPlayer, currentTile.hotelCost/2);
			currentTile.setHotel(false);

		}else{
			bank.transfer(currentPlayer, currentTile.houseCost/2);
			currentTile.numHouses--;
		}

		if(numberOfHouses > 0)
			numberOfHouses--;

	}

	public void liftMortgage(int propertyIndex){
		//Player lifts a mortgage by paying the full mortgage value + another 10%
		//interest

		if(phase != GamePhase.TURN){

			throw new IllegalStateException("Can only lift mortgage in TURN phase.");

		}

		Player currentPlayer = players.get(currentPlayerIndex);

		Tile currentTile = board.getTiles().get(propertyIndex);

		//Can't lift a mortgage on a upgraded property
		if(currentTile.getHasHotel() || currentTile.numHouses > 0){
			return;
		}


		//Want to check if the player actually owns the tile and has a mortgage on it
		if(currentTile.isMortgaged() &&
				currentPlayer.getDeeds().contains(currentTile)){

			currentPlayer.transfer(getBank(), currentTile.mortgageValue + (currentTile.mortgageValue * 0.1));
			currentTile.setMortgaged(false);
		}

	}

	public void buyMortgage(){

		if(phase != GamePhase.BUY_PROPERTY){

			throw new IllegalStateException("Cannot buy mortgage, not currently in BUY_PROPERTY phase.");

		}

		Player currentPlayer = players.get(currentPlayerIndex);

		int currentTileIndex = currentPlayer.getToken().getTileIndex();

		Tile currentTile = board.getTiles().get(currentTileIndex);

		if(!currentTile.isProperty() && !currentTile.isRailRoad()){
			//create error message that the current tile is not a property tile
			endTurn();
			return;
		}

		if(currentTile.isMortgaged()){
			endTurn();
			return;
		}else{
			double mortgageAmount = currentTile.mortgageValue;

			if(currentPlayer.getMoney() >= mortgageAmount) {
				currentPlayer.transfer(bank, mortgageAmount);
				//Update mortgage status
				currentTile.setMortgaged(true);
				currentPlayer.getDeeds().add(currentTileIndex);
			}

		}

		endTurn();

	}

	public void sellToPlayers(int propertyIndex, double startingBid) {
		Tile tile = board.getTiles().get(propertyIndex);

		if (startingBid < tile.propertyCost / 2) {
			return;
		}

		Runnable r = new Runnable(){
			public void run(){
				auctionProperty(players.get(currentPlayerIndex), propertyIndex, startingBid);
				startManagement();
			}
		};

		new Thread(r).start();

	}

	public void sellToBank(int propertyIndex) {
		if (phase != GamePhase.TURN) {
			throw new IllegalStateException("Not currently in TURN phase.");
		}

		Player currentPlayer = players.get(currentPlayerIndex);
		Tile property = board.getTiles().get(propertyIndex);


		if (property.getHasHotel() || property.numHouses > 0) {
			return;
		}

		if (property.isMortgaged()) {
			liftMortgage(propertyIndex);
			return;
		}

		ArrayList<Integer> deedsList = currentPlayer.getDeeds();
		int deedIndex = deedsList.indexOf(propertyIndex);
		if (deedIndex == -1) {
			return;
		}

		bank.transfer(currentPlayer, property.propertyCost / 2);

		currentPlayer.getDeeds().remove(deedIndex);
		property.setOwnerIndex(-1);
	}

	public void payRent(){

		Player currentPlayer = players.get(currentPlayerIndex);
		int tileIndex = currentPlayer.getToken().getTileIndex();
		Tile currentTile = board.getTiles().get(tileIndex);
		Player propOwner = players.get(currentTile.getOwnerIndex());

		if(currentTile.isMortgaged()){
			startManagement();
			return;
		}

		if(currentTile.getType() == TileType.PROPERTY){
			if(currentTile.numHouses == 0)
				currentPlayer.transfer(propOwner, currentTile.rent);
			else if(currentTile.numHouses == 1)
				currentPlayer.transfer(propOwner, currentTile.with1House);
			else if(currentTile.numHouses == 2)
				currentPlayer.transfer(propOwner, currentTile.with2Houses);
			else if(currentTile.numHouses == 3)
				currentPlayer.transfer(propOwner, currentTile.with3Houses);
			else if(currentTile.numHouses == 4)
				currentPlayer.transfer(propOwner, currentTile.with4Houses);
			else if(currentTile.getHasHotel())
				currentPlayer.transfer(propOwner, currentTile.withHotel);
			else{
				try {
					throw new Exception("Cannot pay rent, no such rent option available.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if(currentTile.getType() == TileType.RAILROAD){

			if(propOwner.getNumRailRoadsOwned() == 1)				
				currentPlayer.transfer(propOwner, currentTile.rent);
			else if(propOwner.getNumRailRoadsOwned() == 2)
				currentPlayer.transfer(propOwner, currentTile.with2Railroads);
			else if(propOwner.getNumRailRoadsOwned() == 3)
				currentPlayer.transfer(propOwner, currentTile.with3Railroads);
			else if(propOwner.getNumRailRoadsOwned() == 4)
				currentPlayer.transfer(propOwner, currentTile.with4Railroads);
			else{
				try {
					throw new Exception("Cannot pay rent, no such rent option available.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if(currentTile.getType() == TileType.UTILITY){

			if(propOwner.getNumUtilitiesOwned() == 1) {			
				currentPlayer.transfer(propOwner, (double)4*rolledValue);
			} else if (propOwner.getNumUtilitiesOwned() == 2) {			
				currentPlayer.transfer(propOwner, (double)10*rolledValue);
			} else {
				try {
					throw new Exception("Cannot pay rent, no such rent option available.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void jailChoice(boolean choice) {
		Player player = players.get(currentPlayerIndex);
		if (choice == true && player.getMoney() >= 50) {
			player.remainingTurnsJailed = 0;
			player.transfer(bank, 50.0);
			player.getToken().moveBy(1);
			endTurn();
		}
		else {
			rollDice();
		}
	}

	public void endTurn() {
		Player currentPlayer = players.get(currentPlayerIndex);
		if (currentPlayer.getMoney() == 0) {
			//Player needs to be able to sell whatever they can to be able to pay taxes
			//A new button on ui that says pay tax? and if you cant pay tax and have nothing to left to sell than you lose.
			//phase = GamePhase.TURN;
			//False in this case means player is bankrupt
			//removePlayer(currentPlayer);

			Runnable r = new Runnable() {
				public void run(){
					bankrupt(currentPlayer);
				}
			};

			new Thread(r).start();
		}

		if(phase == GamePhase.ENDGAME)
		{
			return;
		}

		if(rolledDoubles && currentPlayer.doublesRolled < 3 && !currentPlayer.isBankrupt()) {
			++currentPlayer.doublesRolled;
		} else if (rolledDoubles && currentPlayer.doublesRolled == 3 && !currentPlayer.isBankrupt()) {
			//go to jail you speedster
			currentPlayer.jail();
		}

		if (!rolledDoubles)
		{
			currentPlayer.doublesRolled = 0;
			currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

			//If player is bankrupt, increment to the next player
			while(players.get(currentPlayerIndex).isBankrupt())
			{
				currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
			}
		}

		// Automatically start the next turn
		startTurn();
	}

	public boolean isRolledDoubles() {
		return rolledDoubles;
	}

	public void setRolledDoubles(boolean rolledDoubles) {
		this.rolledDoubles = rolledDoubles;
	}

	public void bankrupt(Player patheticLoser) {
		patheticLoser.setBankrupt(true);
		numberBankrupt++;

		if(numberBankrupt == players.size() - 1){ //All but 1 are bankrupts
			endGame();
			return;
		}

		for (int tileIndex : patheticLoser.getDeeds()) {
			Tile tile = board.getTiles().get(tileIndex);
			auctionProperty(bank, tileIndex, tile.propertyCost / 2);
		}
	}

	public String endGame() {
		gameTimer.cancel();

		phase = GamePhase.ENDGAME;

		int [] netWorths = new int[players.size()];

		for(int i = 0; i < players.size(); i++){

			netWorths[i] = calculateNetWorth(players.get(i));

		}

		int maxIndex = 0;

		for(int i = 0; i < netWorths.length; i++){

			if(netWorths[i] > netWorths[maxIndex]){
				maxIndex = i;
			}

		}

		isWinnerCounted = false;

		winner = players.get(maxIndex).getName();

		//resetGame();

		return winner;

	}

	public void resetGame(){

		board = new Board();
		bank = new Bank();
		players = new ArrayList<Player>();

		this.phase = GamePhase.WAITING;
		this.inAuction = false;
		this.numberBankrupt = 0;
		this.numberOfHouses = 0;

	}

	//Helper method to help determine the WINNER
	public int calculateNetWorth(Player p){

		int totalNetWorth = 0;

		totalNetWorth += p.getMoney();

		for(int i : p.getDeeds()){

			totalNetWorth += board.getTiles().get(i).propertyCost;

			for(int j = 0; j < board.getTiles().get(i).numHouses; j++)
				totalNetWorth += board.getTiles().get(i).houseCost;

			if(board.getTiles().get(i).getHasHotel())
				totalNetWorth += board.getTiles().get(i).hotelCost;

		}

		return totalNetWorth;

	}

	public void acknowledgeCard() {

		if (currentCard == null) {
			return; // TODO: Log
		}

		if (currentCard.type == cardType.CHANCE) {
			landOnChance(currentCard);			
		}

		if (currentCard.type == cardType.COMMUNITY) {
			landOnCommunity(currentCard);
		}

		// Reset card
		currentCard = null;
		cardString = null;		

	}

}