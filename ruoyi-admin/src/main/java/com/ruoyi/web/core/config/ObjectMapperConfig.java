package com.ruoyi.web.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Configuration
public class ObjectMapperConfig {

    @Bean
    @Primary
    public ObjectMapper initObjectMapper(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(df);

                return objectMapper;
    }

    @Bean
    @Primary
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverterConfiguration() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper om = initObjectMapper();
        converter.setObjectMapper(om);
        return converter;
    }

}
