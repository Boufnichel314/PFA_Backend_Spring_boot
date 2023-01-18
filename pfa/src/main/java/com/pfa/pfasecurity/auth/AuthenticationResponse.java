package com.pfa.pfasecurity.auth;

import com.pfa.pfasecurity.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
  private String token;
  private String username;
  private Role role;
  private String message;
}