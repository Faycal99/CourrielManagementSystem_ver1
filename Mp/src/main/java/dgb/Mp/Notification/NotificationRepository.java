package dgb.Mp.Notification;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findBySousDirectionId(Long sousDirectionId);

    List<Notification> findByDirectionId(Long directionId);

    List<Notification> findByDivisionIdAndSousDirectionIdIsNull(Long divisionId);

    List<Notification> findByReadFalseAndSousDirectionId(Long sousDirectionId);

    List<Notification> findByReadFalseAndDirectionId(Long directionId);

    List<Notification> findByReadFalseAndDivisionIdAndSousDirectionIdIsNull(Long divisionId);

    @Query(value = """
        SELECT n.id 
        FROM notifications n 
        WHERE NOT EXISTS (
            SELECT 1 FROM user_notification u WHERE u.notification_id = n.id
        )
        """, nativeQuery = true)
    List<Long> findOrphanNotificationIds();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notification_files_names WHERE notification_id IN :ids", nativeQuery = true)
    void deleteFilesNamesByNotificationIds(@Param("ids") List<Long> notificationIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notifications WHERE id IN :ids", nativeQuery = true)
    int deleteNotificationsByIds(@Param("ids") List<Long> notificationIds);


}

