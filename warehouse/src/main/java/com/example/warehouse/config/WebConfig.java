/*
 * ============================================================
 * 【WebConfig】跨域配置
 * ============================================================
 * 什么是跨域？
 *   假设后端跑在 localhost:8080，前端跑在 localhost:5173。
 *   浏览器的安全策略规定：不同端口之间不能互相访问数据。
 *   这就是"跨域问题"。
 *
 * 怎么解决？
 *   后端告诉浏览器："允许来自其他端口的请求"。
 *   这就是 CORS（跨域资源共享）。
 *
 * 具体配置：
 *   .addMapping("/**")         → 允许所有路径
 *   .allowedOriginPatterns("*") → 允许所有来源
 *   .allowedMethods(...)       → 允许 GET、POST 等请求方式
 *   .allowCredentials(true)    → 允许携带 Cookie
 *
 * 如果不配这个，前端调后端接口会报：
 * "Access-Control-Allow-Origin" 错误。
 * ============================================================
 */
package com.example.warehouse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // 告诉 Spring Boot：这是一个配置类
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
