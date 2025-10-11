package com.lucas.ctrlSave.controller;

import com.lucas.ctrlSave.service.SaveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    private SaveService saveService;

    @GetMapping("/perfil")
    public String perfil(Model model) {
        return "perfil";
    }

    @GetMapping("/home")
    public String home(Model model) {

        model.addAttribute("saveFiles",saveService.getAllSaveFiles());

        return "home";
    }
}
