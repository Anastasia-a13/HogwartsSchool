package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.InfoService;

@RestController
public class InfoController {
    @Value("${server.port}")
    private String serverPort;
    private final InfoService infoService;

    @Autowired
    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/port")
    public String getPort() {
        return serverPort;
    }

    @GetMapping("/sum-optimized")
    public long getOptimizedSum() {
        return infoService.getOptimizedSum();
    }
}
