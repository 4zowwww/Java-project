package com.unipi.studentapp.config;

import com.unipi.studentapp.model.Users;
import com.unipi.studentapp.service.AuthService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{

    private final AuthService authService;

    public WebConfig(AuthService authService)
    {
        this.authService = authService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        // Καμια σελιδα δεν αποθηκευεται στην cache του browser (μπαινει πρωτο, ωστε να ισχυει και στα redirect)
        registry.addInterceptor(new NoCacheInterceptor()).addPathPatterns("/**");

        // Οι σελιδες καθε κατηγοριας χρηστη επιτρεπονται μονο σε αυτη την κατηγορια
        registry.addInterceptor(new RoleInterceptor(authService, Users.ROLE_SECRETARY)).addPathPatterns("/secretary/**");
        registry.addInterceptor(new RoleInterceptor(authService, Users.ROLE_PROFESSOR)).addPathPatterns("/professor/**");
        registry.addInterceptor(new RoleInterceptor(authService, Users.ROLE_STUDENT)).addPathPatterns("/student/**");
    }
}
