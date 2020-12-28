package com.game.monopoly.logic;

/*************************************************************************************
 * 											BOARD									 *
 *************************************************************************************/

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Board {

	private TileList tiles;
	private Dice[] dice;

	public Board() {
		tiles = Json.<TileList>deserializeFromFile("./src/main/resources/json/tiles.json", TileList.class);

		// cache tile indices
		for (int i = 0; i < tiles.size(); ++i) {
			tiles.get(i).index = i;
		}

		dice = new Dice[] { new Dice(), new Dice() };
	}

	public TileList getTiles() {
		return tiles;
	}
	
	public Dice[] getDice() {
		return dice;
	}
	
	@JsonIgnore
	public Tile getGo() {
		return tiles.get(0);
	}
}
