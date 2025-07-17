package dgb.Mp.Couriel;

import dgb.Mp.Couriel.enums.Couriel_Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourielRepository extends JpaRepository<Couriel, Long> {
    Optional<Couriel> findById(Long id);



    Page<Couriel> findAll(Pageable pageable);

    @Query("SELECT c FROM Couriel c WHERE c.fromDirection.id = :id OR c.toDirection.id = :id")
    Page<Couriel> findByDirectionId(@Param("id") Long directionId, Pageable pageable);

    @Query("SELECT c FROM Couriel c WHERE c.fromDivision.id=:id OR c.toDivision.id=:id")
    Page<Couriel> findByDivisionId(@Param("id") Long divisionId, Pageable pageable);

    @Query("SELECT c FROM Couriel c WHERE c.fromSouDirection.id=:id OR c.toSouDirection.id=:id")
    Page<Couriel> findBySouDirectionId(@Param("id") Long divisionId, Pageable pageable);
    Optional<Couriel> findByCourielNumber(String courielNumber);

    boolean existsByCourielNumber(String courielNumber);


    @Query("SELECT c FROM Couriel c LEFT JOIN FETCH c.courielFiles WHERE c.courielNumber = :number")
    Optional<Couriel> findByCourielNumberWithFiles(@Param("number") String number);

    @Query("SELECT DISTINCT c FROM Couriel c LEFT JOIN FETCH c.courielFiles")
    List<Couriel> findAllWithFiles();


long countByType(Couriel_Type type);

    @Query("SELECT COUNT(*) FROM Couriel c WHERE c.type=:type And ( c.fromDivision.id=:id OR c.toDivision.id=:id)")
    Long countByTypeAndDivisionId(@Param("type")Couriel_Type type,@Param("id") Long divisionId);
    @Query("SELECT COUNT(*) FROM Couriel c WHERE c.type=:type And ( c.fromDirection.id=:id OR c.toDirection.id=:id)")
    Long countByTypeAndDirectionId(@Param("type")Couriel_Type type,@Param("id") Long directionId);
    @Query("SELECT COUNT(*) FROM Couriel c WHERE c.type=:type And ( c.fromSouDirection.id=:id OR c.toSouDirection.id=:id)")
    Long countByTypeAndSouDirectionId(@Param("type")Couriel_Type type,@Param("id") Long souDirectionId);


    @Query("SELECT MONTH(c.arrivedDate) AS month, COUNT(c) AS total " +
            "FROM Couriel c " +
            "WHERE YEAR(c.arrivedDate) = :year " +
            "GROUP BY MONTH(c.arrivedDate)")
    List<Object[]> countMailsByMonth(@Param("year") int year);

    @Query("""
    SELECT MONTH(c.arrivedDate), COUNT(c)
    FROM Couriel c
    WHERE YEAR(c.arrivedDate) = :year
      AND c.type = 'Arrivé'
      AND (c.fromDivision.id = :id OR c.toDivision.id = :id)
    GROUP BY MONTH(c.arrivedDate)
""")
    List<Object[]> countEntrantByDivisionId(@Param("id") Long divisionId, @Param("year") int year);

    // Sortant by sentDate
    @Query("""
    SELECT MONTH(c.sentDate), COUNT(c)
    FROM Couriel c
    WHERE YEAR(c.sentDate) = :year
      AND c.type = 'Départ'
      AND (c.fromDivision.id = :id OR c.toDivision.id = :id)
    GROUP BY MONTH(c.sentDate)
""")
    List<Object[]> countSortantByDivisionId(@Param("id") Long directionId, @Param("year") int year);


    @Query("""
    SELECT MONTH(c.arrivedDate), COUNT(c)
    FROM Couriel c
    WHERE YEAR(c.arrivedDate) = :year
    AND c.type = 'Arrivé'
      AND (c.fromDirection.id = :id OR c.toDirection.id = :id)
    GROUP BY MONTH(c.arrivedDate)
""")
    List<Object[]> countEntrantByDirectionId(@Param("id") Long directionId, @Param("year") int year);

    // For Direction-level
    @Query("""
    SELECT MONTH(c.sentDate), COUNT(c)
    FROM Couriel c
    WHERE YEAR(c.sentDate) = :year
    AND c.type = 'Départ'
      AND (c.fromDirection.id = :id OR c.toDirection.id = :id)
    GROUP BY MONTH(c.sentDate)
""")
    List<Object[]> countSortantByDirectionId(@Param("id") Long directionId, @Param("year") int year);

    // For SousDirection-level
    @Query("""
    SELECT MONTH(c.arrivedDate), COUNT(c)
    FROM Couriel c
    WHERE YEAR(c.arrivedDate) = :year
    AND c.type = 'Arrivé'
      AND (c.fromSouDirection.id = :id OR c.toSouDirection.id = :id)
    GROUP BY MONTH(c.arrivedDate)
""")
    List<Object[]> countEntrantBySouDirectionId(@Param("id") Long souDirectionId, @Param("year") int year);


    @Query("""
    SELECT MONTH(c.sentDate), COUNT(c)
    FROM Couriel c
    WHERE YEAR(c.sentDate) = :year
    AND c.type = 'Départ'
      AND (c.fromSouDirection.id = :id OR c.toSouDirection.id = :id)
    GROUP BY MONTH(c.sentDate)
""")
    List<Object[]> countSortantBySouDirectionId(@Param("id") Long souDirectionId, @Param("year") int year);

    List<Couriel> findByFromSouDirection_Id(Long id);
    List<Couriel> findByToSouDirection_Id(Long id);

    List<Couriel> findByFromDirection_Id(Long id);
    List<Couriel> findByToDirection_Id(Long id);










// List<Couriel> findByToDivisionId(Long toDivisionId, Pageable pageable);
//    List<Couriel> findByFromDirectionId(Long fromDirectionId, Pageable pageable);
//    List<Couriel> findByToDirectionId(Long toDirectionId,Pageable pageable);
//    List<Couriel> findByFromSouDirectionId(Long fromSouDirectionId, Pageable pageable);
//    List<Couriel> findByToSouDirectionId(Long toSouDirectionId,Pageable pageable);


}
