import endpoints.HealthCheck;
import handlers.HttpRequestHandler;
import netflix.karyon.Karyon;
import netflix.karyon.KaryonBootstrapSuite;
import netflix.karyon.servo.KaryonServoModule;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/27/2014
 */
public class Bootstrap {
    public static void main(String[] args) {
        HealthCheck healthCheckHandler = new HealthCheck();
        Karyon.forRequestHandler(8888,
                new HttpRequestHandler("/hcheck",
                        new HealthCheckEndpoint(healthCheckHandler)),
                new KaryonBootstrapSuite(healthCheckHandler),
                //ShutdownModule.asSuite(),
                KaryonServoModule.asSuite())
                .startAndWaitTillShutdown();
    }

}
