package net.hauntedstudio.Authentication.controller.user;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import net.hauntedstudio.Authentication.entity.UserInfo;
import net.hauntedstudio.Authentication.response.AuthResponse;
import net.hauntedstudio.Authentication.response.ErrorResponse;
import net.hauntedstudio.Authentication.service.JwtService;
import net.hauntedstudio.Authentication.service.SessionService;
import net.hauntedstudio.Authentication.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class RegisterController {

    private final UserInfoService service;

    @Autowired
    public RegisterController(@Qualifier("userInfoService") UserInfoService userInfoService) {
        this.service = userInfoService;
    }
    @Autowired
    private JwtService jwtService;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity<?> addNewUser(@RequestBody UserInfo userInfo, ServletRequest request) {
        userInfo.setRoles("ROLE_STUDENT");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        int status = service.addUser(userInfo);

        if (status == 200) {
            String ipAddress = httpRequest.getRemoteAddr();
            String userAgent = httpRequest.getHeader("User-Agent");
            return ResponseEntity.ok(new AuthResponse(status, sessionService.createSession(userInfo.getUuid(), ipAddress, userAgent).getToken()));
        } else if (status == 409) {
            return ResponseEntity.status(409).body(new ErrorResponse(status, "User already exists"));
        } else if (status == 400) {
            return ResponseEntity.status(400).body(new ErrorResponse(status, "Invalid username or password"));
        } else {
            return ResponseEntity.status(500).body(new ErrorResponse(status, "Internal server error"));
        }
    }

}