package collab.rest.boundaries;

import java.util.Date;
import java.util.Map;


public class ActionBoundary {
	
	
	private ActionId actionId;
	private Element element;
	private User invokedBy;
	private String type;
	private Date createdTimestamp;
	private Map<String, Object> actionAttributes;

	
	
	public ActionBoundary() {
	}

	
	
	public ActionBoundary(Element element, User invokedBy, String type, Date createdTimestamp,
			Map<String, Object> actionAttributes, ActionId actionId) {
		super();
		this.element = element;
		this.invokedBy = invokedBy;
		this.type = type;
		this.createdTimestamp = createdTimestamp;
		this.actionAttributes = actionAttributes;
		this.actionId = actionId;
	}
	
	

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public User getInvokedBy() {
		return invokedBy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Map<String, Object> getActionAttributes() {
		return actionAttributes;
	}

	public void setActionAttributes(Map<String, Object> actionAttributes) {
		this.actionAttributes = actionAttributes;
	}

	public ActionId getActionId() {
		return actionId;
	}

	public void setActionId(ActionId actionId) {
		this.actionId = actionId;
	}

	public void setInvokedBy(User invokedBy) {
		this.invokedBy = invokedBy;

	}
}
