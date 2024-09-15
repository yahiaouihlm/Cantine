package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.OrderEntity;
import fr.sqli.cantine.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderDao extends JpaRepository<OrderEntity, String> {

    public List<OrderEntity> findByStudentOrderByCreationDateDesc(UserEntity student);

    @Query(value = "SELECT order FROM OrderEntity order  WHERE  order.id = ?1")
    public Optional<OrderEntity> findOrderById(String id);

    public List<OrderEntity> findByCreationDate(LocalDate creationDate);

    public List<OrderEntity> findByStudentIdAndCreationDate(String studentUuid, LocalDate creationDate);
}
