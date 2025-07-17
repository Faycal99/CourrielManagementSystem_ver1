package dgb.Mp.Notification;

import dgb.Mp.Direction.Direction;
import dgb.Mp.Direction.DirectionRepository;
import dgb.Mp.Division.Division;
import dgb.Mp.Division.DivisionRepository;
import dgb.Mp.Notification.Dto.NotificationDTO;
import dgb.Mp.Notification.Dto.NotificationMessage;
import dgb.Mp.Notification.enums.Operations;

import dgb.Mp.Notification.enums.UserHierarchyRole;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.SousDirection.SousDirection;
import dgb.Mp.SousDirection.SousDirectionRepository;
import dgb.Mp.generalAdvice.customException.UserNotFoundException;
import dgb.Mp.user.User;
import dgb.Mp.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {


    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;
    private final DirectionRepository directionRepo;
    private final SousDirectionRepository sousDierctionRepo;
    private final DivisionRepository divisionRepo;
    private final UserNotificationRepository userNotificationRepo;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate, NotificationRepository notificationRepo, NotificationRepository notificationRepository, UserRepository userRepo, DirectionRepository directionRepo, SousDirectionRepository sousDierctionRepo, DivisionRepository divisionRepo, UserNotificationRepository userNotificationRepo, NotificationMapper notificationMapper) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
        this.directionRepo = directionRepo;
        this.sousDierctionRepo = sousDierctionRepo;
        this.divisionRepo = divisionRepo;
        this.userNotificationRepo = userNotificationRepo;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public List<NotificationDTO> getAllNotifications() {
        List<Notification>notifications= notificationRepo.findAll();

        List<NotificationDTO> notificationDTOS= notifications.stream().map(notificationMapper::toDto).collect(Collectors.toList());

        return ((notifications.isEmpty()) ? new ArrayList<>() : notificationDTOS);

    }

    @Transactional
    public List<NotificationDTO> getNotificationsForUser(User currentUser) {
        return userNotificationRepo.findByUser(currentUser)
                .stream()
                .sorted(Comparator.comparing((UserNotification un) ->
                        un.getNotification().getTime()).reversed())
                .map(UserNotification::getNotification)
                .map(notificationMapper::toDto)
                .toList();
    }





    @Override
    public String readNotificationById(Long id, User currentUser) {

        Optional<UserNotification> userNotification = userNotificationRepo.findByNotificationIdAndUser(id,currentUser);
        if (userNotification.isPresent()) {
            userNotificationRepo.delete(userNotification.get());
            return "Notification marked as read successfully."+userNotification;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found or already read.");
        }
    }
    @Override
    public void sendNotification(String message, String courrielNumber, Operations operation, String creator) {

        userRepo.findUserByEmail(creator).ifPresent(sender -> {
            String timestamp = LocalDateTime.now().toString();

            NotificationMessage notificationMsg = NotificationMessage.builder()
                    .email(sender.getEmail())
                    .divisionName(sender.getDivision() != null ? sender.getDivision().getDivisionName() : null)
                    .directionName(sender.getDirection() != null ? sender.getDirection().getDirectionName() : null)
                    .sousDirectionName(sender.getSouDirection() != null ? sender.getSouDirection().getSousDirectionName() : null)
                    .message(message)
                    .courrielNumber(courrielNumber)
                  //  .filesNames(filesNames)
                    .operation(operation.name())
                    .time(timestamp)
                    .build();

            // Sauvegarde la notification une seule fois dans la BDD
            saveNotification(notificationMsg);

            // Déterminer les destinataires en fonction de la hiérarchie
            List<User> recipients = resolveRecipientsByHierarchy(sender);

            // Envoyer la notification par WebSocket à chaque destinataire
            recipients.forEach(username -> {
                messagingTemplate.convertAndSend("/topic/notifications/" + username.getEmail().toLowerCase(), notificationMsg);
            });
        });

    }

    @Transactional
    public void saveNotification(NotificationMessage message) {
        Division division = (message.getDivisionName() == null ? null : divisionRepo.findByDivisionName(message.getDivisionName()));
        Direction direction = (message.getDirectionName() == null ? null : directionRepo.findByDirectionName(message.getDirectionName()));
        SousDirection sousDirection = (message.getSousDirectionName() == null ? null : sousDierctionRepo.findBySousDirectionName(message.getSousDirectionName()));

        Optional<User> senderOpt=userRepo.findUserByEmail(message.getEmail());
        if(senderOpt.isPresent()) {
            User sender = senderOpt.get();
            List<User> recipients = resolveRecipientsByHierarchy(sender);

        Notification entity = Notification.builder()
                .email(message.getEmail())
                .divisionId(division != null ? division.getId() : null)
                .directionId(direction != null ? direction.getId() : null)
                .sousDirectionId(sousDirection != null ? sousDirection.getId() : null)
                .message(message.getMessage())
                .courrielNumber(message.getCourrielNumber())
                .filesNames(message.getFilesNames())
                .operation(message.getOperation())
                .time(message.getTime())
                .read(false)
                .build();

        notificationRepo.save(entity);
        recipients.forEach(recipient -> {
            UserNotification userNotification=UserNotification.builder()
                    .notification(entity)
                    .user(recipient)
                    .build();
            userNotificationRepo.save(userNotification);
        });}else{
            throw new UserNotFoundException("User not found with username: " + message.getEmail());
        }

    }

    @Override
    public List<User> resolveRecipientsByHierarchy(User sender) {
        List<User> recipients = new ArrayList<>();
        UserHierarchyRole senderRole = determineHierarchyRole(sender);

        switch (senderRole) {
            case SIMPLE_USER -> {
                recipients.addAll(userRepo.findAllWithRolesByDirection(sender.getDirection()).stream()
                        .filter(u -> determineHierarchyRole(u) == UserHierarchyRole.DIRECTEUR)
                        .toList());
            }
            case DIRECTEUR -> {
                // Autres directeurs de la même direction
                recipients.addAll(userRepo.findAllWithRolesByDirection(sender.getDirection()).stream()
                        .filter(u -> determineHierarchyRole(u) == UserHierarchyRole.DIRECTEUR
                                && !u.getEmail().equalsIgnoreCase(sender.getEmail()))
                        .toList());

                // Chef de division
                recipients.addAll(userRepo.findAllWithRolesByDivision(sender.getDivision()).stream()
                        .filter(u -> determineHierarchyRole(u) == UserHierarchyRole.CHEF_DIVISION)
                        .toList());
            }
            case CHEF_DIVISION -> {
                // Ne notifie personne
            }
        }

        return recipients;
    }

    @Override
    public UserHierarchyRole determineHierarchyRole(User user) {
        boolean hasDivision = user.getDivision() != null;
        boolean hasDirection = user.getDirection() != null;
        boolean hasSousDirection = user.getSouDirection() != null;

        if (hasDivision && !hasDirection && !hasSousDirection) {
            return UserHierarchyRole.CHEF_DIVISION;
        } else if (isAdmin(user) && hasDivision && hasDirection && !hasSousDirection) {
            return UserHierarchyRole.DIRECTEUR;
        } else if (hasDivision && hasDirection && hasSousDirection) {
            return UserHierarchyRole.SIMPLE_USER;
        }
        return UserHierarchyRole.SIMPLE_USER;
    }

    @Override
    public boolean isAdmin(User user) {
        return user.getRole().getName().equals(RoleName.ADMIN);
    }

    @Override
    public int deleteOrphanNotifications() {
        List<Long> orphanIds = notificationRepo.findOrphanNotificationIds();

        if (orphanIds.isEmpty()) {
            return 0;
        }

        // Supprimer  les entrées dans la table des filesNames
        notificationRepo.deleteFilesNamesByNotificationIds(orphanIds);

        //  supprimer les notifications orphelines elles-mêmes
        return notificationRepo.deleteNotificationsByIds(orphanIds);
    }

    @Override
    public void readAllNotificationsByUser(User currentUser) {
        List<UserNotification>userNotifications = userNotificationRepo.findByUser(currentUser);

        userNotificationRepo.deleteAll(userNotifications);


    }
}
