package dgb.Mp.Notification;


import dgb.Mp.Notification.Dto.NotificationDTO;
import dgb.Mp.user.SecurityUser;
import dgb.Mp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 🔔 Récupérer toutes les notifications
     */
    @GetMapping("/all")
    public ResponseEntity<List<NotificationDTO>> getAllNotificationsForUser() {

        List<NotificationDTO> data = notificationService.getAllNotifications();
        return ResponseEntity.ok(data);
    }

    /**
     * 🔔 Récupérer toutes les notifications pour User
     */
//    @GetMapping("/mine")
//    public ResponseEntity<List<Notification>> getNotificationsForUser(@AuthenticationPrincipal SecurityUser currentUserDetails) {
//        var currentUser = currentUserDetails.getUser();
//        var notifications = notificationService.getNotificationsForUser(currentUser);
//        return ResponseEntity.ok(notifications);
//    }

    /**
     * 🔔 Récupérer uniquement les notifications non lues
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@AuthenticationPrincipal SecurityUser
                                                                                 currentUserDetails) {
        var currentUser = currentUserDetails.getUser();
        var unreadNotifications = notificationService.getNotificationsForUser(currentUser);
        return ResponseEntity.ok(unreadNotifications);
    }

    /**
     * ✅ Marquer toutes les notifications non lues comme lues et les supprimer
     */
    @PutMapping("/mark-all-read")
    public ResponseEntity<String> markAllAsRead(@AuthenticationPrincipal SecurityUser
                                                            currentUserDetails) {
        var currentUser = currentUserDetails.getUser();
        notificationService.readAllNotificationsByUser(currentUser);
        return ResponseEntity.ok("All read notifications have been deleted");
    }

    /**
     * ✅ Marquer une notification spécifique comme lue (par ID) et la supprimer
     */
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<String> markAsReadById(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser
                    currentUserDetails) {

        User currentUser = currentUserDetails.getUser();

        // This will throw NOT_FOUND if not found, so no need for extra checks
        String message = notificationService.readNotificationById(id, currentUser);

        return ResponseEntity.ok(message);
    }
}
