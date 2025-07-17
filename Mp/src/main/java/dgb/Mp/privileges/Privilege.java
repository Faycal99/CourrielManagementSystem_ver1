package dgb.Mp.privileges;

import dgb.Mp.Role.Role;
import dgb.Mp.privileges.enums.privilegeEnum;
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
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "privi_seq_gen", sequenceName = "privi_seq", allocationSize = 1)
    private Long id;


    private privilegeEnum name;

    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles;


}
