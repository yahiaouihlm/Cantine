package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderDao  extends JpaRepository<OrderEntity , Integer> {


    public List<OrderEntity> findByStudentId(Integer studentId);
}
