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
	
	//adds a player to this table
	public void addDealer(Dealer dealer, Socket socket) {
		this.dealer = dealer;
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
	
}
