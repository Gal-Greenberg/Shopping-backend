package collab.logic.plugins;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import collab.logic.ActionInvocationException;
import collab.logic.AdvancedElementsService;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ElementBoundary;

@Component
public class LikeToStorePlugin implements ActionPlugin{


	private AdvancedElementsService elementsService;

	@Autowired
	public LikeToStorePlugin(AdvancedElementsService elementsService) {
		super();
		this.elementsService = elementsService;
	}



	@Override
	public Object Action(ActionBoundary action) {

		ElementBoundary store = elementsService.getSpecificElement(
				action.getInvokedBy().getUserId().getDomain(),
				action.getInvokedBy().getUserId().getEmail(),
				action.getElement().getElementId().getDomain(),
				action.getElement().getElementId().getId());


		if(!store.getType().equals("store"))
			throw new ActionInvocationException("this element is not a store so you can't do this action with this element ..!");



		int likesNum = (int) store.getElementAttributes().get("likes") + 1;
		store.getElementAttributes().replace("likes", likesNum);

		return Collections.singletonMap("like", "success"); 

	}

}
