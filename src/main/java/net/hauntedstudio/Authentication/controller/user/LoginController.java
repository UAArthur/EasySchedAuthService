package net.hauntedstudio.Authentication.controller.user;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import net.hauntedstudio.Authentication.entity.AuthRequest;
import net.hauntedstudio.Authentication.entity.UserInfo;
import net.hauntedstudio.Authentication.response.AuthResponse;
import net.hauntedstudio.Authentication.response.ErrorResponse;
import net.hauntedstudio.Authentication.service.JwtService;
import net.hauntedstudio.Authentication.service.SessionService;
import net.hauntedstudio.Authentication.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UserInfoService service;

    @Autowired
    public LoginController(@Qualifier("userInfoService") UserInfoService userInfoService) {
        this.service = userInfoService;
    }

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, ServletRequest request) {
        try {
            UserInfo userInfo = service.getUserByName(authRequest.getUsername());
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (userInfo == null) {
                return ResponseEntity.status(404).body(new ErrorResponse(404, "User not found"));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                String ipAddress = httpRequest.getRemoteAddr();
                String userAgent = httpRequest.getHeader("User-Agent");
                return ResponseEntity.ok(new AuthResponse(200, sessionService.createSession(userInfo.getUuid(), ipAddress, userAgent).getToken()));
            } else {
                return ResponseEntity.status(401).body(new ErrorResponse(401, "Invalid username or password"));
            }
        } catch (UsernameNotFoundException e) {
            logger.error("User not found: {}", authRequest.getUsername(), e);
            return ResponseEntity.status(404).body(new ErrorResponse(404, "User not found"));
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal server error"));
        }
    }

    //TODO: Add code to handle QRCode login
}