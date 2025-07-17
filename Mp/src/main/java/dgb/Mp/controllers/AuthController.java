package dgb.Mp.controllers;

import ch.qos.logback.core.net.SyslogOutputStream;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.Utils.JwtUtils;
import dgb.Mp.Utils.Mapper;
import dgb.Mp.refreshToken.Dtos.AccesTokenUpdateDto;
import dgb.Mp.refreshToken.Dtos.RefreshTokenRequestDto;
import dgb.Mp.refreshToken.Dtos.RefreshTokenResponseDto;
import dgb.Mp.refreshToken.RefreshToken;
import dgb.Mp.refreshToken.RefreshTokenRepository;
import dgb.Mp.refreshToken.RefreshTokenService;
import dgb.Mp.user.Dtos.*;
import dgb.Mp.user.SecurityUser;
import dgb.Mp.user.User;
import dgb.Mp.user.UserRepository;
import dgb.Mp.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserRepository userRepository;
@Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private Mapper mapper;

@Autowired
    private RefreshTokenRepository refreshTokenRepository;


    // Login endpoint to authenticate user and generate tokens
    @PostMapping("/login")
    @Operation(summary = "Log in a new user", description = "Logging in a new user and get access and refresh token")
    @Transactional
    public ResponseEntity<?> login(@RequestBody UserDtoLogin loginRequest) {
        log.debug("Login attempt for username: {}", loginRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            log.debug("Authentication successful for: {}", loginRequest.getEmail());

            Object principal = authentication.getPrincipal();
            log.debug("Principal type: {}, value: {}", principal.getClass().getName(), principal);
            User user;
            if (principal instanceof User) {
                user = (User) principal;
            } else if (principal instanceof dgb.Mp.user.SecurityUser) {
                user = ((dgb.Mp.user.SecurityUser) principal).getUser();
            } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                user = userRepository.findUserByUserName(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username));
            } else {
                throw new RuntimeException("Unexpected principal type: " + principal.getClass().getName());
            }
            log.debug("Fetched user entity: {}", user.getEmail());

            if (jwtUtils == null) {
                log.error("jwtUtils is not initialized");
                throw new RuntimeException("jwtUtils is not initialized");
            }
            log.debug("About to generate access token for user: {}", user.getEmail());
            String accessToken = jwtUtils.generateAccessToken(user);
            System.out.println("Access token: " + accessToken);
            log.debug("About to generate refresh token for user: {}", user.getEmail());
            String refreshToken = jwtUtils.generateRefreshToken(user);
            System.out.println("Refresh token: " + refreshToken);
            log.debug("Generated tokens - Access: {}, Refresh: {}", accessToken, refreshToken);

            RefreshToken token = new RefreshToken();
            token.setRefreshToken(refreshToken);
            token.setUser(user);
            token.setExpiresAt(jwtUtils.extractExpiration(refreshToken));
            log.debug("About to save refresh token for user: {}", user.getEmail());
            refreshTokenService.saveRefreshToken(token);
            log.debug("Refresh token saved for user: {}", user.getEmail());
            String roleName = user.getRole() != null ? String.valueOf(user.getRole().getName()) : "NO_ROLE";
            Long divisionId = user.getDivision() != null ? user.getDivision().getId() : null;
            Long directionId = user.getDirection() != null ? user.getDirection().getId() : null;
            Long sousDirectionId = user.getSouDirection() != null ? user.getSouDirection().getId() : null;
            SuccessfulLoginDto response = new SuccessfulLoginDto(accessToken, refreshToken,roleName,divisionId,directionId,sousDirectionId);
            log.debug("Returning successful login response for: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during login for {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    

   @PostMapping("/createDivisionAdmin")
   @Operation(summary = "Create a division admin with role ADMIN", description = "Creates a new divsion admin with role ADMIN")
   public ResponseEntity<UserDtoLogin> createDivisionAdmin(@RequestBody CreateDivisionAdminRequestDto request, @AuthenticationPrincipal SecurityUser currentUser) {
    UserDtoLogin response = userService.createDivisionAdmin(request, currentUser.getUser());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}


    @PostMapping("/createUser")
    @Operation(summary = "Create a user with role USER", description = "Creates a new user with role USER")
    public ResponseEntity<?> createUser(@RequestBody UserDtoToAddUser userDto,
                                        @AuthenticationPrincipal SecurityUser currentUser) {
        System.out.println("********************created user*******************************");
        System.out.println(userDto);
        System.out.println("**************************************************************");
        return handleUserCreation(userDto, currentUser, RoleName.USER);
    }

    @PostMapping("/createAdmin")
    @Operation(summary = "Create an admin", description = "Creates a new admin with role ADMIN")
    public ResponseEntity<?> createAdmin(@RequestBody UserDtoToAddAdmin userDto,
                                         @AuthenticationPrincipal SecurityUser currentUser) {
        System.out.println("********************created user : admin*******************************");
        System.out.println(userDto);
        System.out.println("**************************************************************");

        return handleUserCreation(userDto, currentUser, RoleName.ADMIN);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logging out", description = "Log out a specific user")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDto request) {
        refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
        return ResponseEntity.ok("Logged out successfully");
    }
    @GetMapping("/getAllUsers")
    @Operation(summary = "Get users", description = "Get users based on the specific hirerchy")
    public ResponseEntity<?> getAllUsers(@AuthenticationPrincipal SecurityUser currentUser){

        try {
            if (currentUser == null) {
                System.out.println("No authenticated user found!");
                throw new IllegalStateException("No authenticated user provided");
            }
            List<UserDtoAllUsers> response = userService.getAllUsers(currentUser.getUser());
        return ResponseEntity.ok(response);}
        catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Registration: " + e.getMessage());
        }

       }




    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequestDto refreshTokenDto) {

        System.out.println("********************refreshed token*******************************");


        System.out.println("our refresh token: " + refreshTokenDto.getRefreshToken());

        System.out.println("**************************************************************");
        String refreshToken = refreshTokenDto.getRefreshToken();

        // Validate JWT token (signature + expiration)
        if (!jwtUtils.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        // Extract user info from token
        String email = jwtUtils.extractUsername(refreshToken);
        User user = userRepository.findUserByEmail(email).orElseThrow();

        // Optional: Check token in DB if you store them
        Optional<RefreshToken> storedToken = Optional.ofNullable(refreshTokenService.getRefreshTokenbyToekn(refreshToken));
        if (storedToken.isEmpty() || storedToken.get().isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token in db");
        }


        // Generate new access token
        String newAccessToken = jwtUtils.generateAccessToken(user);


        System.out.println("********************refreshed token*******************************");
        System.out.println("********************refreshed token*******************************");
        System.out.println("********************refreshed token*******************************");
        System.out.println("********************refreshed token*******************************");
        System.out.println("********************refreshed token*******************************");
        System.out.println("********************refreshed token*******************************");
        System.out.println("********************refreshed token*******************************");
        System.out.println("********************refreshed token*******************************");
        System.out.println("********************refreshed token*******************************");


        System.out.println("New access token: " + newAccessToken);

        System.out.println("[REFRESH] Called at " + LocalDateTime.now() + " by " + Thread.currentThread().getName());

        System.out.println("**************************************************************");
        System.out.println("**************************************************************");System.out.println("**************************************************************");
        System.out.println("**************************************************************");System.out.println("**************************************************************");
        System.out.println("**************************************************************");System.out.println("**************************************************************");System.out.println("**************************************************************");System.out.println("**************************************************************");






        return ResponseEntity.ok(new AccesTokenUpdateDto(newAccessToken));




    }
/*@PostMapping("/auth/refresh")
public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
    try {
        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
        String newRefreshToken = authService.issueNewRefreshTokenIfNeeded();
        return ResponseEntity.ok(Map.of(
            "accessToken", newAccessToken,
            "refreshToken", newRefreshToken
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }
}*/

    private ResponseEntity<?> handleUserCreation(UserDtoToAdd userDto, SecurityUser currentUser, RoleName roleName) {

        try {
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user provided");
            }

            UserDtoResponse response = userService.createUser(userDto, currentUser.getUser(), roleName);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating user: " + e.getMessage());
        }
    }
        @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a User", description = "Delete a User")

        public ResponseEntity<String> deleteUser(@PathVariable Long id, @AuthenticationPrincipal SecurityUser authenticatedUser) {
            try {
                userService.deleteUser(id, authenticatedUser.getUser());
                return ResponseEntity.ok("user deleted successfully");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error deleting user: " + id);
            }

        }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update a user's nomComplet and telephone",
            description = "Only SUPER_ADMIN or ADMIN can update users")
    public ResponseEntity<UserDtoResponse> updateUser(
            @PathVariable Long id,
            @RequestParam String nomComplet,
            @RequestParam String telephone,
            @AuthenticationPrincipal SecurityUser authenticatedUser) throws MessagingException {

        UserDtoToAdd dto = new UserDtoToAdd();
        dto.setNomComplet(nomComplet);
        dto.setTelephone(telephone);

        User updatedUser = userService.updateUser(id, dto, authenticatedUser.getUser());
        return ResponseEntity.ok(new UserDtoResponse(updatedUser, null)); // Password not returned
    }
    @GetMapping("/users/me")
    public UserDtoAllUsers getMyInfo(@AuthenticationPrincipal SecurityUser currentUser) {
        return userService.getCurrentUser(currentUser.getUser());
    }
   /* @PostMapping("/profile")
    public Respo
    */

//    @PostMapping("/encode")
//    public ResponseEntity<String> encodePassword(@RequestParam String rawPassword) {
//        return ResponseEntity.ok(passwordEncoder.encode(rawPassword));
//    }

/*    @GetMapping
    public List<UserDto> getAllUsers(){ return userService.getAllUsers();}

/*
*  @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDto request) {
        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (storedToken.getExpiresAt().before(new Date())) {
            refreshTokenRepository.delete(storedToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        }

        User user = storedToken.getUser();
        String newAccessToken = jwtUtils.generateAccessToken(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);

        storedToken.setRefreshToken(newRefreshToken);
        storedToken.setExpiresAt(jwtUtils.extractExpiration(newRefreshToken));
        refreshTokenRepository.save(storedToken);

        return ResponseEntity.ok(new SuccessfulLoginDto(newAccessToken, newRefreshToken, mapper.toUserDto(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDto request) {
        refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
        return ResponseEntity.ok("Logged out successfully");
    }*/
}
