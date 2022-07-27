import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class test1 {
	public static  Login login1;
	private static UserIDType userIDType;


	@Test
	void loginTest() {
		login1 = new Login("Tom", "123456", userIDType.P );
		System.out.println(login1.toString());

		
		assertEquals("Tom", login1.getUsername());
		assertEquals("123456", login1.getPassword());
		
		
	}

}
