package fr.sqli.Cantine.entity;


import java.time.LocalDate;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import  jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "confirmationToken")

public class ConfirmationToken {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(name = "confirmationtoken")
    private  String confirmationToken;

    @Column(name = "uuid")
    private  Integer uuid ;
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP )
    private LocalDate createdDate;

    @OneToOne(targetEntity = AdminEntity.class ,  fetch = FetchType.EAGER)
    @JoinColumn( nullable = false , name = "admin_id")

    private  AdminEntity admin ;

    public  ConfirmationToken(){

    }

    public ConfirmationToken(AdminEntity admin) {
        this.admin = admin;
        createdDate = LocalDate.now();
        confirmationToken = UUID.randomUUID().toString();
        uuid =  new Random().nextInt((9999999 - 1000000) + 1) + 1000000 ;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Integer getUuid() {
        return uuid;
    }

    public void setUuid(Integer uuid) {
        this.uuid = uuid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public AdminEntity getAdmin() {
        return admin;
    }

    public void setAdmin(AdminEntity admin) {
        this.admin = admin;
    }
}

