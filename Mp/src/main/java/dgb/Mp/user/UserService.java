package dgb.Mp.user;

import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.user.Dtos.*;
import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;

import java.util.List;

public interface UserService {

    User getUser(Long id);



    UserDtoResponse createUser(UserDtoToAdd userDtoToAdd, User user, RoleName roleName) throws MessagingException;

    UserDtoAllUsers getCurrentUser(User user);
   // User  registerUser(UserDtoToAdd userDtoToAdd) throws MessagingException , MailException;
    User updateUser(Long userId, UserDtoToAdd userDtoToAdd, User authenticatedUser) throws MessagingException;
    List<UserDtoAllUsers> getAllUsers(User user);
    void deleteUser(Long userId,User user);

     UserDtoLogin createDivisionAdmin(CreateDivisionAdminRequestDto request, User user);
}
