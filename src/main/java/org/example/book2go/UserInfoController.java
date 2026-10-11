package org.example.book2go;

import org.apache.catalina.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/user-info")
public class UserInfoController {

    @GetMapping
    public List<UserInfo> getUsers() {

        return List.of(
                new UserInfo(
                        "A001",
                        "Kalp Kalani",
                        "kkalani@example.com",
                        "2345678901",
                        "kkpassword",
                        "manager",
                        true
                ),
                new UserInfo(
                        "B001",
                        "Ebuka Samason",
                        "esalani@example.com",
                        "3456789012",
                        "espassword",
                        "team member",
                        false
                )
        );
    }

}
