import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class test2 {
	public static  Login login1;
	private static UserIDType userIDType;
	private static User user1;

	@Test
	void userTest() {
		login1 = new Login("Tom", "123456", userIDType.P );
		System.out.println(login1.toString());

		user1 = new User("Tom Cruise",login1.getUserID());
		System.out.println(user1. getUserID());
		
		assertEquals("Tom Cruise", user1.getName());
		assertEquals(login1.getUserID(), user1.getUserID());
		
	}

}
