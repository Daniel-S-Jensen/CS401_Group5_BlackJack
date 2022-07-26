import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Table {
	
	//attributes
	static final private int Max_Players = 5;
	static private int count = 0;
	private int id;
	private Boolean dealerAssigned;
	private Dealer dealer;
	private int playerCount;
	public Player[] players;
	private Boolean full;
	public long joinTime;
	
	public ArrayList<Socket> clientList;
	
	//constructor
	public Table() {
		this.id = ++count;
		this.dealerAssigned = false;
		this.players = new Player[Max_Players];
		this.playerCount = 0;
		this.clientList = new ArrayList<Socket>();
		this.full = false;
	}
	
	//adds a player to this table
	public void addPlayer(User user, Socket socket) {
		this.players[this.playerCount] = (Player) user;
		this.playerCount++;
		this.clientList.add(socket);
		if(this.playerCount == Max_Players) {
			this.full = true;
		}
	}
	
	//adds a player to this table
	public void addDealer(User user, Socket socket) {
		this.dealer = (Dealer) user;
		dealerAssigned = true;
		this.clientList.add(socket);
	}
	
	private void updateClients(Socket socket, Message message) {
		try {
			for (int i=0; i < this.clientList.size(); i++) {
				if (this.clientList.get(i) == socket) {
					continue;
				}
				else {
					List<Message> messages = new ArrayList<>();
					OutputStream outputStream = this.clientList.get(i).getOutputStream();
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
					messages.add(message);
					objectOutputStream.writeObject(messages);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String tableString = "";
		
		return tableString;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public Boolean getFull() {
		return full;
	}

	public void setFull(Boolean full) {
		this.full = full;
	}
	
	public Dealer getDealer() {
		return this.dealer;
	}
	
	
}
