package com.mgr.api.config;

import com.mgr.api.component.LogInterceptor;
import com.mgr.api.constant.MgrConstant;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] exclusive = {"/v1/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**"};
        registry.addInterceptor(logInterceptor).addPathPatterns("/**").excludePathPatterns(exclusive);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Cấu hình format cho java.util.Date (Kiểu cũ)
        builder.dateFormat(new SimpleDateFormat(MgrConstant.DATE_TIME_FORMAT));

        // Tạo formatters cho Java 8 Date/Time (LocalDateTime, LocalDate)
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(MgrConstant.DATE_TIME_FORMAT);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(MgrConstant.DATE_FORMAT);

        // 1. Cấu hình Serializers (Chiều đi: Java Object -> JSON String)
        builder.serializers(new LocalDateSerializer(dateFormatter));
        builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));

        // 2. Cấu hình Deserializers (Chiều về: JSON String -> Java Object) - GIÚP FEIGN HẾT LỖI
        builder.deserializers(new LocalDateDeserializer(dateFormatter));
        builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));

        builder.indentOutput(true);
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
        converters.add(new ResourceHttpMessageConverter());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateFormatter dateFormatter = new DateFormatter(MgrConstant.DATE_TIME_FORMAT);
        dateFormatter.setLenient(true);
        registry.addFormatter(dateFormatter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public ObjectMapper objectMapper() {
        // Sử dụng builder để tạo ObjectMapper đồng bộ với cấu hình phía trên
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modules(new JavaTimeModule()); // Quan trọng để xử lý Java 8 Date
        builder.dateFormat(new SimpleDateFormat(MgrConstant.DATE_TIME_FORMAT));
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(MgrConstant.DATE_TIME_FORMAT);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(MgrConstant.DATE_FORMAT);

        builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
        builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));
        builder.serializers(new LocalDateSerializer(dateFormatter));
        builder.deserializers(new LocalDateDeserializer(dateFormatter));

        ObjectMapper objectMapper = builder.build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}