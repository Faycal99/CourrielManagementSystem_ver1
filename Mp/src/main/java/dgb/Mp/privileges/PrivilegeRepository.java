package dgb.Mp.privileges;

import dgb.Mp.privileges.enums.privilegeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByName(privilegeEnum name);

}
