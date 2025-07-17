package dgb.Mp.SousDirection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SousDirectionRepository extends JpaRepository<SousDirection, Long> {
     SousDirection findBySousDirectionName(String sousDirectionName);
    //List<Direction> findByDivision_Id(Long id);
    List<SousDirection> findByDirection_IdAndDirection_Division_Id(Long directionId, Long divisionId);}