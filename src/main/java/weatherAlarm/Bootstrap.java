package weatherAlarm;

import netflix.karyon.Karyon;
import netflix.karyon.KaryonBootstrapSuite;
import netflix.karyon.ShutdownModule;
import netflix.karyon.servo.KaryonServoModule;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;
import weatherAlarm.endpoints.HealthCheck;
import weatherAlarm.handlers.HttpRequestHandler;
import weatherAlarm.modules.WeatherQueryModule;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/27/2014
 */
public class Bootstrap {

    public static void main(String[] args) {
        HealthCheck healthCheckHandler = new HealthCheck();
        Karyon.forRequestHandler(8888,
                new HttpRequestHandler("/health",
                        new HealthCheckEndpoint(healthCheckHandler)),
                new KaryonBootstrapSuite(healthCheckHandler),
                WeatherQueryModule.asSuite(),
                ShutdownModule.asSuite(),
                KaryonServoModule.asSuite())
                .startAndWaitTillShutdown();
    }

}
