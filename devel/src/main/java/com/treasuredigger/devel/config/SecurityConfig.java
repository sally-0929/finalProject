package com.treasuredigger.devel.config;

import com.treasuredigger.devel.service.CustomOAuth2UserService;
import com.treasuredigger.devel.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorizeHttpRequestsCustomizer -> authorizeHttpRequestsCustomizer
                        .requestMatchers("/loadItems", "/css/**", "/js/**", "/img/**", "/api/categories").permitAll()
                        .requestMatchers("/", "/members/**", "/item/**", "item/view/**", "/images/**", "/biditem/list", "biditem/view/**").permitAll()
                        .requestMatchers("/customer-service/inquiries", "/customer-service","/customer-service/faqs","/customer-service/notices" ).permitAll()
                        .requestMatchers("/customer-service/inquiries/register/**").authenticated()
                        .requestMatchers("/customer-service/inquiries/edit/**").authenticated()
                        .requestMatchers("/customer-service/inquiries/delete/**").hasAnyRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/members/myPage").authenticated()
                        .requestMatchers("/members/memberUpdate").authenticated()
                        .requestMatchers("/members/checkPassword").authenticated()
                        .requestMatchers("/members/passwordCheck").authenticated()
                        .requestMatchers("/email/sendAuth").permitAll()
                        .requestMatchers("/sms/send").permitAll()
                        .requestMatchers("/payments/paymentP").permitAll()
                        .requestMatchers("/payments/refund").permitAll()
                        .requestMatchers("/payments/validation/**").permitAll()
                        .requestMatchers("/payments/**").permitAll()
                        .anyRequest()
                        .authenticated()
                ).formLogin(formLoginCustomizer -> formLoginCustomizer
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("mid")
                        .failureUrl("/members/login/error")
                        .failureHandler(new CustomAuthenticationFailureHandler())
                ).logout(logoutCustomizer -> logoutCustomizer
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessUrl("/")
                ).oauth2Login(oauth2Login ->
                        oauth2Login.userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService)))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
