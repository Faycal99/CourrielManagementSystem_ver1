package dgb.Mp.History;

import dgb.Mp.Couriel.Couriel;
import dgb.Mp.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "history_seq_gen", sequenceName = "history_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    private Couriel courrier;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    private User updatedBy;

    private String actionType; // e.g., "CREATE", "UPDATE"

    private LocalDate timestamp = LocalDate.now(ZoneId.systemDefault());
}
