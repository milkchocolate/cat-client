package org.mad.catclient;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // https://docs.spring.io/spring-security/site/docs/5.4.5/reference/html5/#appendix-faq-forbidden-csrf
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login();
    }
}