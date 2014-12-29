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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/28/2014
 */
public class WeatherQueryModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(WeatherQueryModule.class);
    public static final int DEFAULT_QUERY_INTERVAL = 15;

    private String locationWOEID;
    private HttpResourceGroup cachedResourceGroup;
    private HttpRequestTemplate<ByteBuf> cachedRequestTemplate;

    @Override
    protected void configure() {
        locationWOEID = System.getProperty("weatherAlarm.locationWoeid");
        if (locationWOEID == null) {
            logger.error("No location WOEID defined");
        }
        final String intervalProperty = System.getProperty("weatherAlarm.weatherServiceQueryInterval");
        long queryInterval;
        if (intervalProperty != null) {
            queryInterval = Long.valueOf(intervalProperty);
        } else {
            logger.error("No query interval defined. Using default " + DEFAULT_QUERY_INTERVAL + " minutes");
            queryInterval = DEFAULT_QUERY_INTERVAL;
        }

        Observable.interval(queryInterval, TimeUnit.MINUTES, Schedulers.newThread()).forEach(new Action1<Long>() {
            @Override
            public void call(Long inLong) {
                requestWeatherData();
            }
        });
    }

    private void requestWeatherData() {
        RibbonRequest<ByteBuf> request = buildRequest();

        Observable<ByteBuf> result = request.observe();
        result.subscribe(new Action1<ByteBuf>() {
            @Override
            public void call(ByteBuf byteBuf) {
                logger.debug("Received response:" + byteBuf.toString(Charset.defaultCharset()));
            }
        });
    }

    private RibbonRequest<ByteBuf> buildRequest() {
        HttpRequestTemplate<ByteBuf> weatherQueryTemplate = getRequestTemplate();
        return weatherQueryTemplate.requestBuilder()
                .withRequestProperty("woeid", locationWOEID)
                .build();
    }

    private HttpRequestTemplate<ByteBuf> getRequestTemplate() {
        if (cachedRequestTemplate == null) {
            HttpResourceGroup group = getResourceGroup();
            String encodedYql = StringUtils.EMPTY;
            String encodedEnv = StringUtils.EMPTY;
            try {
                encodedYql = URLEncoder.encode("select item.condition from weather.forecast where woeid = ", "UTF-8");
                encodedEnv = URLEncoder.encode("store://datatables.org/alltableswithkeys", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            cachedRequestTemplate = group.newTemplateBuilder("getWeatherByWoeid")
                    .withMethod("GET")
                    .withUriTemplate("/v1/public/yql" +
                            "?q=" + encodedYql + "{woeid}" +
                            "&format=json" +
                            "&env=" + encodedEnv)
                    .build();
        }
        return cachedRequestTemplate;
    }

    private HttpResourceGroup getResourceGroup() {
        if (cachedResourceGroup == null) {
            cachedResourceGroup = Ribbon.createHttpResourceGroup("yahooWeatherService",
                    ClientOptions.create()
                            .withMaxAutoRetries(3)
                            .withConfigurationBasedServerList("query.yahooapis.com"));
        }
        return cachedResourceGroup;
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
