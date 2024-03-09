package fr.sqli.cantine.dao;

import fr.sqli.cantine.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPaymentDao  extends JpaRepository<PaymentEntity , Integer> {
}
