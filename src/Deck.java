import java.util.*;

public class Deck {

	//attributes
	static final int maxNumOfCards = 52;	//max number of cards in the deck
	int numberOfCardsLeft;					//current number of cards in deck
	Card[] cards;							//deck, array holding the cards
	
	//constructor
	public Deck() {
		this.numberOfCardsLeft = maxNumOfCards;
		this.generateDeck();
	}
	
	//returns the top card from the deck and "removes" it
	public Card drawCard() {
		Card card = this.cards[this.numberOfCardsLeft - 1];
		this.numberOfCardsLeft--;
		return card;
	}
	
	//shuffles the deck
	public void shuffleDeck() {
		List<Card> cardList = Arrays.asList(this.cards);
		Collections.shuffle(cardList);
		cardList.toArray(this.cards);
	}
	
	//creates a new deck of cards
	public void generateDeck() {
		this.cards = new Card[maxNumOfCards];
		for (int i = 0; i < maxNumOfCards; i++) {
			int suit1 = maxNumOfCards/4;
			int suit2 = maxNumOfCards/2;
			int suit3 = 3*maxNumOfCards/4;
			int suit4 = maxNumOfCards;
			if (i < suit1) {
				Card card = new Card(i % suit1 ,CardSuit.Clubs);
				cards[i] = card;
			}
			else if (i < suit2) {
				Card card = new Card(i % suit1 ,CardSuit.Diamonds);
				cards[i] = card;
				continue;
			}
			else if (i < suit3) {
				Card card = new Card(i % suit1 ,CardSuit.Hearts);
				cards[i] = card;
				continue;
			}
			else {
				Card card = new Card(i % suit1 ,CardSuit.Spades);
				cards[i] = card;
				continue;
			}
		}
	}
	
	//returns number of cards in deck
	public int getNumberOfCardsLeft() {
		return this.numberOfCardsLeft;
	}
	
	
	
	
	
	
	
}
