package com.example.homeaid.customer.entity;

import com.example.homeaid.global.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private Integer point;

    @Column(name = "preferred_region", length = 100)
    private String preferredRegion;

    @Column(name = "has_pet")
    private Boolean hasPet;

    @Lob
    @Column(name = "profile_image")
    private String profileImage;
}

