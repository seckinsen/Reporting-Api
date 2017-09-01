package net.seckinsen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by seck on 01.09.2017.
 */

@Controller
@ApiIgnore
public class SwaggerController {

    @GetMapping("/")
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }

}
