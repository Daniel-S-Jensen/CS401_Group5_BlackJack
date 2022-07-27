import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class test8 {
	private static Deck deck1;
	private static Hand hand1;

	@Test
	void aceTest() {

		deck1 = new Deck();
		deck1.generateDeck();
		//deck1.shuffleDeck();
		
		hand1 = new Hand();
		hand1.addCard(deck1.getCardArray()[0]);//1
		hand1.addCard(deck1.getCardArray()[9]);//10
		System.out.println(hand1.getHandTotal());
	
		assertEquals(21, hand1.getHandTotal());
		assertEquals(false, hand1.getHasBust());
	}
}
