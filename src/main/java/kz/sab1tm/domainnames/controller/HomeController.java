package kz.sab1tm.domainnames.controller;

import kz.sab1tm.domainnames.model.enumeration.DomainFilterEnum;
import kz.sab1tm.domainnames.service.DomainService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class HomeController {

    private final DomainService domainService;

    @GetMapping("/")
    public String home(@RequestParam(required = false) DomainFilterEnum filter,
                       Model model) {
        model.addAttribute("domains", domainService.getByFilter(filter));
        return "index";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String name) {
        domainService.deleteByName(name);
        return "redirect:/";
    }
}
