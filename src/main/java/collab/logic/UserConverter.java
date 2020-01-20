package collab.logic;

import org.springframework.stereotype.Component;

import collab.data.UserEntity;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@Component
public class UserConverter {
	
	
	public UserBoundary fromEntity (UserEntity user) {
		
		try {
			
			return new UserBoundary(
				fromStringUserId(user.getUserId()),
				user.getRole(),
				user.getUsername(),
				user.getAvatar());
			
		} catch (Exception e) {
			throw new UnvalidException("could not convert UserEntity to UserBoundary!"+user.toString());
		}
	}


	
	public UserEntity toEntity (UserBoundary user) {
		
		try {
			return new UserEntity(
				toStringUserId(user.getUserId()),
				user.getRole(),
				user.getUsername(),
				user.getAvatar());
			
		} catch (Exception e) {
			throw new UnvalidException("could not convert UserBoundary to UserEntity!"+user.toString());
		}
	}
	
	
	
	public String toStringUserId(UserId userId) {
		return userId.getDomain() + "@@" + userId.getEmail();
	}
	
	
	public UserId fromStringUserId(String userId) {
		return new UserId(getUserDomain(userId), getUserEmail(userId));
	}

	
	public String getUserDomain(String userId) {
		return (userId.substring(0, userId.indexOf("@@")));
	}

	
	public String getUserEmail(String userId) {
		return userId.split("@@")[1];
	}

}
