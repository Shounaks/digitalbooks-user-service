package org.digitalbooks.entity;

//import jakarta.persistence.*;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    private String emailId;
    private boolean authorUser;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions;
}