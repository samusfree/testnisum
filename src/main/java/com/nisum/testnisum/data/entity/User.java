package com.nisum.testnisum.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    private String name;
    private String email;
    private String password;
    private LocalDate created;
    private LocalDate modified;
    @Column(name = "last_login")
    private LocalDate lastLogin;
    private String token;
    private boolean active;
}
