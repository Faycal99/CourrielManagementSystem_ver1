package dgb.Mp.Picrures;

import jakarta.persistence.*;
import jdk.jfr.Timespan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Picture {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "picture_seq_gen", sequenceName = "picture_seq", allocationSize = 1)
    private Long id;

    private String picturePath;

    private String extension;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date creationDate;

}
