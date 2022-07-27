import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class test4 {
	private static Deck deck1;

	@Test
	void deckTest() {

		deck1 = new Deck();
		deck1.generateDeck();
		deck1.shuffleDeck();
		
		for ( int i = 0; i < 51; i ++)
			System.out.println(deck1.getCardArray()[i].toString());
		
		System.out.println(deck1.getNumberOfCardsLeft());	
		
		assertEquals(52, deck1.getNumberOfCardsLeft());
		
	}
}
