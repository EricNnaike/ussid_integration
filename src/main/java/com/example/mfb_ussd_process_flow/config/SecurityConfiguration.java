package com.example.mfb_ussd_process_flow.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final String[] WHITE_LIST_URL = {
            "/api/v1/user/login",
            "/api/v1/logout",
            "/api/v1/user/activate",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(WHITE_LIST_URL)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                        (request, response, authentication) ->
                                SecurityContextHolder.clearContext());

        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        });

        return http.build();
    }

//    @Bean
//    public SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> securityConfigurerAdapter() {
//        return new SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
//            @Override
//            public void configure(HttpSecurity http) throws Exception {
//                http
//                        .csrf(csrf -> csrf.disable())
//                        .authorizeRequests(authorizeRequests ->
//                                authorizeRequests
//                                        .requestMatchers(WHITE_LIST_URL).permitAll()
//                                        .anyRequest().authenticated()
//                        )
//                        .sessionManagement(sessionManagement ->
//                                sessionManagement
//                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                        )
//                        .authenticationProvider(authenticationProvider)
//                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                        .exceptionHandling(exceptionHandling ->
//                                exceptionHandling
//                                        .authenticationEntryPoint((request, response, authException) -> {
//                                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                                        })
//                        );
//            }
//        };
//    }




}
