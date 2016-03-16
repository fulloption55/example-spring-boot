package com.example.configuration;


import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConfigurationFileLoader {

    //This class will used when you need to read each per files or read external files.
    @Bean
    public PropertyPlaceholderConfigurer properties() {
        final PropertyPlaceholderConfigurer ppc = new CustomPropertyPlaceholder();
        ppc.setIgnoreUnresolvablePlaceholders(false);
        ppc.setIgnoreResourceNotFound(true);

        final List<Resource> resourceLst = new ArrayList<>();

        resourceLst.add(new ClassPathResource("application.properties"));
        resourceLst.add(new FileSystemResource("/data/projects/example-spring-boot/config/global.properties"));

        ppc.setLocations(resourceLst.toArray(new Resource[]{}));

        return ppc;
    }


}