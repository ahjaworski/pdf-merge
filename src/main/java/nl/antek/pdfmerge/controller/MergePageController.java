package nl.antek.pdfmerge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MergePageController {

    @GetMapping("/")
    public String showMergePage() {
        return "merge";
    }
}
