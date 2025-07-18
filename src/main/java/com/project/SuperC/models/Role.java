/**
 * Represents a user role entity in the database.
 * This entity defines the different roles that can be assigned to users,
 * such as ADMIN or USER, controlling their permissions and access levels
 * within the application.
 */
package com.project.SuperC.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleName name;

}
