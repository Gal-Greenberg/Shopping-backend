package collab.rest.boundaries;

import collab.data.UserRole;

public class NewUserForm {
	
	
	private String email;
	private UserRole role;
	private String username;
	private String avatar;

	
	
	public NewUserForm() {
	}
	
	

	public NewUserForm(String email, UserRole role, String username, String avatar) {
		super();
		this.email = email;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
