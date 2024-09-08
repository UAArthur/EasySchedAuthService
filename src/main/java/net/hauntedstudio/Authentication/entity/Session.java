package net.hauntedstudio.Authentication.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    @Id
    private String sessionId = UUID.randomUUID().toString();
    private String userId;
    private String token;
    private Date expiry;
    private Date createdAt;
    private String ipAddress;
    private String userAgent;
    private String country;

}