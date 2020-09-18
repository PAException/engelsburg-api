package io.github.paexception.engelsburginfrastructure.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.paexception.engelsburginfrastructure.EngelsburgInfrastructureApplication;
import io.github.paexception.engelsburginfrastructure.spring.interceptor.HashInterceptor;
import io.github.paexception.engelsburginfrastructure.spring.interceptor.ServiceTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Autowired private ObjectMapper mapper;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("localhost:8080")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE")
                .allowCredentials(false).maxAge(3600);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(x -> x instanceof MappingJackson2HttpMessageConverter)
                .forEach(x -> ((MappingJackson2HttpMessageConverter) x).setObjectMapper(this.mapper));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ServiceTokenInterceptor());
    }

}
