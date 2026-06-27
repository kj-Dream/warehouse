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

@Configuration  // 告诉 Spring Boot：这是一个配置类，里面包含Bean的定义或者配置逻辑，启动时要加载它
public class WebConfig implements WebMvcConfigurer {//WebMvcConfigurer 是 Spring MVC 提供的扩展接口，里面有各种可以覆盖的默认方法

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//指定哪些接口路径需要应用这个跨域规则。/** 是通配符，表示项目中的所有接口都允许跨域访问。你也可以细化，比如只让 /api/** 下的接口跨域。
                .allowedOriginPatterns("*")//这是关键中的关键。它指定哪些前端来源被允许.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")//OPTIONS 特别值得一提：浏览器在发送跨域的 POST/PUT/DELETE 请求前，会先发一个"预检请求"，方法就是 OPTIONS。它是用来问服务器："我能发这个请求吗？"。所以 OPTIONS 必须放行，否则复杂请求根本发不出去。
                .allowedHeaders("*")//允许前端请求携带哪些请求头。* 表示不限制。如果前端要传自定义头（比如 X-Auth-Token），这里就必须包含它。
                .allowCredentials(true)//是否允许携带凭证信息，主要包括： Cookie / HTTP 认证信息（Authorization 头）/ 客户端 SSL 证书
                .maxAge(3600);//预检请求的缓存时间，单位是秒。3600s=1h
    }
}
