
public class User {
	
	//attributes
	private String name;
	private Hand hand;
	//private Boolean isStillInGame = true;
	//private Boolean isStanding = false;
	
	
	//default constructor
	public User() {
		this.hand = new Hand();
		this.isStillInGame = true;
		this.isStanding = false;
	}
	
	//constructor sets name
	public User(String name) {
		this.name = name;
		this.hand = new Hand();
		this.isStillInGame = true;
		this.isStanding = false;
	}
	
	//returns if player intends to play next game
	public Boolean isUserStillInGame() {
		return this.isStillInGame;
	}
	
	//
	public getCards() {
		
	}
	
	//checks if user has a blackjack or not
	public Boolean checkBlackJack() {
		if(hand.getHandTotal() == 21) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//check is the user has bust or not, total over 21;
	public Boolean checkBust() {
		if(hand.getHandTotal() > 21) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//
	public  hit() {
		
	}
	
	//
	public  stand() {
		
	}
	
	//returns user's name
	public String getName() {
		return this.name;
	}
}
