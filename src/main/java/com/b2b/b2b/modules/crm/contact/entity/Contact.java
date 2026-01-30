package com.b2b.b2b.modules.crm.contact.entity;

import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.shared.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id", nullable = false)
    private  Integer id;

    @NotBlank(message = "first name name is required")
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "last name name is required")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Email(message = "Please provide a valid email address")
    @Size(max = 255)
    @NotBlank(message = "Email is required")
    @Column(name = "email",  nullable = false, length = 255)
    private String email;

    @Pattern(regexp = "^\\+?[0-9.]{7,15}$", message = "Invalid phone number format")
    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 100)
    @Column(name = "job_title",  length = 100)
    private String jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @NotNull(message = "Company ID is required to think this contact")
    private Company company;
}
