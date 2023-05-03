package fr.sqli.Cantine.entity;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "admin")
public class AdminEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false )
    private Integer id;
    @Column(name = "firstname" , nullable=false , length = 99)
    private String firstname  ;

    @Column(name = "lastname" , nullable=false , length = 99)
    private String lastname  ;

    @Column(name = "email" , unique = true  , nullable=false , length = 299)
    private String email ;

    @Column(name = "password" , nullable=false , length = 2999)
    private String password  ;

    @Column(name = "birthdate" , nullable=false )
    private LocalDate birthdate ;

    @Column(name = "towen" , nullable=false , length = 399)
    private String towen ;


    @Column(name = "adresse" , nullable=false , length = 999)
    private String adresse ;

    @Column(name = "phone" , nullable=false , length = 99)
    private String phone ;

    @Column(name=  "status" , nullable=false , length = 99)
    private Integer status ;


    @ManyToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name="image_idimage", nullable=false)
    private ImageEntity image;


    @ManyToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name="function_id", nullable=false)
    private FunctionEntity function;


}
