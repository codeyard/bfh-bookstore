package org.bookstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/**").authorizeRequests()
            .antMatchers(HttpMethod.GET, "/customers/**").hasAnyRole(Constants.CUSTOMER, Constants.EMPLOYEE)
            .antMatchers(HttpMethod.PUT, "/customers/**").hasAnyRole(Constants.CUSTOMER, Constants.EMPLOYEE)
            .antMatchers(HttpMethod.POST, "/customers/**").authenticated()

            .antMatchers(HttpMethod.GET, "/orders/**").hasAnyRole(Constants.CUSTOMER, Constants.EMPLOYEE)
            .antMatchers(HttpMethod.PUT, "/orders/**").hasAnyRole(Constants.CUSTOMER, Constants.EMPLOYEE)
            .antMatchers(HttpMethod.POST, "/orders/**").hasRole(Constants.CUSTOMER)

            .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)
            .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());

        http.headers().frameOptions().sameOrigin();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
