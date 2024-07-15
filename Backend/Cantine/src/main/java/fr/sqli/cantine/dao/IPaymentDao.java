package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.PaymentEntity;
import fr.sqli.cantine.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface IPaymentDao  extends JpaRepository<PaymentEntity , String> {

    @Query(value = "SELECT payment FROM PaymentEntity payment WHERE payment.student = ?1")
    List<PaymentEntity> findByStudent(UserEntity student);
}
