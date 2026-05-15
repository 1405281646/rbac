package com.rbac.system.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}