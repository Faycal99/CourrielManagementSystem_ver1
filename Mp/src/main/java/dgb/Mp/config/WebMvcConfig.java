package dgb.Mp.config;

import dgb.Mp.Couriel.CourielTypeConverter;
import dgb.Mp.Couriel.NatureConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private PrivilegeInterceptor privilegeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(privilegeInterceptor);
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter((Converter<?, ?>) new CourielTypeConverter());
        registry.addConverter((Converter<?, ?>) new NatureConverter());
    }
}
