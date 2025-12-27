package com.b2b.b2b.modules.crm.contact.entity;

import com.b2b.b2b.modules.crm.company.entity.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    @ManyToOne
    private Company company;
}
