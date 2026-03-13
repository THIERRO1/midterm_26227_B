package com.academic.integrity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Province Entity - represents geographic provinces
 * Used to group users by location (One-to-Many with Users)
 */
@Entity
@Table(name = "provinces")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Province code (e.g., "KGL", "EST", "WST")
    @Column(name = "province_code", unique = true, nullable = false, length = 10)
    private String provinceCode;

    // Full province name (e.g., "Kigali", "Eastern Province")
    @Column(name = "province_name", nullable = false, length = 100)
    private String provinceName;

    @Column(name = "description")
    private String description;

    /**
     * One-to-Many: One Province can have many Users
     * mappedBy refers to the 'province' field in the User entity
     */
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;
}
