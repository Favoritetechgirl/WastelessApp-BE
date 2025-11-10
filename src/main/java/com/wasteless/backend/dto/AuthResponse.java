package com.wasteless.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
      private String token; //We'll return JWT token later
      private Long userId;
      private String email;
      private String fullName;

      public AuthResponse (String token) {
          this.token = token;
      }

}
