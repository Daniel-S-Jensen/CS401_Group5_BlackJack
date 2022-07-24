import java.net.Socket;
import java.util.ArrayList;

public class Table {
	
	//attributes
	static final private int Max_Players = 5;
	static private int count = 0;
	private int id;
	private Boolean dealerAssigned;
	private Dealer dealer;
	private int playerCount;
	private Player[] players;
	
	private ArrayList<Socket> clientList;
	
	
	
	//constructor
	public Table() {
		this.id = ++count;
		this.dealerAssigned = false;
		this.players = new Player[Max_Players];
		this.playerCount = 0;
		this.clientList = new ArrayList<Socket>();
	}
	
	//adds a player to this table
	public void addPlayer(Player player, Socket socket) {
		this.players[this.playerCount] = player;
		playerCount++;
		this.clientList.add(socket);
	}
	
}
