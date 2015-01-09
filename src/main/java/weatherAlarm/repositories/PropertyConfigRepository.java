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

package weatherAlarm.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 1/8/2015
 */
public class PropertyConfigRepository implements ConfigRepository {
    private final static Logger logger = LoggerFactory.getLogger(PropertyConfigRepository.class);
    private final Properties properties = new Properties();

    public PropertyConfigRepository() {
        File file = new File("config.properties");
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                // load a properties file
                properties.load(in);
            } catch (IOException e) {
                logger.error("Could not load properties", e);
            }
        }
        //Override properties with JVM args
        properties.putAll(System.getProperties());
    }

    @Override
    public String getConfigValue(String config) {
        return (String) properties.get(config);
    }

    @Override
    public void setConfigValue(String config, String value) {
        properties.put(config, value);
    }
}
