package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.PaymentEntity;
import fr.sqli.cantine.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface IPaymentDao  extends JpaRepository<PaymentEntity , Integer> {
    List<PaymentEntity> findByStudent(StudentEntity studentEntity);
}
