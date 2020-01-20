package collab.logic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import collab.dal.UserDao;
import collab.data.UserEntity;
import collab.rest.NotFoundException;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@Service
public class UserServiceRdb implements UsersService {
	
	private UserDao userDao;
	private UserConverter converter;
	private Validator validator;
	private String domain;
	
	
	
	@Autowired
	public UserServiceRdb(UserDao userDao, UserConverter converter, Validator validator) {
		super();
		this.userDao = userDao;
		this.converter = converter;
		this.validator = validator;
	}
	
	
	
	@Value("${collab.config.domain:defaultDomain}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	
	
	@Override
	public UserBoundary create(UserBoundary user) {
		
		user.getUserId().setDomain(this.domain);
		String stringUserId = this.converter.toStringUserId(user.getUserId());

		this.validator.validateUserBoundary(user);
		boolean userExist = false;
		
		try {
			user = this.getUserById(stringUserId);
			userExist = true;
			
		} catch (Exception e) {
			System.err.println("It is all right the user does not exist");
		}
		if(userExist)
			throw new UnvalidException("A user with the id: " + stringUserId + " already exists!");
		
		return 
			this.converter.fromEntity(
				this.userDao.save(
				this.converter.toEntity(user)));
	}
	
	
	
	@Override
	public UserBoundary update(UserBoundary update) {
		
		String stringUserId = this.converter.toStringUserId(update.getUserId());
		UserBoundary existingUser = this.getUserById(stringUserId);

		if (update.getAvatar() != null) {
			existingUser.setAvatar(update.getAvatar());
		}
		if (update.getUsername() != null) {
			existingUser.setUsername(update.getUsername());
		}
		if (update.getRole() != null) {
			existingUser.setRole(update.getRole());
		}

		System.err.println("updated User: " + existingUser); // print to console

		return this.converter.fromEntity(
			this.userDao
				.save(converter.toEntity(existingUser)));
	}

	
	
	@Override
	@Transactional(readOnly = true)
	public UserBoundary login(String domain, String email) {
		
		this.validator.validateEmail(email);

		String stringUserId = generateStringUserId(new UserId(domain, email));
		return this.getUserById(stringUserId);
	}

	
	
	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers() {
		
		Iterable<UserEntity> iter = this.userDao.findAll();
		
		return StreamSupport
				.stream(iter.spliterator(), false)
				.map(this.converter::fromEntity)
				.collect(Collectors.toList());
	}

	
	
	@Override
	@Transactional
	public void deleteAll() {
		this.userDao.deleteAll();
	}
	
	
	
	public String generateStringUserId(UserId userId) {
		return this.converter.toStringUserId(userId);
	}
	
	
	
	public UserBoundary getUserById(String stringUserId) {
		
		return this
				.converter
				.fromEntity(this.userDao.findById(stringUserId)
				.orElseThrow(()->new NotFoundException("no user could be found with id: " + stringUserId)));
	}

}
