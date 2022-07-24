
public class Player extends User {
	
	//attributes
	private int accountBalance;		//holds the amount of money in the player's account
	private int betAmount;			//the amount the player bet for the current hand
	
	//constructor
	public Player() {
		this.userType = userType.player;
		this.accountBalance = 0;
	}
	
	//returns how much the player bet this round
	public int getBet() {
		return this.betAmount;
	}
	
	//sets how much the player bet this round
	public void setBet(int bet) {
		this.betAmount = bet;
		this.accountBalance -= bet;
	}
	
	//
	public void receivePayout(int payout) {
		this.accountBalance += payout;
	}
	
	//
	public int getAccountBalance() {
		return accountBalance;
	}

	//
	public void setAccountBalance(int accountBalance) {
		this.accountBalance = accountBalance;
	}
	
	
}
