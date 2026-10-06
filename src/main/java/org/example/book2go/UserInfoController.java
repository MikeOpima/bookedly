package org.example.book2go;

import org.apache.catalina.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/user-info")
public class UserInfoController {

    public List<User> getUsers() {

        return List.of();
    }

}
