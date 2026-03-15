package com.academic.plagiarism.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Location Entity - ONE-TO-ONE relationship with User
 *
 * The Location entity OWNS the relationship (it has the foreign key column)
 * @JoinColumn(name = "user_id", unique = true) ensures:
 *   - A foreign key column 'user_id' is created in the location table
 *   - unique = true enforces the One-to-One constraint at database level
 */
@Entity
@Table(name = "location")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String address;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Province name is required")
    @Column(nullable = false)
    private String provinceName;

    @NotBlank(message = "Province code is required")
    @Column(nullable = false, length = 10)
    private String provinceCode;

    @Column
    private String country;

    /**
     * ONE-TO-ONE: This side OWNS the relationship (has the FK)
     * @JoinColumn(name = "user_id", unique = true) creates:
     *   - A column 'user_id' in the location table
     *   - Unique constraint so one user = one location
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    @ToString.Exclude
    private User user;
}
