import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class test9 {
	private static Deck deck1;
	private static Hand hand1;

	@Test
	void burstTest() {
		
		deck1 = new Deck();
		deck1.generateDeck();

		hand1 = new Hand();
		hand1.addCard(deck1.getCardArray()[9]);//10
		hand1.addCard(deck1.getCardArray()[10]);//10
		hand1.addCard(deck1.getCardArray()[11]);//10
		System.out.println(hand1.getHandTotal());
	
		assertEquals(30, hand1.getHandTotal());
		assertEquals(true, hand1.getHasBust());
		
	}
}