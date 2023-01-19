package com.pfa.pfasecurity.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pfa.pfasecurity.config.JwtService;
import com.pfa.pfasecurity.user.Role;
import com.pfa.pfasecurity.user.User;
import com.pfa.pfasecurity.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
	  if (repository.existsByEmail(request.getEmail())) {
	        return AuthenticationResponse.builder().token("").username("").role(null).message("Email already exists.").build();
	    }
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .approved(false)
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    repository.save(user);
    if (!user.isApproved()) {
        return AuthenticationResponse.builder().token("").username("").role(null).message("Your registration is pending admin approval.").build();
    }
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .username(user.getEmail())
        .role(user.getRole())
        .build();
  }
  

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    if (!user.isApproved()) {
        return AuthenticationResponse.builder().token("").username("").role(null).message("Your registration is pending admin approval.").build();
    }
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .username(user.getEmail())
        .role(user.getRole())
        .build();
  }
}