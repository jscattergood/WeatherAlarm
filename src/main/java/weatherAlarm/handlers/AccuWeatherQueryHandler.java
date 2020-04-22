package weatherAlarm.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.WeatherConditionEvent;
import weatherAlarm.model.WeatherConditions;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;

import java.io.IOException;

/**
 * This class is responsible for querying the AccuWeather service for the current conditions
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 3/28/2015
 */
@Singleton
public class AccuWeatherQueryHandler extends AbstractWeatherQueryHandler {
    private static final Logger logger = LoggerFactory.getLogger(AccuWeatherQueryHandler.class);
    private final String apiKey;

    @Inject
    public AccuWeatherQueryHandler(IEventStream stream, IConfigService configService, IWeatherAlarmService weatherAlarmService) {
        super(stream, configService, weatherAlarmService);
        this.apiKey = configService.getConfigValue(IConfigService.CONFIG_WEATHER_SERVICE_API_KEY);
        if (this.apiKey == null) {
            logger.error("No api key defined. Cannot query weather service...");
            return;
        }
        finishInit();
    }

    @Override
    protected HttpRequestTemplate<ByteBuf> getRequestTemplate() {
        if (cachedRequestTemplate == null) {
            HttpResourceGroup group = getResourceGroup();
            cachedRequestTemplate = group.newTemplateBuilder("getWeatherByLocation")
                    .withMethod("GET")
                    .withUriTemplate("/currentconditions/v1/{location}?apikey="+ this.apiKey)
                    .withHeader("Accept", "application/json")
                    .build();
        }
        return cachedRequestTemplate;
    }

    @Override
    protected HttpResourceGroup getResourceGroup() {
        if (cachedResourceGroup == null) {
            cachedResourceGroup = Ribbon.createHttpResourceGroup("accuWeatherService",
                    ClientOptions.create()
                            .withMaxAutoRetries(3)
                            .withConfigurationBasedServerList("dataservice.accuweather.com"));
        }
        return cachedResourceGroup;
    }

    @Override
    protected WeatherConditionEvent createWeatherConditionEvent(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonString);
            WeatherConditions conditions = new WeatherConditions();
            conditions.setTemperature(root
                    .get(0)
                    .get("Temperature")
                    .get("Imperial")
                    .get("Value").asInt()
            );
            return new WeatherConditionEvent(conditions);
        } catch (IOException e) {
            logger.error("Could not create WeatherConditionEvent from JSON string", e);
            throw new RuntimeException(e);
        }
    }
}
