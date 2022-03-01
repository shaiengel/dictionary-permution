package com.shai.app;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationReader {

    Configuration config;

    public ConfigurationReader(){
        int offset;
        try
        {
            config = new PropertiesConfiguration("./config/application.properties");
        }
        catch (ConfigurationException cex)
        {

        }
    }

    public int getAsciiOffset() {
        return config.getInt("dictionary.ascii.offset");
    }

    public int getAlphaBeitSize() {
        return config.getInt("dictionary.alphbeit.size");
    }


}