package dgb.Mp.SousDirection;


import dgb.Mp.Direction.Direction;
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
public class SousDirection {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
@SequenceGenerator(name = "SousDir_seq_gen", sequenceName = "SousDir_seq", allocationSize = 1)

    private Long id;
    private String sousDirectionName;




//    @OneToOne
//    @JoinColumn(name = "director_id")
//    private User director;


    @ManyToOne(fetch = FetchType.EAGER)
    private Direction direction;








}
