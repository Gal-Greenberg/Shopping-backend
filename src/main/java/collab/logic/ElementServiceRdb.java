package collab.logic;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import collab.dal.ElementDao;
import collab.dal.IdGenerator;
import collab.dal.IdGeneratorDao;
import collab.data.ElementEntity;
import collab.data.UserRole;
import collab.rest.NotFoundException;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@Service
public class ElementServiceRdb implements AdvancedElementsService {

	private ElementDao elementDao;
	private ElementConverter converter;
	private Validator validator;
	private String domain;
	private IdGeneratorDao idGenerator;
	private UserServiceRdb userService;

	
	@Autowired
	public ElementServiceRdb(ElementDao elementDao, UserServiceRdb userService, ElementConverter converter,
							Validator validator, IdGeneratorDao idGenerator) {
		super();
		this.elementDao = elementDao;
		this.converter = converter;
		this.validator = validator;
		this.idGenerator = idGenerator;
		this.userService = userService;
	}

	
	
	@Value("${collab.config.domain:defaultDomain}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	
	
	@Override
	@Transactional
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary element) {
		
		this.validator.validateEmail(managerEmail);
		isManager(managerDomain, managerEmail);

		if (element.getName().isEmpty())
			throw new UnvalidException("Element name is null!");
		
		if (element.getType().isEmpty())
			throw new UnvalidException("Element type is null!");

		element.setCreatedBy(new User(new UserId(managerDomain, managerEmail)));

		if (element.getActive() != null)
			element.setActive(element.getActive());
		else
			element.setActive(true);

		element.setCreatedTimestamp(new Date());

		IdGenerator newGeneratedValue = this.idGenerator.save(new IdGenerator());
		element.setElementId(new ElementId(this.domain, "" + newGeneratedValue.getNextId()));
		this.idGenerator.delete(newGeneratedValue);

		if (element.getElementAttributes() != null)
			element.setElementAttributes(element.getElementAttributes());

		if (element.getParentElement() != null && element.getParentElement().getElementId() != null) {
			this.getElementById(this.generateStringElementId(element.getParentElement().getElementId()));
			element.setParentElement(element.getParentElement());
		} else
			element.setParentElement(null);

		
		return this.converter.fromEntity(this.elementDao.save(this.converter.toEntity(element)));
	}

	
	
