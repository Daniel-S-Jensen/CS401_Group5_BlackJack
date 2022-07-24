import java.io.Serializable;

public class Message implements Serializable {
	protected MessageType type;
	protected MessageStatus status;
	protected int value;
	protected String name;				//for signup
	protected String username;			//for login
	protected String password;			//for login
	protected User user;				//

	//constructors
	public Message(MessageType type) {
		this.type = type;
		this.status = MessageStatus.undefined;
	}

	
	//getters and setters
	public MessageStatus getStatus() {
		return status;
	}

	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public MessageType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	

	

}
