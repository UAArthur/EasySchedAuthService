package net.hauntedstudio.Authentication.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    @Id
    private String uuid = UUID.randomUUID().toString();
    private String name;
    private String email;
    private String password;
    private String roles;
}