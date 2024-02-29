package kz.sab1tm.domainnames.controller;

import kz.sab1tm.domainnames.model.enumeration.DomainStatus;
import kz.sab1tm.domainnames.service.DomainService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    private final DomainService domainService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("domains", domainService.getAll());
        return "index";
    }
}
