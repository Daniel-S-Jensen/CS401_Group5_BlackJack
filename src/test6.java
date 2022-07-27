import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
class test6 {
	
	private static Table table1;

	@Test
	void tableTest() {
		
		table1 = new Table();

		table1.addPlayer(null, null);
		table1.addPlayer(null, null);
		table1.addPlayer(null, null);
		table1.addPlayer(null, null);
		table1.addPlayer(null, null);
		System.out.println(table1.toString());
		
		assertEquals(true, table1.getFull());
		assertEquals(5, table1. getPlayerCount());
		
	}
}
