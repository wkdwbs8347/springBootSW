package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // .cors().and() 대신 람다 DSL 사용
            .cors(Customizer.withDefaults()) // WebMvcConfigurer에서 정의한 CORS 설정 사용

            // .csrf().disable() 대신 람다 DSL 사용
            // 개발용, 필요하면 나중에 활성화
            .csrf(AbstractHttpConfigurer::disable) 
            
            .authorizeHttpRequests(auth -> auth
                // 로그아웃 요청은 인증 없이 접근 가능하도록 설정
                .requestMatchers("/api/**").permitAll()
                // 나머지 요청에 대한 보안 설정 (필요시 추가)
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                // 프론트엔드에서 사용하는 로그아웃 엔드포인트 설정
                .logoutUrl("/user/logout") 
                // 로그아웃 요청은 반드시 POST 메서드여야 한다고 명시적으로 강제
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout", "POST"))
                // 로그아웃 성공 시 리디렉션할 경로 (예: 로그인 페이지)
                .logoutSuccessUrl("/") 
                // 세션 무효화
                .invalidateHttpSession(true) 
                // 쿠키 삭제 (JSESSIONID 등)
                .deleteCookies("JSESSIONID") 
            );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // React 개발 서버 주소
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}