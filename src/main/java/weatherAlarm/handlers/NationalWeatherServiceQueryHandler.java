package weatherAlarm.handlers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.WeatherConditionEvent;
import weatherAlarm.model.WeatherConditions;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;

import java.io.IOException;

/**
 * This class is responsible for querying the www.weather.gov for the current conditions
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 3/28/2015
 */
@Singleton
public class NationalWeatherServiceQueryHandler extends AbstractWeatherQueryHandler {
    private static final Logger logger = LoggerFactory.getLogger(NationalWeatherServiceQueryHandler.class);

    @Inject
    public NationalWeatherServiceQueryHandler(IEventStream stream,
                                              IConfigService configService,
                                              IWeatherAlarmService weatherAlarmService) {
        super(stream, configService, weatherAlarmService);
        finishInit();
    }

    @Override
    protected HttpRequestTemplate<ByteBuf> getRequestTemplate() {
        if (cachedRequestTemplate == null) {
            HttpResourceGroup group = getResourceGroup();
            cachedRequestTemplate = group.newTemplateBuilder("getWeatherByLocation")
                    .withMethod("GET")
                    .withUriTemplate("/zones/forecast/{location}/observations?limit=1")
                    .withHeader("Accept", "application/json")
                    .withHeader("User-Agent", "WeatherAlarmService")
                    .build();
        }
        return cachedRequestTemplate;
    }

    @Override
    protected HttpResourceGroup getResourceGroup() {
        if (cachedResourceGroup == null) {
            IClientConfig config = IClientConfig.Builder.newBuilder()
                    .withSecure(true)
                    .build();
            config.set(CommonClientConfigKey.SecurePort, 443);
            config.set(CommonClientConfigKey.IsClientAuthRequired, false);
            config.set(CommonClientConfigKey.KeyStore, "dummy-keystore.jks");
            config.set(CommonClientConfigKey.KeyStorePassword, "password");

            cachedResourceGroup = Ribbon.createHttpResourceGroup("nationalWeatherService",
                    ClientOptions.from(config)
                            .withMaxAutoRetries(3)
                            .withConfigurationBasedServerList("https://api.weather.gov:443"));
        }
        return cachedResourceGroup;
    }

    @Override
    protected WeatherConditionEvent createWeatherConditionEvent(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonString);
            WeatherConditions conditions = new WeatherConditions();
            Double temperatureC = root
                    .get("features").get(0)
                    .get("properties")
                    .get("temperature")
                    .get("value").asDouble();
            if (temperatureC <= 0) {
                logger.error("Invalid temperature value in event:\n" + jsonString);
                throw new RuntimeException("Invalid temperature value: " + temperatureC);
            }
            Integer temperatureF = (int) Math.round(convertToFahrenheit(temperatureC));
            conditions.setTemperature(temperatureF);
            return new WeatherConditionEvent(conditions);
        } catch (IOException e) {
            logger.error("Could not create WeatherConditionEvent from JSON string", e);
            throw new RuntimeException(e);
        }
    }

    private Double convertToFahrenheit(Double celsius) {
        return 32 + (celsius * 9 / 5);
    }
}
