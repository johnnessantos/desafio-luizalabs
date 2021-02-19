package com.luizalabs.clientesapi.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="client")
public class Client {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column
    @NotBlank(message = "Informe o nome.")
    private String name;

    @NotNull
    @Email
    @Column(unique=true)
    @NotBlank(message = "Informe o email.")
    private String email;

    protected Client() {}

    public Client(String name, String email){
        this.name = name;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
