package com.eshoppingzone.profileservice.security;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eshoppingzone.profileservice.dao.UserProfileRepository;
import com.eshoppingzone.profileservice.entity.UserProfile;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
    private UserProfileRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserProfile> optionalUser = userRepo.findByEmail(email);
        return optionalUser.map(MyUserDetails::new).orElse(null);
        
    }
}
