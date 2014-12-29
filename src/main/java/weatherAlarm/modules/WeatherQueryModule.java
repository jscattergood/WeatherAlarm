package weatherAlarm.modules;

import com.google.inject.AbstractModule;
import com.netflix.governator.guice.LifecycleInjectorBuilder;
import com.netflix.governator.guice.LifecycleInjectorBuilderSuite;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/28/2014
 */
public class WeatherQueryModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(WeatherQueryModule.class);

    private String locationWOEID;
    private String weatherApiKey;

    @Override
    protected void configure() {
        weatherApiKey = System.getProperty("weatherApiKey");
        if (weatherApiKey == null) {
            logger.error("No weather API key defined");
        }

        locationWOEID = System.getProperty("woeid");
        if (locationWOEID == null) {
            logger.error("No location WOEID defined");
        }

        Observable.interval(1, TimeUnit.MINUTES, Schedulers.newThread()).forEach(new Action1<Long>() {
            @Override
            public void call(Long inLong) {
                callWeatherService();
            }
        });
    }

    private void callWeatherService() {
        HttpResourceGroup group = Ribbon.createHttpResourceGroup("weatherServiceClient",
                ClientOptions.create()
                        .withMaxAutoRetries(3)
                        .withConfigurationBasedServerList("query.yahooapis.com"));

        HttpRequestTemplate<ByteBuf> weatherQueryTemplate = group.newTemplateBuilder("weatherByWoeid")
                .withMethod("GET")
                .withUriTemplate("/v1/public/yql?q={yql}&format={format}&env={env}")
                .build();

        RibbonRequest<ByteBuf> request = weatherQueryTemplate.requestBuilder()
                .withRequestProperty("yql", "select item.condition from weather.forecast where woeid = "+ locationWOEID)
                .withRequestProperty("format", "json")
                .withRequestProperty("env", "store://datatables.org/alltableswithkeys")
                .build();

        if (logger.isDebugEnabled()) {
            logger.debug("Request built: " + request.toString());
        }

        Observable<ByteBuf> result = request.observe();
        result.subscribe(new Action1<ByteBuf>() {
            @Override
            public void call(ByteBuf byteBuf) {
                logger.debug("Received response:" + byteBuf.toString(Charset.defaultCharset()));
            }
        });
    }

    public static LifecycleInjectorBuilderSuite asSuite() {
        return new LifecycleInjectorBuilderSuite() {
            @Override
            public void configure(LifecycleInjectorBuilder builder) {
                builder.withAdditionalModules(new WeatherQueryModule());
            }
        };
    }
}
