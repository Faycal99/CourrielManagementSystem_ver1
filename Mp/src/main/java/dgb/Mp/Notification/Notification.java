package dgb.Mp.Notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "notifications")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private Long divisionId;
    private Long directionId;
    private Long sousDirectionId;
    private String message;
    private String courrielNumber;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> filesNames;

    private String operation;
    private String time;
    private boolean read;
}
