package collab.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import collab.logic.ActionInvocationException;
import collab.logic.ActionsService;
import collab.rest.boundaries.ActionBoundary;

@RestController
public class ActionController {
	
	private ActionsService actionsServices;
	
	
	@Autowired
	public ActionController(ActionsService actionsServices) {
		this.actionsServices = actionsServices;
	}


	
	@RequestMapping(
			path = "/collab/actions",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeAction(@RequestBody ActionBoundary newAction) {
		
		return  this.actionsServices.invoke(newAction);
	}
	
	
	
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public Map<String, String> handleElementNotFoundException(NotFoundException e) {
		
		String message = e.getMessage();
		if (message == null) {
			message = "Action could not be found";
		}

		Map<String, String> errorMessage = new HashMap<>();
		errorMessage.put("error", message);

		return errorMessage;
	}
	
	
	
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleActionInvocationException(ActionInvocationException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "Action could not invoked";
		}

		Map<String, String> errorMessage = new HashMap<>();
		errorMessage.put("error", message);

		return errorMessage;
	}
	
	
}
