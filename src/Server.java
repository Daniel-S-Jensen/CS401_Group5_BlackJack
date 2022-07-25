import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	private static ArrayList<Dealer> dealerList;
	private static ArrayList<Player> playerList;
	private static ArrayList<Login> loginList;
	
	private static ArrayList<Table> tableList;

	Player test = new Player();

	public static void main(String[] args) throws IOException {

		ServerSocket server = null;

		try {
			//port 7777 is localhost
			server = new ServerSocket(7777);
			System.out.println("Server is listening on port 7777"); //
			server.setReuseAddress(true);

			while(true) {
				//socket to receive requests on
				Socket client = server.accept();

				System.out.println("Connection from " + client); //

				//new thread
				ClientHandler clientSock = new ClientHandler(client);
				new Thread(clientSock).start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (server != null) {
				try {
					server.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//ClientHandler class
	private static class ClientHandler implements Runnable {
		private final Socket clientSocket;

		public ClientHandler(Socket socket) {
			this.clientSocket = socket;
		}

		public void run() throws IndexOutOfBoundsException {

			loadLoginList();
			loadUserList();

			try {
				Boolean closeThread = false;
				while (!closeThread) {

					Boolean loginMatches = false;
					while (!loginMatches) {

						Boolean messageReceived = false;
						while (!messageReceived) {
							//client input stream from connected socket
							InputStream inputStream = clientSocket.getInputStream();

							//object input stream
							ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

							List<Message> listOfMessages = (List<Message>) objectInputStream.readObject();

							if (listOfMessages.size() > 0) {
								Message receivedMessage = listOfMessages.get(0);

								if (receivedMessage.getType() == MessageType.login) {
									messageReceived = true;
									System.out.println("Login attempt received from " + clientSocket);
									Message message;

									//finds login in list of valid logins
									User loadedUser = null;
									Login foundLogin = null;
									Boolean userFound = false;
									for (int i = 0; i < loginList.size(); i++) {
										if (receivedMessage.getUsername().equals(loginList.get(i).getUsername()) && receivedMessage.getPassword().equals(loginList.get(i).getPassword())) {
											loginMatches = true;
											userFound = true;
											foundLogin = loginList.get(i);
											break;
										}
									}
									//send message back
									message = receivedMessage;
									if (userFound) {
										if (foundLogin.getUserID().getUserType() == UserType.dealer) {
											for (int i = 0; i < dealerList.size(); i++) {
												if(dealerList.get(i).getUserID() == foundLogin.getUserID()) {
													loadedUser = dealerList.get(i);
													break;
												}
											}
										}
										else {
											for (int i = 0; i < playerList.size(); i++) {
												if(playerList.get(i).getUserID() == foundLogin.getUserID()) {
													loadedUser = playerList.get(i);
													break;
												}
											}
										}

										message.setStatus(MessageStatus.success);
										message.setUser(loadedUser);
										System.out.println("Login attempt :" + "success");
									}
									else {
										message.setStatus(MessageStatus.failure);
										System.out.println("Login attempt :" + "failure");
									}

									sendMessage(clientSocket, message);
								}
								else if (receivedMessage.getType() == MessageType.signup) {
									Boolean validUsername = true;
									Message message = receivedMessage;
									for (int i = 0; i < loginList.size(); i++) {
										if(loginList.get(i).getUsername() == receivedMessage.getUsername()) {
											validUsername = false;
											message.setStatus(MessageStatus.failure);
											break;
										}
									}
									if (validUsername) {
										loginMatches = true;
										Login login = new Login(receivedMessage.getUsername(), receivedMessage.getPassword(), UserIDType.P);
										loginList.add(login);
										saveLoginList();
										Player player = new Player(receivedMessage.getName(), login.getUserID());
										playerList.add(player);
										saveUserList();
										message.setUser(player);
										message.setStatus(MessageStatus.success);

									}
									sendMessage(clientSocket, message);
								}
							}
						}
					}

					Boolean logoutReceived = false;
					while (!logoutReceived) {

						//client input stream from connected socket
						InputStream inputStream = clientSocket.getInputStream();

						//object input stream
						ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

						List<Message> listOfMessages = (List<Message>) objectInputStream.readObject();
						Message receivedMessage = listOfMessages.get(0);
						if (receivedMessage.getType() == MessageType.logout) {
							logoutReceived = true;
							System.out.println("Logout received from " + clientSocket);
							Message message = receivedMessage;
							message.setStatus(MessageStatus.success);
							//send message back
							sendMessage(clientSocket, message);
							closeThread = true;
						}
						else if (receivedMessage.getType() == MessageType.transaction) {
							System.out.println("Balance update received from " + clientSocket);
							Message message = receivedMessage;
							for (int i = 0; i < playerList.size(); i++) {
								if(playerList.get(i).getUserID() == receivedMessage.getUser().getUserID()) {
									playerList.get(i).receivePayout(receivedMessage.value);
									saveUserList();
									break;
								}
							}
							message.setStatus(MessageStatus.success);
							//send message back
							sendMessage(clientSocket, message);				
						}
						else {
							//TODO: gameplay
							Message message = receivedMessage;
							if (receivedMessage.getUser().getUserType() == UserType.player) {
								if(receivedMessage.getType() == MessageType.joinTable) {
									//single player
									if(receivedMessage.getValue() == 1) {
										for(int i = 0; i < tableList.size(); i++) {
											if(tableList.get(i).getPlayerCount() == 0) {
												tableList.get(i).addPlayer(receivedMessage.getUser(), clientSocket);
												tableList.get(i).setFull(true);
												message.setStatus(MessageStatus.success);
												message.setTable(tableList.get(i));
												break;
											}
										}
									}
									//multi player
									else {
										for(int i = 0; i < tableList.size(); i++) {
											if(tableList.get(i).getFull() != true) {
												tableList.get(i).addPlayer(receivedMessage.getUser(), clientSocket);
												message.setStatus(MessageStatus.failure);
												message.setTable(tableList.get(i));
												break;
											}
										}
									}
								}
							}
							else if (receivedMessage.getUser().getUserType() == UserType.dealer) {
								if(receivedMessage.getType() == MessageType.joinTable) {
									Table table = new Table();
									table.addDealer(receivedMessage.getUser(), clientSocket);
									message.setTable(table);
									message.setStatus(MessageStatus.success);
								}
								else if(receivedMessage.getType() == MessageType.startGame) {
									receivedMessage.getTable().setFull(true);
								}
								
							}
							
							
							
							message.setStatus(MessageStatus.success);
							//send message back
							sendMessage(clientSocket, message);
							closeThread = true;	
						}
					}					
				}	
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally {
				try {
					System.out.println("Closing socket: " + clientSocket);
					clientSocket.close();
					System.out.println("Socket closed.");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		private static void sendMessage(Socket socket, Message message) {
			try {
				List<Message> messages = new ArrayList<>();
				OutputStream outputStream = socket.getOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
				messages.add(message);
				objectOutputStream.writeObject(messages);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}



		private void loadLoginList() {
			String filename = "loginList.txt";
			try {
				File inFile = new File(filename);
				Scanner input = new Scanner(inFile);
				while (input.hasNextLine()) {
					String inLine = input.nextLine();
					String[] tokens = inLine.split(",");
					if(tokens.length == 3) {
						UserID tempUserID = new UserID(tokens[2].charAt(0), Integer.valueOf(tokens[2].substring(1)));
						Login tempLogin = new Login(tokens[0], tokens[1], tempUserID);
						loginList.add(tempLogin);
					}
					else {
						break;
					}
				}
				UserID temp = new UserID();
				temp.loadCount(loginList.size());
				input.close();

			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		private static void loadUserList() {
			String filename = "userList.txt";
			try {
				File inFile = new File(filename);
				Scanner input = new Scanner(inFile);
				while (input.hasNextLine()) {
					String inLine = input.nextLine();
					String[] tokens = inLine.split(",");
					if(tokens.length == 3) {
						UserID tempUserID = new UserID(tokens[0].charAt(0), Integer.valueOf(tokens[0].substring(1)));
						if (tempUserID.getUserType() == UserType.dealer) {
							Dealer tempDealer = new Dealer(tempUserID, tokens[1]);
							dealerList.add(tempDealer);
						}
						else {
							Player tempPlayer = new Player(tempUserID, tokens[1], Integer.valueOf(tokens[2]));
							playerList.add(tempPlayer);
						}

					}
					else {
						break;
					}
				}
				UserID temp = new UserID();
				temp.loadCount(loginList.size());
				input.close();

			} catch (FileNotFoundException e) {
				//e.printStackTrace();
			}
		}

		private static void saveLoginList() {
			try {
				String filename = "loginList.txt";
				FileWriter outFile = new FileWriter(filename);
				for (int i = 0; i < loginList.size(); i++) {
					outFile.write(loginList.get(i).toString());
				}
				outFile.close();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		private static void saveUserList() {
			try {
				String filename = "userList.txt";
				FileWriter outFile = new FileWriter(filename);
				for (int i = 0; i < dealerList.size(); i++) {
					outFile.write(dealerList.get(i).toString());
				}
				for (int i = 0; i < playerList.size(); i++) {
					outFile.write(playerList.get(i).toString());
				}
				outFile.close();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}


}