package com.horizon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//跨域过滤器
/**
 * 使用CORS，用于解决ajax跨域访问问题
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        //1.添加Cors配置信息
        CorsConfiguration configuration = new CorsConfiguration();
        //1.允许的域，不要写*，否则cookie就无法使用了
        configuration.addAllowedOrigin("http://manage.leyou.com");
        //2.是否发送cookie信息
        configuration.setAllowCredentials(true);
        //3.允许的请求方式
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("HEAD");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        //4.允许的头信息
        configuration.addAllowedHeader("*");

        //5.有效时长
        configuration.setMaxAge(3600L);

        //6.添加映射路径，这里拦截一切请求
        UrlBasedCorsConfigurationSource configurationSource
                = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configuration);

        //7.返回新的CorsFilter
        return new CorsFilter(configurationSource);
    }
}
