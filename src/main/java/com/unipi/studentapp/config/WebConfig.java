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
        // Καμία σελίδα δεν αποθηκεύεται στην cache του browser (μπαίνει πρώτο, ώστε να ισχύει και στα redirect)
        registry.addInterceptor(new NoCacheInterceptor()).addPathPatterns("/**");

        // Οι σελίδες κάθε κατηγορίας χρήστη επιτρέπονται μόνο σε αυτή την κατηγορία
        registry.addInterceptor(new RoleInterceptor(authService, Users.ROLE_SECRETARY)).addPathPatterns("/secretary/**");
        registry.addInterceptor(new RoleInterceptor(authService, Users.ROLE_PROFESSOR)).addPathPatterns("/professor/**");
        registry.addInterceptor(new RoleInterceptor(authService, Users.ROLE_STUDENT)).addPathPatterns("/student/**");
    }
}
