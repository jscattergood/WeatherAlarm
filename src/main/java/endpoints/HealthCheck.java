package endpoints;

import netflix.karyon.health.HealthCheckHandler;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/28/2014
 */
public class HealthCheck implements HealthCheckHandler {
    @Override
    public int getStatus() {
        return 0;
    }
}
