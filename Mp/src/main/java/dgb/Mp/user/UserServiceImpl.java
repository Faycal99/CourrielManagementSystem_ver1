package dgb.Mp.user;

import dgb.Mp.Direction.Direction;
import dgb.Mp.Direction.DirectionRepository;
import dgb.Mp.Direction.DirectionService;
import dgb.Mp.Division.Division;
import dgb.Mp.Division.DivisionRepository;
import dgb.Mp.Division.DivisionService;
import dgb.Mp.Role.Role;
import dgb.Mp.Role.RoleRepository;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.SousDirection.SousDirection;
import dgb.Mp.SousDirection.SousDirectionRepository;
import dgb.Mp.SousDirection.SousDirectionService;
import dgb.Mp.Utils.MailSenderService;
import dgb.Mp.Utils.Mapper;
import dgb.Mp.user.Dtos.*;
import dgb.Mp.Utils.UserCredentialGenerator;
import dgb.Mp.generalAdvice.customException.HaveNotPermissionForThat;
import dgb.Mp.generalAdvice.customException.UserNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;
    private final RoleRepository roleRepository;
    private final Mapper mapper;
    private final SousDirectionRepository sousDirectionRepository;
    private final DirectionRepository directionRepository;
    private final SousDirectionService sousDirectionService;
    private final DirectionService directionService;
    private final DivisionService divisionService;
private final DivisionRepository divisionRepository;


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           MailSenderService mailSenderService,
                           RoleRepository roleRepository,
                           Mapper mapper,
                           SousDirectionRepository sousDirectionRepository,
                           @Lazy DirectionRepository directionRepository,
                           @Lazy SousDirectionService sousDirectionService,
                           @Lazy DirectionService directionService,
                           @Lazy DivisionService divisionService, DivisionRepository divisionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
        this.sousDirectionRepository = sousDirectionRepository;
        this.directionRepository = directionRepository;
        this.sousDirectionService = sousDirectionService;
        this.directionService = directionService;
        this.divisionService=divisionService;
        this.divisionRepository = divisionRepository;
    }
    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User Not found with given id"+id));
    }


    public List<UserDtoAllUsers> getAllUsers(User user) {

        List<User> users;
        if (user.getRole().getName() == RoleName.SUPER_ADMIN) {
            users = userRepository.findAll();
        } else if (user.getRole().getName() == RoleName.ADMIN) {
            if (user.getDirection() != null) {
                users = userRepository.findByDirection(user.getDirection());
            } else if (user.getDivision() != null) {
                users = userRepository.findByDivision(user.getDivision());
            } else {
                throw new RuntimeException("Admin must be associated with either a Direction or a Division.");
            }
        } else {
            throw new HaveNotPermissionForThat("You do not have permission to view users.");
        }

        return users.stream().filter(u -> !u.getId().equals(user.getId()))
                .sorted(Comparator.comparing(User::getNomComplet, String.CASE_INSENSITIVE_ORDER))
                .map(mapper::toUserDtoAllUsers)
                .collect(Collectors.toList());

        /* return users.stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .map(mapper::toUserDtoAllUsers)
                .collect(Collectors.toList());*/

    }



///

