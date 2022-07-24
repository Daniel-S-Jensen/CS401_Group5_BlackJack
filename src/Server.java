import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
	/*
	//adds new client connection to list of clients
	clientList.add(client); //do this after joining a table
	*/
	
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
									
									User loadedUser;
									//list of logins
									//TODO: compare login info to list of all users
									//loop to go through list
									if (receivedMessage.getUsername().equals("username") && receivedMessage.getPassword().equals("password")) {
										loginMatches = true;
										//process message
										message = receivedMessage;
										message.setStatus(MessageStatus.success);
										System.out.println("Login attempt :" + "success");
										//send message back
										sendMessage(clientSocket, message);
									}
									else {
										//send message back
										message = new Message(receivedMessage.getType(), "fail", receivedMessage.getText());
										System.out.println("Login attempt: " + message.getStatus());
										sendMessage(clientSocket, message);
									}
									
									
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
						Message message = listOfMessages.get(0);
						logoutReceived = checkLogout(message);
						if (!logoutReceived) {
							//System.out.println("Received [" + listOfMessages.size() + "] message from: " + clientSocket);
							System.out.println("Received message from: " + clientSocket);					
							//System.out.println("All messages:");
							//listOfMessages.forEach(msg -> printMessage(msg));
							printMessage(message);

							//process message
							//listOfMessages.forEach(msg -> processMessage(msg));
							Message returnMessage = processMessage(message);
							sendMessage(clientSocket, returnMessage);
							updateClients(clientSocket, returnMessage);

						}
						if (logoutReceived) {
							System.out.println("Logout received from " + clientSocket);
							//process message
							Message returnMessage = processMessage(message);
							//send message back
							sendMessage(clientSocket, returnMessage);
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

		private static void printMessage(Message msg){
			System.out.println("Type: " + msg.getType());
			System.out.println("Status: " + msg.getStatus());
			System.out.println("Text: " + msg.getText());
		}

		private Message processMessage (Message message) {
			if (message.getType().equals("login")) {
				message.setStatus("success");
			}
			else if (message.getType().equals("logout")) {
				message.setStatus("success");
			}
			else if (message.getType().equals("text")) {
				message.setText(message.getText().toUpperCase());
			}
			return message;
		}

//		private Boolean checkLogin(Message msg){
//			if (msg.getType().equals("login")) {
//				return true;
//			}
//			return false;
//		}
//
//		private Boolean checkLogout(Message msg){
//			if (msg.getType().equals("logout")) {
//				return true;
//			}
//			return false;
//		}

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
		
		private static void updateClients(Socket socket, Message message) {
			try {
				for (int i=0; i < clientList.size(); i++) {
					if (clientList.get(i) == socket) {
						continue;
					}
					else {
						List<Message> messages = new ArrayList<>();
						OutputStream outputStream = clientList.get(i).getOutputStream();
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


	}




}