package collab.logic;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@Component
public class Validator {
	
	public boolean validateUserBoundary(UserBoundary user) {
		
		if (user.getRole() == null)
			throw new UnvalidException("UserRole is null!");
		
		if (user.getAvatar() == null || user.getAvatar().isEmpty())
			throw new UnvalidException("User Avatar is missing!");
		
		this.validateUserId(user.getUserId());
		
		return true;
	}

	
	
	public boolean validateUserId(UserId userId) {
		
		if (userId == null || userId.getDomain().isEmpty() || userId.getEmail().isEmpty())
			throw new UnvalidException("User Id is null!");
		
		this.validateEmail(userId.getEmail());
		
		return true;
	}

	
	
	public boolean validateElement(Element element) {
		
		if (element == null || element.getElementId().getId().isEmpty() || element.getElementId().getDomain().isEmpty())
			throw new UnvalidException("Element Id has null attributes!");
		
		return true;
	}

	
	public boolean validateActionBoundary(ActionBoundary actionBoundary) {
		
		if (actionBoundary.getType().isEmpty())
			throw new UnvalidException("Action type is null!");
		
		if (actionBoundary.getInvokedBy() == null)
			throw new UnvalidException("User Id is null!");
		
		
		this.validateUserId(actionBoundary.getInvokedBy().getUserId());
		this.validateElement(actionBoundary.getElement());
		
		return true;
	}

	
	
	public boolean validateEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{0,9}$";
		Pattern pat = Pattern.compile(emailRegex);
		if (email == null || !pat.matcher(email).matches())
			throw new UnvalidException("The email: " + email + " is invalid!");
		return true;
	}
}
