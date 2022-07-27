import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class test3 {
	public static  Login login1;
	private static UserIDType userIDType;
	private static User user1;
	public Player player1;

	@Test
	void playerTest() {
		login1 = new Login("Tom", "123456", userIDType.P );
		System.out.println(login1.toString());

		user1 = new User("Tom Cruise",login1.getUserID());
		System.out.println(user1. getUserID());
		
		player1 = new Player(user1.getUserID(), user1.getName(), 500);
		System.out.println(player1.toString());
		
		assertEquals(500, player1.getAccountBalance());
		
		
	}

}
