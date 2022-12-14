package com.eshoppingzone.profileservice.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eshoppingzone.profileservice.entity.JwtRes;
import com.eshoppingzone.profileservice.entity.LoginReq;
import com.eshoppingzone.profileservice.entity.MessageRes;
import com.eshoppingzone.profileservice.entity.SignupReq;
import com.eshoppingzone.profileservice.entity.UserProfile;
import com.eshoppingzone.profileservice.security.MyUserDetails;
import com.eshoppingzone.profileservice.security.jwt.JwtProvider;
import com.eshoppingzone.profileservice.service.UserProfileService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class ProfileController {
	
	@Autowired
    private AuthenticationManager authenticationManager;
	@Autowired
    private PasswordEncoder encoder;
	@Autowired
    private JwtProvider jwtProvider;
	@Autowired
    private UserProfileService userProfileService;

    @PostMapping(value = "/createNewCustomer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNewCustomer(@RequestBody UserProfile userProfile) {
    	if (userProfileService.existsByEmail(userProfile.getEmail())) {
            return new ResponseEntity<>(new MessageRes("User already exists!"),
                    HttpStatus.BAD_REQUEST);
        }
        userProfile.setRole("ROLE_USER");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userProfileService.addNewUserProfile(userProfile));
    }

    @PostMapping(value = "/createNewMerchant", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNewMerchant(@RequestBody UserProfile userProfile) {
    	 if (userProfileService.existsByEmail(userProfile.getEmail())) {
             return new ResponseEntity<>(new MessageRes("User already exists!"),
                     HttpStatus.BAD_REQUEST);
         }
        userProfile.setRole("ROLES_ADMIN");
        userProfile.setPassword(encoder.encode(userProfile.getPassword()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userProfileService.addNewUserProfile(userProfile));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq loginReq) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateJwtToken(authentication);
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(
                new JwtRes(userDetails.getId(),
                        userDetails.getName(),
                        userDetails.getUsername(),
                        token,
                        userDetails.getIsAdmin()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@Valid @RequestBody SignupReq signupReq) {
        if (userProfileService.existsByEmail(signupReq.getEmail())) {
            return new ResponseEntity<>(new MessageRes("User already exists!"),
                    HttpStatus.BAD_REQUEST);
        }
        // Creating user's account
        UserProfile user = UserProfile
                .builder()
                .fullName(signupReq.getName())
                .email(signupReq.getEmail())
                .role("ROLE_USER")
                .isAdmin(false)
                .password(encoder.encode(signupReq.getPassword()))
                .build();
        userProfileService.addNewUserProfile(user);
        return login(new LoginReq(signupReq.getEmail(), signupReq.getPassword()));
    }

    @PostMapping(value = "/createNewDeliveryProfile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> addNewDeliveryProfile(@RequestBody UserProfile userProfile) {
        userProfile.setRole("ROLE_MERCHANT");
        userProfile.setPassword(encoder.encode(userProfile.getPassword()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userProfileService.addNewUserProfile(userProfile));
    }
    @GetMapping(value = "/allUserProfiles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserProfile>> getAllUserProfiles() {
        return ResponseEntity.ok(userProfileService.getAllUserProfiles());
    }


    @GetMapping(value = "/{profileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> getAllProfileById(@PathVariable("profileId") String profileId ) {
        return ResponseEntity.ok(userProfileService.getByProfileId(profileId));
    }

    @GetMapping(value = "/mobile/{mobilePhone}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> getAllByMobilePhone(@RequestParam (value = "mobilePhone") long mobilePhone ) {
        return ResponseEntity.ok(userProfileService.getByMobileNumber(mobilePhone));
    }

    // @RequestParam is a Spring annotation used to bind a web request parameter to a method parameter. 
    @GetMapping(value = "/name/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> getByName(@RequestParam (value = "userName") String userName ) {
        return ResponseEntity.ok(userProfileService.getByUserName(userName));
    }

    @PutMapping(value = "/updateProfile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProduct(@RequestBody UserProfile userProfile) {
        userProfileService.updateUserProfile(userProfile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@RequestParam(value = "profileId") int profileId) {
        userProfileService.deleteUserProfile(profileId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
