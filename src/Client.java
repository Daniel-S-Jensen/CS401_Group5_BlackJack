import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

	private static User activeUser;
	private final static String ClientError = "Error. Logging out. Please login again and retry.";
	
	//client driver
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		try (Socket socket = new Socket("localhost", 7777)) {
			
			//startup screen
			Scanner sc = new Scanner(System.in);
			
			//controls menu so startup shows after logout
			Boolean programRunning = true;
			while (programRunning) {
			
				//startup menu
				Boolean menuChoiceSelected = false;
				while (!menuChoiceSelected) {
					System.out.println("What would you like to do?");
					System.out.println("1. Login");
					System.out.println("2. Sign Up");
					System.out.println("0. Exit");
					
					char menuChoice = sc.nextLine().charAt(0);
					switch (menuChoice) {
						case '1':	//login
							menuChoiceSelected = true;
							Boolean loggedIn = false;
							while (!loggedIn) {
								//user attempts to login
								loggedIn = login(socket);
							}
							break;
						case '2':	//signup
							menuChoiceSelected = true;
							Boolean signedUp = false;
							while (!signedUp) {
								//user attempts to login
								signedUp = signUp(socket);
							}
							break;
						case '0':
							menuChoiceSelected = true;
							programRunning = false;
							System.exit(0);	//graceful exit
							break;
						default:	
					}
				}
				
				//while user is logged in
				Boolean loggedOut = false;
				while (!loggedOut && programRunning) {
					if (activeUser.getUserType() == UserType.dealer) {
						System.out.println("What would you like to do?");
						System.out.println("1. Join table.");
						System.out.println("0. Logout");
						char menuChoice = sc.nextLine().charAt(0);
						switch (menuChoice) {
							case '1':	//login
								menuChoiceSelected = true;
								Boolean loggedIn = false;
								while (!loggedIn) {
									//user attempts to login
									loggedIn = login(socket);
								}
								break;
							case '0':
								menuChoiceSelected = true;
								System.exit(0);	//graceful exit
								break;
							default:	
						}
					}
					else if(activeUser.getUserType() == UserType.player) {
						System.out.println("What would you like to do?");
						System.out.println("1. Join queue for game.");
						System.out.println("2. Manage balance.");
						System.out.println("0. Logout");
						char menuChoice = sc.nextLine().charAt(0);
						switch (menuChoice) {
							case '1':	//join game
								menuChoiceSelected = true;
								Boolean inGame = true;
								while (inGame) {
									//user attempts to login
									inGame = login(socket);
								}
								break;
							case '2': //manage balance - handles both deposit and withdraw
								menuChoiceSelected = true;
								Boolean balanceManaged = false;
								while (!balanceManaged) {
									balanceManaged = manageBalance(socket);
								}
								
							case '0':	//logout
								menuChoiceSelected = true;
								
								//send logout message and receive confirmation
								
								loggedOut = true;
								break;
							default:	
						}
					}
					else {
						System.out.println(ClientError);
						loggedOut = true;
					}
				}	
			}
			
			
			
			
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	//sends a message to server
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

	private static Boolean login(Socket socket) {
		
		Boolean loggedIn = false;
		Scanner sc = new Scanner(System.in);
		
		while(!loggedIn) {
		
			//get login info
			System.out.print("Enter username <username>: ");
			String username = sc.nextLine();
			System.out.print("Enter password <password>: ");
			String password = sc.nextLine();
			
			//create login message
			Message loginMessage = new Message(MessageType.login);
			loginMessage.setUsername(username);
			loginMessage.setPassword(password);
			
			//send login message
			sendMessage(socket, loginMessage);
			
			Boolean messageReceived = false;
			
			//receive server response and validate login status
			try {
				while (!messageReceived) {
					//receive server response
					InputStream inputStream = socket.getInputStream();
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					List<Message> listOfReceivedMessages = (List<Message>) objectInputStream.readObject();
					if (listOfReceivedMessages.size() > 0) {
						Message receivedMessage = listOfReceivedMessages.get(0);
						if (receivedMessage.getType() == MessageType.login) {
							if(receivedMessage.getStatus() == MessageStatus.success) {
								loggedIn = true;
								activeUser = receivedMessage.getUser();
							}
							messageReceived = true;
						}
					}	
				}
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (!loggedIn) {
				System.out.println("Login failure: Please make sure username and passowrd are correct.");
			}
		}
		sc.close();
		return loggedIn;
	}
	
	private static Boolean signUp(Socket socket) {
		
		Boolean signedUp = false;
		Scanner sc = new Scanner(System.in);
		
		while(!signedUp) {
		
			//get signup info
			System.out.print("Enter desired username <username>: ");
			String username = sc.nextLine();
			System.out.print("Enter desired password <password>: ");
			String password = sc.nextLine();
			System.out.print("Enter your name: ");
			String name = sc.nextLine();
			
			//create login message
			Message signUpMessage = new Message(MessageType.signup);
			signUpMessage.setUsername(username);
			signUpMessage.setPassword(password);
			signUpMessage.setName(password);
			
			//send login message
			sendMessage(socket, signUpMessage);
			
			Boolean messageReceived = false;
			
			//receive server response and validate login status
			try {
				while (!messageReceived) {
					//receive server response
					InputStream inputStream = socket.getInputStream();
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					List<Message> listOfReceivedMessages = (List<Message>) objectInputStream.readObject();
					if (listOfReceivedMessages.size() > 0) {
						Message receivedMessage = listOfReceivedMessages.get(0);
						if (receivedMessage.getType() == MessageType.signup) {
							if(receivedMessage.getStatus() == MessageStatus.success) {
								signedUp = true;
								activeUser = receivedMessage.getUser();
							}
							messageReceived = true;
						}
					}	
				}
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (!signedUp) {
				System.out.println("Signup failure: Please try again.");
			}
		}
		sc.close();
		return signedUp;
	}
	
	private static Boolean manageBalance(Socket socket) {
		Boolean balanceManaged = false;
		Scanner sc = new Scanner(System.in);
		int amount = 0;
		
		while(!balanceManaged) {
			Boolean menuChoiceSelected = false;
			while (!menuChoiceSelected) {
				System.out.println("What would you like to do?");
				System.out.println("1. Add funds.");
				System.out.println("2. Withdraw funds");
				System.out.println("0. Return to menu;");
				char menuChoice = sc.nextLine().charAt(0);
				amount = 0;
				switch (menuChoice) {
					case '1':	//join game
						menuChoiceSelected = true;
						System.out.println("Withdraw funds");
						System.out.println("How much would you like to add: $");
						amount = sc.nextInt();
						break;
					case '2': //manage balance
						menuChoiceSelected = true;
						System.out.println("Withdraw funds");
						System.out.println("How much would you like to withdraw: $");
						amount = sc.nextInt();
						amount *= -1;
						break;
					case '0':
						menuChoiceSelected = true;
						balanceManaged = true;
						break;
					default:	
				}
			}
			
			//create update message
			Message balanceUpdateMessage = new Message(MessageType.transaction);
			balanceUpdateMessage.setUser(activeUser);
			balanceUpdateMessage.setValue(amount);
			
			//send login message
			sendMessage(socket, balanceUpdateMessage);
			
			Boolean messageReceived = false;
			Boolean balanceUpdated = false;
			
			//receive server response and validate login status
			try {
				while (!messageReceived) {
					//receive server response
					InputStream inputStream = socket.getInputStream();
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					List<Message> listOfReceivedMessages = (List<Message>) objectInputStream.readObject();
					if (listOfReceivedMessages.size() > 0) {
						Message receivedMessage = listOfReceivedMessages.get(0);
						if (receivedMessage.getType() == MessageType.transaction) {
							if(receivedMessage.getStatus() == MessageStatus.success) {
								balanceUpdated = true;
								activeUser = receivedMessage.getUser();
							}
							messageReceived = true;
						}
					}	
				}
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (!balanceUpdated) {
				System.out.println("Transaction not completed: Please try again.");
			}
		}
		return balanceManaged;
	}
	
	
}
