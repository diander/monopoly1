package com.game.monopoly.logic;

/*************************************************************************************
 * 											BANK									 *
 *************************************************************************************/

public class Bank extends Owner {
	
	private double freeParkingPool = 100;
	
	private final int numberOfHouses = 32;
	
	public Bank() {
		super("Bank", 2000000);
	}
	
	public int getNumberOfHouses(){
		return numberOfHouses;
	}
	
	public double getFreeParkingPool() {
		return freeParkingPool;
	}
	
	public void payTax(Player player, Tile tile)
	{
		if (tile.getName().equals("LUXURY TAX")) {
			freeParkingPool += player.transfer(this, 75.0);
		}
		else if (player.getMoney() > 2000){
			freeParkingPool += player.transfer(this, 200.0);
		}
		else{
			freeParkingPool += player.transfer(this, (double)Math.round(0.1 * player.getMoney()));
		}
	}
	
	public void awardFreeParking(Player player){
		this.transfer(player, freeParkingPool);
		freeParkingPool = 100;
	}
}