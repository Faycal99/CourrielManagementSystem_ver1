package dgb.Mp.Direction;

import dgb.Mp.Division.Division;
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

public class Direction {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
@SequenceGenerator(name = "dir_seq_gen", sequenceName = "dir_seq", allocationSize = 1)

    private Long id;
    private String directionName;


    @ManyToOne
    @JoinColumn(name = "division_id")
    private Division division;

    @OneToOne
    @JoinColumn(name = "director_id")
    private User director;

//    @OneToMany(mappedBy = "direction")
//    private Set<User> employees;
    /*
//      @ManyToOne
   private Division division;*/


}
