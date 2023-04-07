package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderDao extends JpaRepository<OrderEntity, Integer> {
}
