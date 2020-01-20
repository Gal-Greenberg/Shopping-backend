package collab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import collab.dal.UserDao;
import collab.data.UserEntity;
import collab.data.UserRole;
import collab.data.utils.EntityFactory;
import collab.rest.NotFoundException;

//@Profile("production")
//@Component
public class UserEntityDemo implements CommandLineRunner {

	private EntityFactory factory;
	private UserDao userDao;

	@Autowired
	public UserEntityDemo(EntityFactory factory, UserDao userDao) {
		super();
		this.factory = factory;
		this.userDao = userDao;
	}

	@Override
	public void run(String... args) throws Exception {
//		System.err.println("\n\n ----------------  User Entity Start: -------------------- \n");
//		String username = "Player1";
//		UserRole ur = UserRole.PLAYER;
//		String userEmail = "yuvalbne@gmail.com";
//		String userDomain = "2020a.alik";
//		String avatar = ":)";
//		String username1 = "Manager";
//		UserRole ur1 = UserRole.MANAGER;
//		String userEmail1 = "Nofaralfasi@gmail.com";
//		String avatar1 = ":')";
//		
//		UserEntity player1 = factory.createNewUser( userEmail+"@@"+userDomain, username, avatar, ur);
//		System.err.println("new user:" + player1 + "\n");
//		player1 = this.userDao.save(player1);
//		System.err.println("stored user:" + player1 + "\n");
//		UserEntity managerDemo = factory.createNewUser( userEmail1+"@@"+userDomain, username1, avatar1, ur1);
//		System.err.println("new user:" + managerDemo + "\n");
//		managerDemo = this.userDao.save(managerDemo);
//		System.err.println("stored user:" + managerDemo + "\n");
		/*this.userDao.deleteAll();
		if (!this.userDao.findAll().iterator().hasNext()) {
			System.err.println("\nSuccessfully deleted all users");
		
		} else {
			throw new RuntimeException("Error! there is a user in the memory after deletion");
		}
		System.out.println("\n ----------------  User Entity End -------------------- \n\n");
		
		*/
		UserEntity userFromDB = userDao.findById("Nofaralfasi@gmail.com@@2020a.alik")
				.orElseThrow(()->new NotFoundException("no user could be found "));
		System.err.println(userFromDB.getAvatar());
	}

}