
public class Player extends User {
	
	//attributes
	private int accountBalance;		//holds the amount of money in the player's account
	private int betAmount;			//the amount the player bet for the current hand
	
	//constructor
	public Player() {
		
	}
	
	//returns how much the player bet this round
	public int getBet() {
		return this.betAmount;
	}
	
	//sets how much the player bet this round
	public void setBet(int bet) {
		this.betAmount = bet;
	}
	
	//
	public  playerPayoutForThisGame() {
		
	}
	
	
}
