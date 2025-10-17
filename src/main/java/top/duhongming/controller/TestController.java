package top.duhongming.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duhongming.utils.BeanUtils;

@RestController
public class TestController {
    @GetMapping("/")
    public void sendMessage() {
        BeanUtils.printAllBeans();

    }
}