@Transactional
@Override
public UserDtoLogin createDivisionAdmin(CreateDivisionAdminRequestDto request,User user) {
    Role superAdminRole = roleRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Role SUPER_ADMIN not found"));
    if (!(user.getRole().getId().equals(1L))) {
        throw new HaveNotPermissionForThat("You, " + user.getUserName() + ", do not have permission to create users. Only SUPER_ADMIN can perform this action.");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");
    }

    Role adminRole = roleRepository.findByName(RoleName.ADMIN)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ADMIN role not found"));

    if (userRepository.existsByRole_IdAndDivision_Id(adminRole.getId(), request.getDivisionId())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "This division already has an assigned admin.");
    }

    Division division = divisionRepository.findById(request.getDivisionId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Division not found"));

    // Create and save user
    User user_created = new User();
    user_created.setEmail(request.getEmail());
    user_created.setNomComplet(request.getName());
    user_created.setUserName(request.getUsername());
    user_created.setPassword(passwordEncoder.encode(request.getPassword()));
    user_created.setRole(adminRole);
    user_created.setDivision(division);
    user_created.setQuatreChiffres(request.getQuatreChiffres());
    user_created.setProfession(request.getProfession());
    userRepository.save(user_created);

    // Return email and original password
    return new UserDtoLogin(user_created.getEmail(), request.getPassword());
}
///
    @Transactional
    @Override
    public UserDtoResponse createUser(UserDtoToAdd userDtoToAdd, User user,RoleName roleName) throws MessagingException , MailException {
        Role superAdminRole = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Role SUPER_ADMIN not found"));
        Role adminRole = roleRepository.findById(0L)
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

        if (!(user.getRole().getId().equals(1L) || user.getRole().getId().equals(0L))) {
            throw new HaveNotPermissionForThat("You, " + user.getUserName() + ", do not have permission to create users. Only SUPER_ADMIN or ADMIN can perform this action.");
        }

        Role userRole = roleRepository.findByName(RoleName.valueOf(roleName.name()))
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        validateAdminCRUDRules2(user, adminRole, userRole,userDtoToAdd);

        checkEmailUniqueness(userDtoToAdd.getEmail());

     //   validateSingleAdminPerDirection(userRole, adminRole, userDtoToAdd.getDirectionId());

        User newUser = new User();
        newUser.setEmail(userDtoToAdd.getEmail());
        newUser.setRole(userRole);
        newUser.setUserName(UserCredentialGenerator.generateUsername(userDtoToAdd.getEmail()));
        String password = UserCredentialGenerator.generatePassword();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setNomComplet(userDtoToAdd.getNomComplet());
        newUser.setTelephone(userDtoToAdd.getTelephone());
        newUser.setQuatreChiffres(userDtoToAdd.getQuatreChiffres());
        newUser.setProfession(userDtoToAdd.getProfession());

        if (roleName.equals(RoleName.USER)) {
            if (userDtoToAdd.getSouDirectionId() != null) {
                /*Division division= divisionService.getDivisionById(divisionId);
        direction.setDivision(division);*/
                SousDirection souDirection = sousDirectionService.getSousDirectionById(userDtoToAdd.getSouDirectionId());
                if (souDirection != null) {

                if (userDtoToAdd.getDirectionId() != null &&
                        !souDirection.getDirection().getId().equals(userDtoToAdd.getDirectionId())) {
                    throw new RuntimeException("SouDirection does not belong to the specified Direction.");
                }

                Direction direction = souDirection.getDirection();
                Division division = direction.getDivision();
                if (userDtoToAdd.getDivisionId() != null &&
                        !division.getId().equals(userDtoToAdd.getDivisionId())) {
                    throw new RuntimeException("Direction of the SouDirection does not belong to the specified Division.");
                }

                newUser.setSouDirection(souDirection);
                    newUser.setDirection(direction);
                    newUser.setDivision(division);}
            }
        } else if (roleName.equals(RoleName.ADMIN)) {
            if (userDtoToAdd.getDirectionId() != null) {
                Direction direction = directionService.getDirectionById(userDtoToAdd.getDirectionId());

                if (userDtoToAdd.getDivisionId() != null &&
                        !direction.getDivision().getId().equals(userDtoToAdd.getDivisionId())) {
                    throw new RuntimeException("Direction does not belong to the specified Division.");
                }

                newUser.setDirection(direction);
                newUser.setDivision(direction.getDivision());
            } else if (userDtoToAdd.getDivisionId() != null) {
                Division division = divisionService.getDivisionById(userDtoToAdd.getDivisionId());
                newUser.setDivision(division);
            } else {
                throw new RuntimeException("ADMIN must be assigned at least to a Division or Direction.");
            }
        }
mapper.toUserDtoToAdd(userRepository.save(newUser));
       // userRepository.save(newUser);
        //mapper.toDirectionDto(directionRepository.save(direction));
        return new UserDtoResponse(newUser, password);

    }





