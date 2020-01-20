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
public class FindStoresInStatePlugin implements ActionPlugin{


	private AdvancedElementsService elementsService;


	@Autowired
	public FindStoresInStatePlugin(AdvancedElementsService elementsService) {
		super();
		this.elementsService = elementsService;
	}


	// this action has an specific requirement that the state has no more than 100,000 malls and for each mall no more than 10,000 stores. 
	// all this requirements are written in the requirement Document. 
	@Override
	public Object Action(ActionBoundary action) {


		
		int requestedPage = (int)action.getActionAttributes().get("page");
		int requestedSize = (int)action.getActionAttributes().get("size"); 
		int mallPage = 0;
		int storePage = 0;
		int size = 100;



		ElementBoundary state = this.elementsService.getSpecificElement(
				action.getInvokedBy().getUserId().getDomain(),
				action.getInvokedBy().getUserId().getEmail(),
				action.getElement().getElementId().getDomain(),
				action.getElement().getElementId().getId());

		if(!state.getType().equals("state"))
			throw new ActionInvocationException("this element is not a state so you can't do this action with this element ..!");



		List<ElementBoundary> malls;
		List<Mall> mallsWithStoresNumber = new ArrayList<>();
		List<ElementBoundary> stores;
		List<ElementBoundary> results = new ArrayList<>();

		do {// get all malls in this state

			malls = elementsService.getAllElementsByParentElement(
					action.getInvokedBy().getUserId().getDomain(),
					action.getInvokedBy().getUserId().getEmail(),
					action.getElement().getElementId().getDomain(),
					action.getElement().getElementId().getId(),
					size,
					mallPage++);

			// for each mall check number of stores that contain in the action
			for (ElementBoundary mall : malls) {

				Mall m = new Mall(mall, 0);
				storePage = 0;


				// Get all Stores in a specific mall
				do {
					stores = this.elementsService.getAllElementsByParentElement(
							action.getInvokedBy().getUserId().getDomain(),
							action.getInvokedBy().getUserId().getEmail(),
							mall.getElementId().getDomain(),
							mall.getElementId().getId(),
							size,
							storePage++);

					for (ElementBoundary store : stores) {
						if(action.getActionAttributes().containsValue(store.getName())){
							m.setNumOfStores(m.getNumOfStores()+1);
						}
					}

				}while(stores.size() == size);


				if(m.getNumOfStores() > 0) {
					mallsWithStoresNumber.add(m);
					mallsWithStoresNumber = mallsWithStoresNumber.stream().sorted(	(m1, m2)->m2.getNumOfStores().compareTo(m1.getNumOfStores())	)
							.collect(Collectors.toList());
				}



			} //END for each mall check number of stores that contain in the action


		}while(malls.size() == size); //END get all malls in this state



		mallsWithStoresNumber.stream().forEach(m-> results.add(m.getMall()));

		return results
				.stream()
				.skip(requestedPage * requestedSize)
				.limit(requestedSize)
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);





	}
}
