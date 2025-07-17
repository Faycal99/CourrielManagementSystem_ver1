package dgb.Mp.refreshToken;


import dgb.Mp.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "refToken_seq_gen", sequenceName = "refToken_seq", allocationSize = 1)
    private Long id;

    @OneToOne
    private  User user;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date  createdAt;

    private Date  expiresAt;


    public boolean isExpired() {
        return expiresAt.toInstant().isBefore(Instant.now());
    }
}
