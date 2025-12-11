package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	// 리액트와 연결하기 위한 CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")	// 모든 URL 경로 허용
                .allowedOrigins("http://localhost:5173") // 이 도메인에서 들어오는 요청만 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 리액트가 벡엔드로 보낼 수 있는 메서드
                .allowCredentials(true); // 쿠키, 세션, Authorization 헤더 등을 포함한 요청을 허용
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = System.getProperty("user.dir") + "/uploads/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(0);
    }
}