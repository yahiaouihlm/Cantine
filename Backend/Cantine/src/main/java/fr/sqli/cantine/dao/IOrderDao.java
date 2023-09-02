package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderDao  extends JpaRepository<OrderEntity , Integer> {


    public List<OrderEntity> findByStudentId(Integer studentId);
}
