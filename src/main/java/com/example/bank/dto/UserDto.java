package com.example.bank.dto;

import com.example.bank.entities.Users;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String status;
    public UserDto(Users user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = "***"; //user.getPassword();
        this.email = user.getEmail();
        this.status = user.getStatus();
    }
}
