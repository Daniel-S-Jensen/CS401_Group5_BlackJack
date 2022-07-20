public class Card {
	
	//attributes
	private int cardValue;					//face value of the card
	private CardSuit cardSuit;				//the suit of the card
	private Boolean isFaceDown = false;		//whether the card is facing up(false) or down(true)
	
	//constructor
	public Card(int cardValue, CardSuit cardSuit) {
		this.cardValue = cardValue;
		this.cardSuit = cardSuit;
	}
	
	//returns the face value of the card
	public int getValue() {
		return this.cardValue;
	}
	
	//returns the suit of the card
	public CardSuit getSuit() {
		return this.cardSuit;
	}
	
	//returns true if card is face down, false if not
	public Boolean getIsFacedown() {
		return this.isFaceDown;
	}
	
	//setter method for isFaceDown
	public void setIsFacedown(Boolean isFacedown) {
		this.isFaceDown = isFacedown;
	}
	
}