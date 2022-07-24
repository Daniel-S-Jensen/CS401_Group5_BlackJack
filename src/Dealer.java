
public class Dealer extends User {
	
	//attributes
	
	
	//constructor
	public Dealer() {
		this.userType = UserType.dealer;
	}
	
	public Dealer(UserID userID) {
		this.userID = userID;
	}
	
	public Dealer(UserID userID, String name) {
		this.userID = userID;
		this.name = name;
	}
	
	//
	/*public  getFaceDownCard() {
		
	}
	
	//
	public  askPlayerStandOrHit(User user) {
		
	}
	
	//
	public  payPlayerPayout(User user) {
		
	}*/
	
	public String toString() {
		return (this.userID.toString() + ", " + this.name + ", "  + 0);
	}
	
}