	@Override
	@Transactional
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,ElementBoundary update) {
		
		this.validator.validateEmail(managerEmail);
		isManager(managerDomain, managerEmail);

		ElementBoundary existingElement = this
				.getElementById(this.generateStringElementId(new ElementId(elementDomain, elementId)));

		if (update.getType() != null) 
			existingElement.setType(update.getType());
		
		
		if (update.getName() != null) 
			existingElement.setName(update.getName());
		
		if (update.getActive() != null) 
			existingElement.setActive(update.getActive());
		
		if (update.getParentElement() != null) {
			this.getElementById(this.generateStringElementId(update.getParentElement().getElementId()));
			existingElement.setParentElement(update.getParentElement());
		}
		
		if (update.getElementAttributes() != null) 
			existingElement.setElementAttributes(update.getElementAttributes());
		

		// skipped elementId, createdTimestamp & userId - they can't be updated
		System.err.println("updated Element: " + existingElement);

		return this.converter.fromEntity(this.elementDao.save(this.converter.toEntity(existingElement)));
	}

	
	
	@Override
	@Transactional(readOnly = true)
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain, String elementId) {
		
		isUserAllowed(userDomain, userEmail,
				this.generateStringElementId(new ElementId(elementDomain, elementId)));
		
		ElementBoundary toReturn = this.getElementById(this.generateStringElementId(new ElementId(elementDomain, elementId)));
		return toReturn;
	}

	
	
	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElements(String userDomain, String userEmail) {
		
		Iterable<ElementEntity> iter = this.elementDao.findAll();
		
		return StreamSupport
				.stream( iter.spliterator(), false)
				.map(this.converter::fromEntity)
				.collect(Collectors.toList());
	}

	
	
	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElements(String userDomain, String userEmail, int size, int page) {
		
		UserBoundary user = this.userService
				.getUserById(this.userService.generateStringUserId(new UserId(userDomain, userEmail)));

		if (user.getRole() == UserRole.PLAYER) {
			
			List<ElementEntity> iter = this.elementDao // get results with pagination
					.findAllByActive(true, PageRequest.of(page, size, Direction.ASC, "elementId"));
			
			System.err.println(iter);
			
			return iter.stream().map(this.converter::fromEntity).collect(Collectors.toList());
		}
		
		List<ElementEntity> iter = this.elementDao // get results with pagination
				.findAll(PageRequest.of(page, size, Direction.ASC, "elementId")).getContent();
		
		return iter.stream().map(this.converter::fromEntity).collect(Collectors.toList());

	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElementsByName(String userDomain, String userEmail, String name, int size, int page) {
		
		UserBoundary user = this.userService
				.getUserById(this.userService.generateStringUserId(new UserId(userDomain, userEmail)));

		if (user.getRole() == UserRole.PLAYER) {
			
			List<ElementEntity> iter = this.elementDao // get results with pagination
					.findAllByActiveAndNameLike(true, name, PageRequest.of(page, size, Direction.ASC, "elementId"));
			return iter
					.stream()
					.map(this.converter::fromEntity)
					.collect(Collectors.toList());
		}
		
		return this.elementDao // get results with pagination
				.findAllByNameLike(name, PageRequest.of(page, size, Direction.ASC, "elementId")).stream()
				.map(this.converter::fromEntity)
				.collect(Collectors.toList());
	}
	
	

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElementsByType(String userDomain, String userEmail, String type, int size, int page) {
		
		UserBoundary user = this.userService
				.getUserById(this.userService.generateStringUserId(new UserId(userDomain, userEmail)));

		if (user.getRole() == UserRole.PLAYER) {
			
			List<ElementEntity> iter = this.elementDao // get results with pagination
					.findAllByActiveAndType(true, type, PageRequest.of(page, size, Direction.ASC, "elementId"));
			
			return iter
					.stream()
					.map(this.converter::fromEntity)
					.collect(Collectors.toList());
		}
		
		List<ElementEntity> iter = this.elementDao // get results with pagination
				.findAllByType(type, PageRequest.of(page, size, Direction.ASC, "elementId"));
		
		return iter
				.stream()
				.map(this.converter::fromEntity)
				.collect(Collectors.toList());
	}

	
	
	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElementsByParentElement(String userDomain, String userEmail, String parentDomain,String parentId, int size, int page) {
		
		String stringElementId = this.generateStringElementId(new ElementId(parentDomain, parentId));
		
		UserBoundary user = this.userService
				.getUserById(this.userService.generateStringUserId(new UserId(userDomain, userEmail)));

		if (user.getRole() == UserRole.PLAYER) {
			
			List<ElementEntity> iter = this.elementDao.findAllByActiveAndParentElement(true,
					this.converter.toEntity(this.getElementById(stringElementId)),
					PageRequest.of(page, size, Direction.ASC, "elementId"));
			
			return iter
					.stream()
					.map(this.converter::fromEntity)
					.collect(Collectors.toList());
		}
		
		List<ElementEntity> iter = this.elementDao.findAllByParentElement(
				this.converter.toEntity(this.getElementById(stringElementId)),
				PageRequest.of(page, size, Direction.ASC, "elementId"));
		
		return iter
				.stream()
				.map(this.converter::fromEntity)
				.collect(Collectors.toList());
	}

	
	
	public void isManager(String managerDomain, String managerEmail) {
		
		UserBoundary user = userService
				.getUserById(userService.generateStringUserId(new UserId(managerDomain, managerEmail)));
		
		if (user.getRole() != UserRole.MANAGER) {
			throw new UnvalidException("The user is not manager therefore cannot create an element");
		}
	}

	
	
	public void isUserAllowed(String userDomain, String userEmail, String stringElementId) {
		
		UserBoundary user = userService
				.getUserById(userService.generateStringUserId(new UserId(userDomain, userEmail)));

		ElementBoundary element = this.getElementById(stringElementId);

		if (user.getRole() == UserRole.PLAYER && !element.getActive()) {
			throw new NotFoundException("This is not an active element - Players dont have premissions "+ stringElementId);
		}
	}

	
	
	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElements() {
		
		Iterable<ElementEntity> iter = this.elementDao.findAll();
		
		return StreamSupport
				.stream( iter.spliterator(), false)
				.map(this.converter::fromEntity)
				.collect(Collectors.toList());
	}

	
	
	@Transactional(readOnly = true)
	public ElementBoundary getElementById(String stringElementId) {
		
		return this
				.converter
				.fromEntity(this.elementDao.findById(stringElementId)
				.orElseThrow(() -> new NotFoundException("No element could be found with id: " + stringElementId)));
	}

	
	
	@Override
	@Transactional
	public void deleteAll() {
		
		this.elementDao.deleteAll();
	}

	
	
	public String generateStringElementId(ElementId elementId) {
		
		return elementId.getDomain() + "@@" + elementId.getId();
	}

}
