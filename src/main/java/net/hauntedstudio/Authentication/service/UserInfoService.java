package net.hauntedstudio.Authentication.service;

import net.hauntedstudio.Authentication.entity.UserInfo;
import net.hauntedstudio.Authentication.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = repository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new UserInfoDetails(userInfo);
    }

    public UserDetails loadUserByUuid(String uuid) throws UsernameNotFoundException {
        UserInfo userInfo = repository.findById(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + uuid));
        return new UserInfoDetails(userInfo);
    }

    public int addUser(UserInfo userInfo) {
        //validate if user already exists
        Optional<UserInfo> user = repository.findByName(userInfo.getName());
        if (user.isPresent()) {
            System.out.println("User already exists");
            return 409;
        }

        //validate if password is empty or null or less than 8 characters
        if (userInfo.getPassword() == null || userInfo.getPassword().isEmpty() || userInfo.getPassword().length() < 8) {
            System.out.println("Password is empty or null or less than 8 characters");
            return 400;
        }

        //validate if username is empty or null
        if (userInfo.getName() == null || userInfo.getName().isEmpty()) {
            System.out.println("Username is empty or null");
            return 400;
        }

        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return 200;
    }

    public UserInfo getUserByUuid(String uuid) {
        return repository.findById(uuid).orElse(null);
    }

    public UserInfo getUserByName(String name) {
        return repository.findByName(name).orElse(null);
    }
}