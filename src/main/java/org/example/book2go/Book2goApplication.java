package org.example.book2go;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Book2goApplication {

    public static void main(String[] args) {

        SpringApplication.run(Book2goApplication.class, args);
    }

    @GetMapping
    public String helloWorld(){
        return "Hello Book 2 Go!";
    }
}

