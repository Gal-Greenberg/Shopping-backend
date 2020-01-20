package collab.logic;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import collab.dal.ActionDao;
import collab.dal.ElementDao;
import collab.dal.IdGeneratorDao;
import collab.data.ActionEntity;
import collab.data.UserRole;
import collab.logic.plugins.ActionPlugin;
import collab.rest.NotFoundException;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ActionId;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;
import collab.dal.IdGenerator;

@Service
public class ActionsServiceRdb implements ActionsService {
	
	private ActionDao actionDao;
	private ActionConverter converter;
	private Validator validator;
	private String domain;
	private ElementServiceRdb elementService;
	private UserServiceRdb userService;
	private ApplicationContext springContext;
	private IdGeneratorDao idGenerator;

	
	
	@Autowired
	public ActionsServiceRdb(ElementServiceRdb elementService, ActionDao actionDao, ActionConverter converter, Validator validator, ElementDao elementDao, UserServiceRdb userService, IdGeneratorDao idGenerator, ApplicationContext springContext) {
		super();
		this.actionDao = actionDao;
		this.converter = converter;
		this.validator = validator;
		this.idGenerator = idGenerator;
		this.elementService = elementService;
		this.userService = userService;
		this.springContext = springContext;
	}

	
	
	@Value("${collab.config.domain:defaultDomain}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	
	
	@Override
	@Transactional
	public Object invoke(ActionBoundary newAction) {
		
		System.err.println(newAction.getElement().getElementId());
		this.validator.validateActionBoundary(newAction);
		
		// generate ID using DATABSE
		IdGenerator newGeneratedValue = this.idGenerator.save(new IdGenerator());
		String id = "" + newGeneratedValue.getNextId();
		this.idGenerator.delete(newGeneratedValue);
		
		newAction.setActionId(new ActionId(this.domain, id));
		newAction.setCreatedTimestamp(new Date());
		
		UserId userId = newAction.getInvokedBy().getUserId();
		ElementId elementId = newAction.getElement().getElementId();
		
		isPlayer(userId.getDomain(), userId.getEmail());
		
		this.elementService.getSpecificElement(userId.getDomain(), userId.getEmail(), elementId.getDomain(), elementId.getId());
		
		newAction.setInvokedBy(new User(userId));
		newAction.setElement(new Element(elementId));
		
		this.actionDao.save(this.converter.toEntity(newAction));

		String actionType = newAction.getType();
		
		String plugInClassName = 
				"collab.logic.plugins." + 
				actionType.substring(0, 1).toUpperCase()
				+ actionType.substring(1) + "Plugin";
		
		
		try {
			
			Class<?> pluginClass = Class.forName(plugInClassName);
			ActionPlugin plugin = (ActionPlugin) this.springContext.getBean(pluginClass);
			
			return plugin.Action(newAction);
		
		}catch(Exception e) {
			throw new NotFoundException(e.getMessage());
		}
		
	}

	
	
	@Override
	@Transactional(readOnly = true)
	public List<ActionBoundary> getAllActions() {
		
		Iterable<ActionEntity> iter = this.actionDao.findAll();
		
		return StreamSupport
				.stream( iter.spliterator(), false)
				.map(this.converter::fromEntity).
				collect(Collectors.toList());
	}

	
	
	@Override
	@Transactional
	public void deleteAll() {
		
		this.actionDao.deleteAll();
	}
	
	
	
	public void isPlayer(String userDomain, String userEmail) {
		
		UserBoundary user = this.userService.getUserById(this.userService.generateStringUserId(new UserId(userDomain, userEmail)));
		
		if (user.getRole() != UserRole.PLAYER) {
			throw new NotFoundException("The user is not a player, therefore cannot invoke an action");
			
		}
	}
	
	
}
