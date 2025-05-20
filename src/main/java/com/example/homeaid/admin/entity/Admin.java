package com.example.homeaid.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Admin {

    @Id
    private Long adminId;

    private String id;
    private String password;
}