//    public User registerUser(UserDtoToAdd userDtoToAdd) throws MessagingException , MailException {
//        Role userRole = roleRepository.findByName(userDtoToAdd.getRole())
//                .orElseThrow(() -> new RuntimeException("Role not found: " + userDtoToAdd.getRole()));
//        User newUser = new User();
//        newUser.setEmail(userDtoToAdd.getEmail());
//        newUser.setRole(userRole);
//        newUser.setUserName(UserCredentialGenerator.generateUsername(userDtoToAdd.getEmail()));
//        String password = UserCredentialGenerator.generatePassword();
//       newUser.setPassword(passwordEncoder.encode(password));
////newUser.setPassword(password);
////        if(!mailSenderService.sendEmail(newUser.getEmail(), newUser.getUserName(),password)){
////
////            throw new MailNotSendException("Mail not send to the user :"+newUser.getUserName()+"for some reasons ");
////        }
//
//        return userRepository.save(newUser);
//
//    }

    @Transactional
    @Override
    public User updateUser(Long userId, UserDtoToAdd userDtoToAdd, User authenticatedUser) throws MessagingException , MailException{


        Role superAdminRole = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Role SUPER_ADMIN not found"));
        Role adminRole = roleRepository.findById(0L)
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
        System.out.println(superAdminRole.getId());
        if (!(authenticatedUser.getRole().getId().equals(1L) || authenticatedUser.getRole().getId().equals(0L)) ) {
            throw new HaveNotPermissionForThat("You, " + authenticatedUser.getUserName() + ", do not have permission to create/update users. Only SUPER_ADMIN or ADMIN can perform this action.");
        }
//               Role userRole = roleRepository.findByName(RoleName.valueOf(roleName.name()))
//           .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
//        validateAdminCRUDRules2(authenticatedUser, adminRole, userRole,userDtoToAdd);


//
//        validateAdminCRUDRules(authenticatedUser, adminRole, userRole);

       // checkEmailUniqueness(userDtoToAdd.getEmail());

        User userToUpdate = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if(userToUpdate != null) {
            userToUpdate.setNomComplet(userDtoToAdd.getNomComplet());
            userToUpdate.setTelephone(userDtoToAdd.getTelephone());
        }



        return userRepository.save(userToUpdate);
    }

    public UserDtoAllUsers getCurrentUser(User user) {
        return mapper.toUserDtoAllUsers(user);
    }

