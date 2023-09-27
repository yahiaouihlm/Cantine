package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IOrderDao  extends JpaRepository<OrderEntity , Integer> {


    public List<OrderEntity> findByStudentId(Integer studentId);

    public List<OrderEntity> findByCreationDate(LocalDate creationDate);
    public List<OrderEntity>findByStudentIdAndCreationDate(Integer studentId, LocalDate creationDate);
}
