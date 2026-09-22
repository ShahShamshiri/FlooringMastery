package com.sg.flooring;

import com.sg.flooring.controller.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    public static void main(String[] args) {
        //Start spring
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //Get the controller from Spring
        Controller controller = context.getBean("controller", Controller.class);

        controller.run();
    }
}