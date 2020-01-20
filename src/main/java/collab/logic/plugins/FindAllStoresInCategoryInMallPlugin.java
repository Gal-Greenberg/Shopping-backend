package collab.logic.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import collab.logic.ActionInvocationException;
import collab.logic.AdvancedElementsService;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ElementBoundary;

@Component
public class FindAllStoresInCategoryInMallPlugin implements ActionPlugin{



	private AdvancedElementsService elementsService;


	@Autowired
	public FindAllStoresInCategoryInMallPlugin(AdvancedElementsService elementsService) {
		super();
		this.elementsService = elementsService;
	}

	// this action has an specific requirement that the mall has no more than 10,000 stores. 
	// all this requirements are written in the requirement Document.
	@Override
	public Object Action(ActionBoundary action) {

		int requestedPage = (int)action.getActionAttributes().get("page");
		int requestedSize = (int)action.getActionAttributes().get("size"); 
		int page = 0;
		int size = 100;


		ElementBoundary mall = this.elementsService.getSpecificElement(
				action.getInvokedBy().getUserId().getDomain(),
				action.getInvokedBy().getUserId().getEmail(),
				action.getElement().getElementId().getDomain(),
				action.getElement().getElementId().getId());

		if(!mall.getType().equals("mall"))
			throw new ActionInvocationException("this element is not a Mall so you can't do this action with this element ..!");



		List<ElementBoundary> Stores;
		List<ElementBoundary> results = new ArrayList<>();

		do {
			Stores = elementsService.getAllElementsByParentElement(
					action.getInvokedBy().getUserId().getDomain(),
					action.getInvokedBy().getUserId().getEmail(),
					action.getElement().getElementId().getDomain(),
					action.getElement().getElementId().getId(),
					size,
					page++);



			for (ElementBoundary store : Stores)
				if(action.getActionAttributes().containsValue(store.getElementAttributes().get("category")))
					results.add(store);


		}while(Stores.size() == size);


		return results
				.stream()
				.skip(requestedPage * requestedSize)
				.limit(requestedSize)
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);


	}

}
