package com.murad.userservice.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "sub_type")
public class SubTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String       name;
    @ManyToOne
    @JoinColumn(name = "systems_id", nullable = false)
    private SystemEntity system;
}
