/*
 * Copyright 2015 John Scattergood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package weatherAlarm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is a {@link java.util.Properties} based implementation of the
 * {@link weatherAlarm.services.IConfigService}. It supports property overrides via JVM arguments.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/8/2015
 */
public class PropertyConfigService implements IConfigService {
    public static final String CONFIG_PROPERTIES = "config.properties";
    private final static Logger logger = LoggerFactory.getLogger(PropertyConfigService.class);
    private final Properties properties = new Properties();

    public PropertyConfigService() {
        // load a properties file
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(CONFIG_PROPERTIES)) {
            properties.load(inputStream);

        } catch (IOException e) {
            logger.error("Could not load properties", e);
        }

        //Override properties with JVM args
        properties.putAll(System.getProperties());
    }

    @Override
    public String getConfigValue(String config) {
        return (String) properties.get(config);
    }
}
