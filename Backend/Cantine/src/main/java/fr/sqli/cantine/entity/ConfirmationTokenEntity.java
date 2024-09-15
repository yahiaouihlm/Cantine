package fr.sqli.cantine.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static fr.sqli.cantine.constants.ConstCantine.ADMIN_ROLE_LABEL;

@Entity
@Table(name = "confirmation-token")
@Getter
@Setter
public class ConfirmationTokenEntity extends AbstractEntity {

    @Column(name = "token")
    private String token;

    @Column(name = "uuid")
    private Integer uuid;
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id")
    private UserEntity admin;


    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private UserEntity student;


    public ConfirmationTokenEntity() {

    }

    public ConfirmationTokenEntity(UserEntity user) {
        if (user.getRoles().get(0).getLabel().equals(ADMIN_ROLE_LABEL)) {
            this.admin = user;
        } else {
            this.student = user;
        }
        createdDate = new Date();
        token = UUID.randomUUID().toString();
        uuid = new Random().nextInt((999999 - 100000) + 1) + 100000;
    }


}

