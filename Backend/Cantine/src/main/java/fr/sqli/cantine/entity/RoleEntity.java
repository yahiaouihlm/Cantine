package fr.sqli.cantine.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity extends AbstractEntity {

    @Column(name = "label", nullable = false, length = 200)
    private String label;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public RoleEntity(String label, String description, UserEntity user) {
        this.label = label;
        this.description = description;
        this.user = user;
    }

}
