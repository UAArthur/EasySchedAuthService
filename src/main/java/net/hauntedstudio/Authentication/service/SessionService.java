package net.hauntedstudio.Authentication.service;

import net.hauntedstudio.Authentication.entity.Session;
import net.hauntedstudio.Authentication.repository.SessionRepository;
import net.hauntedstudio.Authentication.utils.UserAgentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoService userDetailsService;

    public Session createSession(String userId, String ipAddress, String userAgent) {
        UserDetails userDetails = userDetailsService.loadUserByUuid(userId);
        String token = jwtService.generateToken(userId, userDetails);

        Session session = new Session();
        session.setUserId(userId);
        session.setToken(token);
        session.setExpiry(jwtService.extractExpiration(token));
        session.setCreatedAt(new Date());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setCountry(UserAgentUtils.getCountryByIPAdress(ipAddress));

        return sessionRepository.save(session);
    }
}