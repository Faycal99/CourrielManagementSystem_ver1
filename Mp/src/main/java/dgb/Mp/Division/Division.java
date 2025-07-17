package dgb.Mp.Division;



import dgb.Mp.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "division_seq_gen", sequenceName = "division_seq", allocationSize = 1)

    private Long id;
    private String divisionName;

    @OneToOne
    @JoinColumn(name = "director_id")
    private User director;

//    @OneToMany()
//    private Set<User> employees;

//    @ManyToOne
//    private DirectionGenerale directionGenerale;





}


