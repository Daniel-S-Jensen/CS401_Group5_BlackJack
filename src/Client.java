import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

	private static User activeUser;
	private static Player activePlayer;
	private static Dealer activeDealer;
	private final static String ClientError = "Error. Logging out. Please login again and retry.";

	//client driver
	public static void main(String[] args) {

		try (Socket socket = new Socket("localhost", 7777)) {

			//startup screen
			Scanner sc = new Scanner(System.in);

			//controls menu so startup shows after logout
			Boolean programRunning = true;
			while (programRunning) {

				//startup menu
				Boolean loggedIn = false;
				Boolean signedUp = false;
				while (loggedIn == true || signedUp == true) {
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
							loggedIn = false;
							while (!loggedIn) {
								//user attempts to login
								loggedIn = login(socket);
							}
							break;
						case '2':	//signup
							menuChoiceSelected = true;
							signedUp = false;
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
				}

				//while user is logged in
				Boolean loggedOut = false;
				while (!loggedOut) {
					Boolean menuChoiceSelected = false;
					while (!menuChoiceSelected) {
						if (activeUser.getUserType() == UserType.dealer) {
							System.out.println("What would you like to do?");
							System.out.println("1. Join table.");
							System.out.println("0. Logout");
							char menuChoice = sc.nextLine().charAt(0);
							switch (menuChoice) {
							case '1':	//Dealer joins table
								menuChoiceSelected = true;
								Boolean inGame = true;
								while (inGame) {
									//user attempts to login
									inGame = playGame(socket);
								}
								break;
							case '0':	//Dealer logs out
								menuChoiceSelected = true;
								loggedOut = logOut(socket);
								break;
							default:	
							}
						}
						else if(activeUser.getUserType() == UserType.player) {
							System.out.println("What would you like to do?");
							System.out.println("1. Start game.");
							System.out.println("2. Manage balance.");
							System.out.println("0. Logout");
							char menuChoice = sc.nextLine().charAt(0);
							switch (menuChoice) {
							case '1':	//join game
								menuChoiceSelected = true;
								Boolean inGame = true;
								while (inGame) {
									//user attempts to login
									inGame = playGame(socket);
								}
								break;
							case '2': //manage balance - handles both deposit and withdraw
								menuChoiceSelected = true;
								Boolean balanceManaged = false;
								while (!balanceManaged) {
									balanceManaged = manageBalance(socket);
								}

							case '0':	//User logs out
								menuChoiceSelected = true;
								loggedOut = logOut(socket);
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
			signUpMessage.setName(name);

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
								if (receivedMessage.getUser().getUserType() == UserType.dealer) {
									activeDealer = (Dealer) receivedMessage.getUser();
									activeUser = receivedMessage.getUser();
								}
								else {
									activePlayer = (Player) receivedMessage.getUser();
									activeUser = receivedMessage.getUser();
								}
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
				System.out.println("Signup failure: Please try again with a different username.");
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
			while (amount != 0) {
				//TODO: payment screen would normally go here to complete money transaction

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
		}
		return balanceManaged;
	}


	private static Boolean logOut(Socket socket) {

		Boolean loggedOut = false;


		//create update message
		Message logoutMessage = new Message(MessageType.logout);
		logoutMessage.setUser(activeUser);


		//send login message
		sendMessage(socket, logoutMessage);

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
					if (receivedMessage.getType() == MessageType.logout) {
						if(receivedMessage.getStatus() == MessageStatus.success) {
							loggedOut = true;
						}
						messageReceived = true;
					}
				}	
			}
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (!loggedOut) {
			System.out.println("Logout not completed: Please try again.");
		}
		return loggedOut;
	}

	private static Boolean playGame(Socket socket) {
		Boolean inGame = true;
		while(inGame) {
			if(activeUser.getUserType() == UserType.dealer) {
				inGame = dealerGame(socket);
			}
			else {
				inGame = playerGame(socket);
			}
		}
		return inGame;
	}

	private static Boolean dealerGame(Socket socket) {
		Boolean inGame = true;
		while(inGame) {
			//create message
			Message joinGameMessage = new Message(MessageType.joinTable);
			joinGameMessage.setUser(activeUser);
			
			Boolean messageReceived = false;
			try {
				while (!messageReceived) {
					//receive server response
					InputStream inputStream = socket.getInputStream();
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					List<Message> listOfReceivedMessages = (List<Message>) objectInputStream.readObject();
					if (listOfReceivedMessages.size() > 0) {
						Message receivedMessage = listOfReceivedMessages.get(0);
						if (receivedMessage.getType() == MessageType.joinTable) {
							if(receivedMessage.getStatus() == MessageStatus.success) {
								activeUser = receivedMessage.getUser();
								Boolean dealAgain = true;
								while (dealAgain) {
									dealAgain = dealGame(socket, receivedMessage.getTable());
								}
							}
							else if(receivedMessage.getStatus() == MessageStatus.failure) {
								System.out.println("Error joining game. Please try again.");
							}
							inGame = false;
							messageReceived = true;
						}
					}	
				}
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return inGame;
	}

	private static Boolean playerGame(Socket socket) {
		Scanner sc = new Scanner(System.in);
		Boolean inGame = true;
		while(inGame) {
			Boolean menuChoiceSelected = false;
			while (!menuChoiceSelected) {
				System.out.println("What would you like to do?");
				System.out.println("1. Start a new game");
				System.out.println("2. Join a multiplayer queue");
				System.out.println("0. Return to menu");
				char menuChoice = sc.nextLine().charAt(0);
				switch (menuChoice) {
				case '1':	//new game
					menuChoiceSelected = true;
					singlePlayerGame(socket);
					break;
				case '2':	//join queue
					menuChoiceSelected = true;
					multiPlayerGame(socket);
					break;
				case '0':
					menuChoiceSelected = true;
					inGame = false;
					System.exit(0);	//graceful exit
					break;
				default:	
				}
			}
		}
		return inGame;
	}

	private static void singlePlayerGame(Socket socket) {
		//create message
		Message joinGameMessage = new Message(MessageType.joinTable);
		joinGameMessage.setUser(activeUser);
		joinGameMessage.setValue(1);	//1 value lets server know game request is singleplayer

		//send join game message
		sendMessage(socket, joinGameMessage);
		
		Boolean messageReceived = false;
		try {
			while (!messageReceived) {
				//receive server response
				InputStream inputStream = socket.getInputStream();
				ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
				List<Message> listOfReceivedMessages = (List<Message>) objectInputStream.readObject();
				if (listOfReceivedMessages.size() > 0) {
					Message receivedMessage = listOfReceivedMessages.get(0);
					if (receivedMessage.getType() == MessageType.joinTable) {
						if(receivedMessage.getStatus() == MessageStatus.success) {
							activeUser = receivedMessage.getUser();
							Boolean playAgain = true;
							while (playAgain) {
								playAgain = playGame(socket, receivedMessage.getTable());
							}
						}
						else if(receivedMessage.getStatus() == MessageStatus.failure) {
							System.out.println("Error joining game. Please try again.");
						}
						messageReceived = true;
					}
				}	
			}
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void multiPlayerGame(Socket socket) {
		//create message
		Message joinGameMessage = new Message(MessageType.joinTable);
		joinGameMessage.setUser(activeUser);
		joinGameMessage.setValue(2);	//2 value lets server know game request is multiplayer

		//send join game message
		sendMessage(socket, joinGameMessage);
		
		Boolean gameJoined = false;
		while (!gameJoined) {
			Boolean messageReceived = false;
			try {
				while (!messageReceived) {
					//receive server response
					InputStream inputStream = socket.getInputStream();
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					List<Message> listOfReceivedMessages = (List<Message>) objectInputStream.readObject();
					if (listOfReceivedMessages.size() > 0) {
						Message receivedMessage = listOfReceivedMessages.get(0);
						if (receivedMessage.getType() == MessageType.joinTable) {
							if(receivedMessage.getStatus() == MessageStatus.success) {
								activeUser = receivedMessage.getUser();
								Boolean playAgain = true;
								while (playAgain) {
									playAgain = playGame(socket, receivedMessage.getTable());
									gameJoined = true;
								}
							}
							else if(receivedMessage.getStatus() == MessageStatus.failure) {
								if(receivedMessage.getValue() != 2) {
									int time = receivedMessage.getValue();
									System.out.println("You are in a queue to join a table. The game will start in " + time + " seconds or when table reaches max players.");
								}
								else {
									System.out.println("Error joining game. Please try again.");
									gameJoined = true;	//sends back to menu to retry
								}
							}
							messageReceived = true;
						}
					}	
				}
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static Boolean playGame(Socket socket, Table table) {
		Boolean playAgain = false;
		Scanner sc = new Scanner(System.in);
		Boolean gameOver = false;
		while (!gameOver) {
			try {
				Boolean messageReceived = false;
				while (!messageReceived) {
					//receive server response
					InputStream inputStream = socket.getInputStream();
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					List<Message> listOfReceivedMessages = (List<Message>) objectInputStream.readObject();
					if (listOfReceivedMessages.size() > 0) {
						Message receivedMessage = listOfReceivedMessages.get(0);
						//users turn
						if(receivedMessage.getUser() == activeUser) {
							Message message = receivedMessage;
							if(receivedMessage.getTurn() == true) {
								//server requesting bet amount
								if (receivedMessage.getType() == MessageType.requestBet) {
									Boolean menuChoiceSelected = false;
									while (!menuChoiceSelected) {
										System.out.println("How much would you like bet ($1 increments): $");
										int bet = sc.nextInt();
										if (bet >= 1) {
											menuChoiceSelected = true;
											activePlayer.setBet(bet);
										}
									}
									message.setValue(activePlayer.getBet());
								}
								else if (receivedMessage.getType() == MessageType.requestGameAction) {
									Boolean menuChoiceSelected = false;
									while (!menuChoiceSelected) {
										System.out.println("Would you like to 1. Hit or 2. Stand? <1/2>: ");
										char menuChoice = sc.nextLine().charAt(0);
										if (menuChoice == '1') {
											activePlayer.hit();
											menuChoiceSelected = true;
										}
										else if (menuChoice == '2') {
											activePlayer.stand();
											menuChoiceSelected = true;
										}
									}
									message.setUser(activeUser);
								}
							}
							sendMessage(socket, message);
						}
						else if (receivedMessage.getType() == MessageType.update) {
							//TODO: update game to show current cards or end game
							//update game display
							
							//dispay winning/losing
						}
					}	
					messageReceived = true;
				}
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		Boolean menuChoiceSelected = false;
		while (!menuChoiceSelected) {
			System.out.println("Would you like to play again:");
			System.out.println("1. Yes");
			System.out.println("2. No");
			char menuChoice = sc.nextLine().charAt(0);
			if (menuChoice == '1') {
				playAgain = true;
				menuChoiceSelected = true;
			}
			else if (menuChoice == '2') {
				playAgain = false;
				menuChoiceSelected = true;
			}
		}
		return playAgain;
	}
	
	//TODO:all dealer interaction
	private static Boolean dealGame(Socket socket, Table table) {
		Boolean playAgain = false;
		Scanner sc = new Scanner(System.in);
		Boolean gameOver = false;
		while (!gameOver) {
			try {
				Boolean messageReceived = false;
				while (!messageReceived) {
					//receive server response
					InputStream inputStream = socket.getInputStream();
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					List<Message> listOfReceivedMessages = (List<Message>) objectInputStream.readObject();
					if (listOfReceivedMessages.size() > 0) {
						Message receivedMessage = listOfReceivedMessages.get(0);
						//users turn
						if(receivedMessage.getUser() == activeUser) {
							Message message = receivedMessage;
							if(receivedMessage.getTurn() == true) {
								if (receivedMessage.getType() == MessageType.requestGameAction) {
								}
							}
							sendMessage(socket, message);
						}
						else if (receivedMessage.getType() == MessageType.update) {
							//TODO: update game to show current cards or end game
							//update game display
							
							//dispay winning/losing
						}
					}	
					messageReceived = true;
				}
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		Boolean menuChoiceSelected = false;
		while (!menuChoiceSelected) {
			System.out.println("Would you like to play again:");
			System.out.println("1. Yes");
			System.out.println("2. No");
			char menuChoice = sc.nextLine().charAt(0);
			if (menuChoice == '1') {
				playAgain = true;
				menuChoiceSelected = true;
			}
			else if (menuChoice == '2') {
				playAgain = false;
				menuChoiceSelected = true;
			}
		}
		return playAgain;
	}
	
	
	
	
	
	
	
}
