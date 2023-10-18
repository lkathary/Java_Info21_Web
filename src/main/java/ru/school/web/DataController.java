package ru.school.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class DataController {

    @GetMapping("/")
    public String index() {
        return "head";
    }

    @GetMapping("/data")
    public String dataMain() {
        return "data";
    }

    @GetMapping("/operations")
    public String operationsMain() {
        return "operations";
    }

    @GetMapping("/sqlQuery")
    public String queryMain() {
        return "sqlQuery";
    }

    @GetMapping("/about")
    public String aboutMain() {
        return "about";
    }

}
