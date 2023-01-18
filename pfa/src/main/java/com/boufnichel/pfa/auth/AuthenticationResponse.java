package com.boufnichel.pfa.auth;

import com.boufnichel.pfa.user.Role;

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