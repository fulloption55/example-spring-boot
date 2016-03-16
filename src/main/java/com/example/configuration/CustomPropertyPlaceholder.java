package com.example.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;


public class CustomPropertyPlaceholder extends PropertyPlaceholderConfigurer {
    private final Logger logger = LogManager.getLogger(CustomPropertyPlaceholder.class);

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        super.processProperties(beanFactory, props);

        logger.info("=========================================================================");
        logger.info("====================== Application Configuration ========================");
        logger.info("=========================================================================");
        for (Object key : props.keySet()) {
            String keyName = key.toString();
            String value = props.getProperty(keyName);
            logger.info(String.format("Config : %s = %s", keyName, value));
        }
        logger.info("=========================================================================");
        logger.info("=========================================================================");
        logger.info("=========================================================================");
    }
}
