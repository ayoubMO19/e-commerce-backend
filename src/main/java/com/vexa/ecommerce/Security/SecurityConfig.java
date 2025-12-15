package com.vexa.ecommerce.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // indica que es una clase con configuraciones
@EnableMethodSecurity // esta anotación habilita los métodos de seguridad como preAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter; // import de JwtAuthenticationFilter que entiendo que tiene los filtros que hay que superar para validar la autenticación
    private final UserDetailsService userDetailsService; // import de UserDetailsService que es el service que tiene la lógica para obtener el user por Email? Es como la comunicación con la db?

    // El contructor de la clase securityConfig
    public SecurityConfig(JwtAuthenticationFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean // No sé para qué sirve esta anotación
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // Esta es la función main, donde se configura la seguridad http y se lanza con build
        http
                .csrf(csrf -> csrf.disable()) // Esto deshabilita csrf, que es?
                .cors(cors -> {}) // Esto hace algo con cors pero no se el qué exactamente.
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll() // autoriza requests a todos los endpoints que hay en api/auth/

                        // Endpoints que requieren autenticación
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").authenticated() // se configura autenticación necesaria para el endpont get /api/users/{id} para obtener user
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").authenticated() // se configura autenticación necesaria para el endpont put /api/users/{id} para crear user
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").authenticated() // se configura autenticación necesaria para el endpont delete /api/users/{id} para borrar user

                        // GET /api/users solo para administradores (agregarás roles después)
                        .requestMatchers(HttpMethod.GET, "/api/users").authenticated() // esta es igual que los 3 enpoints de users de arriba, falta indicar que el rol debe ser admin, como se haría? Con preAuthorize?

                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated() // Esto indica que el resto de endpoints no indicados especificamente arriba, requieren autenticación hasta que se indique lo contrario.
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Ésto no sé qué hace, algo de sesión?
                .authenticationProvider(authenticationProvider()) // Esto no se que hace, que provider?
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Aqui entiendo que se están asignando los filtros, los cuales están definidos en la clase JwtAuthenticationFilter

        return http.build(); // retorna el build del http con toda la configuracion comentada previamente.
    }

    @Bean // no se que es esta anotación
    public AuthenticationProvider authenticationProvider() { // Según mi intuición esta función se encarga una nueva autenticación para un user específico como una sesión para x user.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // crea un nuevo authProvider, que es como una autenticación nueva segun entiendo.
        authProvider.setUserDetailsService(userDetailsService);// le asigna a la nueva autenticación un user.
        authProvider.setPasswordEncoder(passwordEncoder()); // aqui entiendo que le asigna una password encoded, pero de donde saca la pasword?
        return authProvider; // retorna la autenticación configurada y lista
    }

    @Bean // no se que es esta anotación
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { // esta función no se está usando no? no se que hace.
        return config.getAuthenticationManager();
    }

    @Bean // no se que es esta anotación
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } // Entiendo que esta función se encarga de encriptar una password usando BCrypt, pero no entiendo, donde se le pasa la password para que la encripte??
}