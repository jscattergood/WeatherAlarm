package weatherAlarm;

import netflix.karyon.Karyon;
import netflix.karyon.KaryonBootstrapSuite;
import netflix.karyon.servo.KaryonServoModule;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;
import weatherAlarm.endpoints.HealthCheck;
import weatherAlarm.handlers.HttpRequestHandler;
import weatherAlarm.modules.FilterModule;
import weatherAlarm.modules.NotificationModule;
import weatherAlarm.modules.WeatherQueryModule;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/27/2014
 */
public class Bootstrap {

    public static void main(String[] args) {
        HealthCheck healthCheckHandler = new HealthCheck();
        WeatherQueryModule weatherQueryModule = new WeatherQueryModule();
        FilterModule filterModule = new FilterModule();
        NotificationModule notificationModule = new NotificationModule();

        configureProcessChain(weatherQueryModule, filterModule, notificationModule);

        Karyon.forRequestHandler(8888,
                new HttpRequestHandler("/health",
                        new HealthCheckEndpoint(healthCheckHandler)),
                new KaryonBootstrapSuite(healthCheckHandler),
                weatherQueryModule.asSuite(),
                filterModule.asSuite(),
                notificationModule.asSuite(),
                KaryonServoModule.asSuite())
                .startAndWaitTillShutdown();
    }

    private static void configureProcessChain(WeatherQueryModule weatherQueryModule,
                                              FilterModule filterModule,
                                              NotificationModule notificationModule) {
        weatherQueryModule.observe()
                .flatMap(filterModule.filterEvent())
                .flatMap(notificationModule.sendNotification())
                .doOnNext(System.out::println)
                .subscribe();
    }
}
