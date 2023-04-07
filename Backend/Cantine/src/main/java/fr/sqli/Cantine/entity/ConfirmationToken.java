package fr.sqli.Cantine.entity;


import java.util.Date;
import java.util.Random;
import java.util.UUID;
import  jakarta.persistence.*;
@Entity
@Table(name = "confirmationtoken")
public class ConfirmationToken {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer tokenid;

    @Column(name = "confirmationtoken")
    private  String confirmationToken;




    @Column(name = "uuiduser")
    private  Integer useruuid ;
    @Column(name = "createddate")
    @Temporal(TemporalType.TIMESTAMP )
    private Date createdDate;

    @OneToOne(targetEntity = UserEntity.class ,  fetch = FetchType.EAGER)
    @JoinColumn( nullable = false , name = "user_id")

    private  UserEntity user ;

    public ConfirmationToken(UserEntity user) {
        this.user = user;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
        useruuid =  new Random().nextInt((9999999 - 1000000) + 1) + 1000000 ;
    }
    public  ConfirmationToken(){}
    public Integer getTokenid() {
        return tokenid;
    }

    public void setTokenid(Integer tokenid) {
        this.tokenid = tokenid;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


    public Integer getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(Integer useruuid) {
        this.useruuid = useruuid;
    }
}