//    private void assignOrganizationalHierarchy(UserDtoToAdd dto, User newUser) {
//        if (dto.getRole().equals(RoleName.USER)) {
//            if (dto.getSouDirectionId() != null) {
//                SousDirection souDirection = sousDirectionRepository.findById(dto.getSouDirectionId())
//                        .orElseThrow(() -> new RuntimeException("SouDirection not found"));
//
//                if (dto.getDirectionId() != null &&
//                        !souDirection.getDirection().getId().equals(dto.getDirectionId())) {
//                    throw new RuntimeException("SouDirection does not belong to the specified Direction.");
//                }
//
//                Direction direction = souDirection.getDirection();
//                Division division = direction.getDivision();
//                if (dto.getDivisionId() != null &&
//                        !division.getId().equals(dto.getDivisionId())) {
//                    throw new RuntimeException("Direction of the SouDirection does not belong to the specified Division.");
//                }
//
//                newUser.setSouDirection(souDirection);
//            }
//        } else if (dto.getRole().equals(RoleName.ADMIN)) {
//            if (dto.getDirectionId() != null) {
//                Direction direction = directionRepository.findById(dto.getDirectionId())
//                        .orElseThrow(() -> new RuntimeException("Direction not found"));
//
//                if (dto.getDivisionId() != null &&
//                        !direction.getDivision().getId().equals(dto.getDivisionId())) {
//                    throw new RuntimeException("Direction does not belong to the specified Division.");
//                }
//                newUser.setDirection(direction);
//                newUser.setDivision(direction.getDivision());
//            }
//        }
//    }

    private void validatePermission(User currentUser, Role superAdminRole, Role adminRole) {
        if (!(currentUser.getRole().getId().equals(superAdminRole.getId()) || currentUser.getRole().getId().equals(adminRole.getId()))) {
            throw new HaveNotPermissionForThat("You, " + currentUser.getUserName() + ", do not have permission to create users. Only SUPER_ADMIN or ADMIN can perform this action.");
        }
    }

    private void validateAdminCRUDRules(User currentUser, Role adminRole, Role newUserRole) {
        if (currentUser.getRole().getId().equals(adminRole.getId()) && newUserRole.getId().equals(adminRole.getId())) {
            throw new HaveNotPermissionForThat("Admin users cannot create other Admins.");
        }
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already in use: " + email);
        }
    }

    private void validateSingleAdminPerDirection(Role newUserRole, Role adminRole, Long directionId) {
        if (newUserRole.getId().equals(adminRole.getId()) && directionId != null) {
            boolean adminExistsInDirection = userRepository.existsByDirectionIdAndRoleId(directionId, adminRole.getId());
            if (adminExistsInDirection) {
                throw new RuntimeException("An admin already exists for this direction.");
            }
        }
    }


    private void validateAdminCRUDRules2(User currentUser, Role adminRole, Role newUserRole, UserDtoToAdd newUserDto) {
        boolean isCurrentUserAdmin = currentUser.getRole().getId().equals(adminRole.getId());
        boolean isCreatingAdmin = newUserRole.getId().equals(adminRole.getId());

        if (isCurrentUserAdmin && isCreatingAdmin) {
            // Division admin cannot create another division admin
            if (newUserDto.getDivisionId() != null && newUserDto.getDirectionId() == null) {
                throw new HaveNotPermissionForThat("Division Admins cannot create other Division Admins.");
            }

            // Division admin can only create direction admins within their own division
            if (newUserDto.getDirectionId() != null) {
                if (currentUser.getDivision() == null ||
                        !currentUser.getDivision().getId().equals(newUserDto.getDivisionId())) {
                    throw new HaveNotPermissionForThat("You can only create Direction Admins within your own Division.");
                }
            }
        }
    }


    @Transactional
    @Override
    public void deleteUser(Long userIdToDelete, User currentUser) {
        User userToDelete = userRepository.findUserById(userIdToDelete)
                .orElseThrow(() -> new RuntimeException("User with ID " + userIdToDelete + " not found"));

        if (!(currentUser.getRole().getId().equals(1L) || currentUser.getRole().getId().equals(0L))) {
            throw new HaveNotPermissionForThat("You do not have permission to delete users.");
        }

        // If the current user is ADMIN, apply scope-based check
        if (currentUser.getRole().getId().equals(0L)) {
            // Check that ADMIN is allowed to delete the user (based on division or direction)
            if (currentUser.getDirection() != null) {
                if (userToDelete.getDirection() == null ||
                        !currentUser.getDirection().getId().equals(userToDelete.getDirection().getId())) {
                    throw new HaveNotPermissionForThat("You can only delete users in your Direction.");
                }
            } else if (currentUser.getDivision() != null) {
                if (userToDelete.getDivision() == null ||
                        !currentUser.getDivision().getId().equals(userToDelete.getDivision().getId())) {
                    throw new HaveNotPermissionForThat("You can only delete users in your Division.");
                }
            } else {
                throw new HaveNotPermissionForThat("Invalid ADMIN scope. You must be assigned to a Division or Direction.");
            }
        }

        userRepository.delete(userToDelete);
    }


}
