package dgb.Mp.Notification;

import dgb.Mp.Notification.Dto.NotificationDTO;
import dgb.Mp.Notification.Dto.NotificationMessage;
import dgb.Mp.Notification.enums.Operations;
import dgb.Mp.Notification.enums.UserHierarchyRole;
import dgb.Mp.user.User;

import java.util.List;
import java.util.Set;

public interface NotificationService {

    public List<NotificationDTO> getAllNotifications();
    public List<NotificationDTO> getNotificationsForUser(User currentUser);
    public String readNotificationById(Long id, User currentUser);
    public void sendNotification(String message, String courrielNumber, Operations operation, String creator);
    public void saveNotification(NotificationMessage message);
    List<User> resolveRecipientsByHierarchy(User sender);
    UserHierarchyRole determineHierarchyRole(User user);
    boolean isAdmin(User user);

    public int deleteOrphanNotifications();
    public void readAllNotificationsByUser(User currentUser);
}
