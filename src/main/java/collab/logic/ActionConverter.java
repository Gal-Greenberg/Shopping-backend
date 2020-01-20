package collab.logic;

import org.springframework.stereotype.Component;

import collab.data.ActionEntity;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ActionId;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserId;


@Component
public class ActionConverter {
	
	
	public ActionBoundary fromEntity(ActionEntity action) {
		
		try {
			
			return new ActionBoundary(
					new Element(fromStringElementId(action.getElement())),
					new User(fromStringUserId(action.getInvokedBy())),
					action.getType(),
					action.getCreatedTimestamp(),
					action.getActionAttributes(),
					new ActionId(this.getActionDomain(action.getActionId()),this.getActionId(action.getActionId()))
					);
		
		
		} catch (Exception e) {
			throw new UnvalidException(e);
		}
	}

	
	
	public ActionEntity toEntity(ActionBoundary action) {
		
		try {
			
			return new ActionEntity(
					toStringActionId(action.getActionId()),
					toStringElementId(action.getElement().getElementId()),
					toStringUserId(action.getInvokedBy()),
					action.getType(),
					action.getCreatedTimestamp(),
					action.getActionAttributes()
					);
			
			
		} catch (Exception e) {
			throw new UnvalidException(e);
		}
	}

	
	
	public String toStringActionId(ActionId actionId) {
		
		return actionId.getDomain() + "@" + actionId.getId();
	}
	
	
	
	public String toStringUserId(User userId) {
		
		UserId tempUserId = userId.getUserId();
		return tempUserId.getDomain() + "@@" + tempUserId.getEmail();
	}

	
	
	public ActionId fromStringActionId(String actionId) {
		
		return new ActionId(getActionDomain(actionId), getActionId(actionId));
	}
	
	
	
	public UserId fromStringUserId(String userId) {
		
		String domain = userId.split("@@")[0];
		String email = userId.split("@@")[1];
		return new UserId(domain, email);
	}
	
	
	
	public String toStringElementId(ElementId elementId) {
		
		return elementId.getDomain() + "@@" + elementId.getId();
	}
	
	
	
	public ElementId fromStringElementId(String elementId) {
		
		return new ElementId(elementId.split("@@")[0], elementId.split("@@")[1]);
	}
	
	

	public String getActionDomain(String actionId) {
		
		return (actionId.substring(0, actionId.indexOf("@")));
	}

	
	
	public String getActionId(String actionId) {
		
		return (actionId.substring(actionId.indexOf("@"), actionId.length()));
	}

}
