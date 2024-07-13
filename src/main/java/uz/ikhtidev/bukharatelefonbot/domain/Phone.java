package uz.ikhtidev.bukharatelefonbot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "phone")
public class Phone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "is_active")
    private boolean isActive = false;

    @Column(name = "name")
    private String name;

    @Column(name = "memory")
    private String memory;

    @Column(name = "year")
    private String year;

    @Column(name = "color")
    private String color;

    @Column(name = "has_document")
    private Boolean hasDocument;

    @Column(name = "condition")
    private String condition;

    @Column(name = "charge")
    private String charge;

    @Column(name = "price")
    private String price;

    @Column(name = "is_replace")
    private Boolean isReplace;

    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "owner_contact")
    private String ownerContact;

    @Column(name = "owner_username")
    private String ownerUsername;

    @Column(name = "address")
    private String address;

    @Column(name = "image")
    private String image ="";

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

}
