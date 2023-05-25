package fr.sqli.Cantine.entity;


import java.util.Date;
import java.util.Random;
import java.util.UUID;
import  jakarta.persistence.*;

@Entity
@Table(name = "confirmation-token")
public class ConfirmationTokenEntity {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(name = "token")
    private  String token;

    @Column(name = "uuid")
    private  Integer uuid ;
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP )
    private Date createdDate;

    @OneToOne(targetEntity = AdminEntity.class ,  fetch = FetchType.EAGER)
    @JoinColumn( nullable = false , name = "admin_id")

    private  AdminEntity admin ;


    public ConfirmationTokenEntity(){

    }

    public ConfirmationTokenEntity(AdminEntity admin) {
        this.admin = admin;
        createdDate = new Date();
        token = UUID.randomUUID().toString();
        uuid =  new Random().nextInt((9999999 - 1000000) + 1) + 1000000 ;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

