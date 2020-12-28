package com.game.monopoly.logic;

/*************************************************************************************
 * 										CHANCE CARD									 *
 *************************************************************************************/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

enum cardType {CHANCE, COMMUNITY};

public class Card {

	private ArrayList<String> cards;
	private Random rand;
	private int cardIndex;
	private String freeJailDesc;
	
	public cardType type;

	public Card(cardType type, boolean ignoreTwo){
		
		cards = new ArrayList<String>();
		rand = new Random(System.currentTimeMillis());
		do{
			cardIndex = rand.nextInt(15);
		}while(ignoreTwo && cardIndex==2);			
		freeJailDesc = "";
		this.type = type;

		readDescription(type);

	}
	
	//Removes get-out-of-jail-free card from deck
	public void removeFreedomCard(){
		
		freeJailDesc = cards.get(2);
		cards.remove(2);
		
	}
	
	//Puts get-out-of-jail-free card back into the deck
	public void addFreedomCard(){
		
		cards.add(2, freeJailDesc);
		
	}
	
	public int getCardIndex(){
		return cardIndex;
	}

	private void readDescription(cardType type){
		
		String fileName = "";
		
		if(type.equals(cardType.COMMUNITY)) fileName="./src/main/resources/txt/cc_card_desc.txt";
		else fileName = "./src/main/resources/txt/chance_card_desc.txt";

		try(BufferedReader br = new BufferedReader(new FileReader(fileName)))
		{

			String currentLine;

			while((currentLine = br.readLine()) != null){

				cards.add(currentLine);

			}			


		}catch(IOException e){

			e.printStackTrace();

		}
	}
	
	public String getCardDesc(){
		
		return cards.get(cardIndex).substring(1, cards.get(cardIndex).lastIndexOf("\""));
		
	}
	
	public int getPayment(){
		
		String temp = cards.get(cardIndex);
		String [] sarry = temp.split(":");
		return Integer.parseInt(sarry[1]);
		
	}
	
	public int getDefPent(){
		
		String temp = cards.get(cardIndex);
		String [] sarry = temp.split(":");
		return Integer.parseInt(sarry[2]);
		
	}
	
	public int getSecdPent(){
		
		if(cardIndex != 5){
			
			return -1;
			
		}
		
		String temp = cards.get(cardIndex);
		String [] sarry = temp.split(":");
		if(sarry.length == 5){
			
			return Integer.parseInt(sarry[3]);
			
		}else{
			
			return -1;
			
		}
		
	}
	
	public int moveToIndex(){
		
		String temp = cards.get(cardIndex);
		String [] sarry = temp.split(":");
		if(cardIndex == 5){
			
			return Integer.parseInt(sarry[4]);
			
		}else{
			
			return Integer.parseInt(sarry[3]);
			
		}
		
	}

}
