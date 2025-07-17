package dgb.Mp.Components;

import dgb.Mp.Couriel.CourielService;
import dgb.Mp.Notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DatabaseCleanupScheduler {

    private final CourielService courrielService;

    private final NotificationService notificationService;

    // Methode will be executed every thursday at 15h30

    @Scheduled(cron = "0 0 12 ? * 4L", zone = "Africa/Algiers")

    public void scheduledDatabaseCleanup() {
        System.out.println("[SCHEDULER] " + LocalDateTime.now() + " - Cleaning orphan files and courriels from database...");
        courrielService.cleanDatabaseFromMissingDiskData();
        int deletedCount = notificationService.deleteOrphanNotifications();
        System.out.println("Scheduler: " + deletedCount + " - Cleaning orphan  notifications...");
        System.out.println("[SCHEDULER] " + LocalDateTime.now() + " - Cleanup finished.");
    }
}
