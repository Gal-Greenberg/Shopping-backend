package collab.logic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import collab.dal.ElementDao;
import collab.data.ElementEntity;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserId;



@Component
public class ElementConverter {

	private ElementDao elementDao;
	
	
	
	@Autowired
	public ElementConverter(ElementDao elementDao) {
		super();
		this.elementDao = elementDao;
	}
	
	
	
	public ElementBoundary fromEntity(ElementEntity element) {
		
		//System.err.println(element.getElementId());
		//System.err.println(this.fromStringElementId(element.getElementId()));
		//System.err.println(this.fromStringUserId(element.getCreatedBy()));
		
		try {
			
			return new ElementBoundary(
					this.fromStringElementId(element.getElementId()),
					element.getName(),
					element.getType(),
					element.getActive(),
					element.getCreatedTimestamp(),
					new User(this.fromStringUserId(element.getCreatedBy())),
					(element.getParentElement() != null)?new Element(this.fromStringElementId(element.getParentElement().getElementId())) : null,
					element.getElementAttributes()
					);

		} catch (Exception e) {
			throw new UnvalidException("Coulde not convert Element from Entity to Boundary");
		}
	}
	

	
	public ElementEntity toEntity(ElementBoundary element) {
		
		try {
			
			ElementEntity parent = null;
			if (element.getParentElement() != null &&
					element.getParentElement().getElementId() != null &&
					!element.getParentElement().getElementId().getDomain().isEmpty()) {
				
				String parentElement = this.toStringElementId(element.getParentElement().getElementId());
				
				parent = this.elementDao
							.findById(parentElement)
							.orElseThrow(()->new UnvalidException("no parent Element with id: " + parentElement));
			}
			
			return new ElementEntity(
					this.toStringElementId(element.getElementId()),
					element.getName(),
					element.getType(),
					(element.getActive() != null)? element.getActive() : true,
					element.getCreatedTimestamp(),
					this.toStringUserId(element.getCreatedBy()),
					parent,
					element.getElementAttributes()
					);
						
			
		} catch (Exception e) {
			throw new UnvalidException("Coulde not convert Element to Entity from Boundary");
		}
		
	}
	
	
	
	public String toStringElementId(ElementId elementId) {
		
		return elementId.getDomain() + "@@" + elementId.getId();
	}
	
	
	
	public ElementId fromStringElementId(String elementId) {
		
		return new ElementId(elementId.split("@@")[0], elementId.split("@@")[1]);
	}
	
	
	
	public String toStringUserId(User userId) {
		
		return userId.getUserId().getDomain() + "@@" + userId.getUserId().getEmail();
	}
	
	
	
	public UserId fromStringUserId(String userId) {
		
		return new UserId(userId.split("@@")[0], userId.split("@@")[1]);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
}
