package dgb.Mp.Role;

import dgb.Mp.Role.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role>  findByName(RoleName name);
    Optional<Role> findById(Long id);

    void delete(Role role);
}
