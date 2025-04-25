package com.kyn.commonjwt.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO class for representing a token request.
 * Contains subject and roles.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    private String subject;
    private List<String> roles;
}
