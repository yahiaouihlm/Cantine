package fr.sqli.Cantine.dao;

import fr.sqli.Cantine.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IConfirmationToken  extends JpaRepository<ConfirmationToken,  String> {

    Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);

}
