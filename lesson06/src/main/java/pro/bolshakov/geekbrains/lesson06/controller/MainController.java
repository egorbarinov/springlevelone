package pro.bolshakov.geekbrains.lesson06.controller;

import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Service
@Path("/")
public class MainController {
    @GET
    @Produces("text/plain")
    public String helloWorld() {
        return "Hello, it is my Spring Boot App";
    }
}