import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class test5 {
	private static Deck deck1;
	private static Hand hand1;

	@Test
	void handTest() {

		deck1 = new Deck();
		deck1.generateDeck();
		deck1.shuffleDeck();
		
		hand1 = new Hand();
		hand1.addCard(deck1.drawCard());
		hand1.addCard(deck1.drawCard());
		
		System.out.println(hand1.getHandTotal());
		System.out.println(hand1.getHasBust());
		System.out.println(hand1.getNumCardsInHand());
		System.out.println(deck1.getNumberOfCardsLeft());
		
		assertEquals(2, hand1.getNumCardsInHand());
		assertEquals(50, deck1.getNumberOfCardsLeft());
		
	}
}
