package dgb.Mp.Role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.privileges.Privilege;
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
@JsonIgnoreProperties({"users"})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "role_seq_gen", sequenceName = "role_seq", allocationSize = 1)
    private Long id;


   // @Enumerated(EnumType.STRING)
    private RoleName name;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_privileges",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    private Set<Privilege> privileges;


}
