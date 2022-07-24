
public class UserID {
	//attributes
	static private int count = 0;
	private UserIDType userIDType;
	private int userIDNum;
	
	//constructor
	public UserID() {
		
	}
	
	public UserID(UserIDType userIDType) {
		this.userIDType = userIDType;
		this.userIDNum = ++count;
	}
	
}

