package it.paleocapa.mastroiannim;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DockerHomeController {

    @GetMapping("/")
    public String home() {
        return "Bot avviato";
    }
}
