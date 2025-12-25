package com.vexa.ecommerce.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    // Constructor
    public SecurityConfig(JwtAuthenticationFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll() // autoriza requests a todos los endpoints que hay en api/auth/
                        .requestMatchers("/api/payments/webhook").permitAll() // autoriza requests a webhook de payment
                        .requestMatchers(HttpMethod.GET, "/api/products").permitAll() // Permite obtener productos sin autenticación
                        .requestMatchers(HttpMethod.GET, "/api/products/{id}").permitAll() // Permite obtener producto por ID sin autenticación
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll() // Permite el acceso público a Swagger

                        // Endpoints que requieren autenticación
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").authenticated() // se configura autenticación necesaria para el endpoint get /api/users/{id} para obtener user
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").authenticated() // se configura autenticación necesaria para el endpoint put /api/users/{id} para crear user
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").authenticated() // se configura autenticación necesaria para el endpoint delete /api/users/{id} para borrar user
                        .requestMatchers(HttpMethod.GET, "/api/users").authenticated() // se configura autenticación necesaria para el endpoint get /api/users/ para obtener users

                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Aquí se están asignando los filtros, los cuales están definidos en la clase JwtAuthenticationFilter

        return http.build(); // retorna el build del http con toda la configuración comentada previamente.
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}