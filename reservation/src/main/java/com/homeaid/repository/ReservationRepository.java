package com.homeaid.repository;


import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Page<Reservation> findAllByCustomerId(Long userId, Pageable pageable);

  Page<Reservation> findAllByManagerId(Long managerId, Pageable pageable);

  @Query("SELECT COUNT(r) FROM Reservation r WHERE DATE(r.createdDate) = CURRENT_DATE")
  long countTodayReservations();

  Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

  @Query("SELECT COUNT(r) FROM Reservation r WHERE YEAR(r.createdDate) = :year")
  long countByYear(@Param("year") int year);

  @Query(value = """
    SELECT AVG(DATEDIFF(DATE(r.modified_date), r.requested_date))
    FROM reservation r
    WHERE YEAR(r.requested_date) = :year AND r.status = 'COMPLETED'
    """, nativeQuery = true)
  Double getAverageProcessingDays(@Param("year") int year);

//  @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, r.createdAt, r.completedAt)) " +
//      "FROM Reservation r WHERE YEAR(r.createdAt) = :year AND r.completedAt IS NOT NULL")
//  Double getAverageProcessingTime(@Param("year") int year);
}
