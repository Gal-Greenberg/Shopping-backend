package collab.logic.plugins;

import java.util.Collections;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import collab.logic.ActionInvocationException;
import collab.logic.AdvancedElementsService;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ElementBoundary;

@Component
public class DistanceBetweenMallsPlugin implements ActionPlugin{

	private AdvancedElementsService elementsService;



	@Autowired
	public DistanceBetweenMallsPlugin(AdvancedElementsService elementsService) {
		super();
		this.elementsService = elementsService;
	}


	@Override
	public Object Action(ActionBoundary action) {


		ElementBoundary mall1 = this.elementsService.getSpecificElement(
				action.getInvokedBy().getUserId().getDomain(),
				action.getInvokedBy().getUserId().getEmail(),
				action.getElement().getElementId().getDomain(),
				(String) action.getActionAttributes().get("id"));


		ElementBoundary mall2 = this.elementsService.getSpecificElement(
				action.getInvokedBy().getUserId().getDomain(),
				action.getInvokedBy().getUserId().getEmail(),
				action.getElement().getElementId().getDomain(),
				action.getElement().getElementId().getId());


		if(!mall1.getType().equals("mall") || !mall2.getType().equals("mall"))
			throw new ActionInvocationException("this element is not a Mall so you can't do this action with this element ..!");


		double distance = getDistanceFromLatLonInKm(
				(double) mall1.getElementAttributes().get("lat"),
				(double) mall1.getElementAttributes().get("lng"),
				(double) mall2.getElementAttributes().get("lat"),
				(double) mall2.getElementAttributes().get("lng"));

		return Collections.singletonMap("dis", distance);


	}



	public double getDistanceFromLatLonInKm(double lat1, double lon1,double lat2,double lon2) {

		int R = 6371; // Radius of the earth in km

		double dLat =  deg2rad(lat2-lat1);
		double dLon = deg2rad(lon2-lon1); 

		double a = 
				Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
				Math.sin(dLon/2) * Math.sin(dLon/2); 

		return R* (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
	}


	public static double deg2rad(double deg) {

		return deg * (Math.PI/180);

	}	

}
