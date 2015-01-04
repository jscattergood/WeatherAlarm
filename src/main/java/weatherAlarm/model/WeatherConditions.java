package weatherAlarm.model;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 1/4/2015
 */
public class WeatherConditions {
    private int temperature;

    public void setTemperature(int temp) {
        temperature = temp;
    }

    public int getTemperature() {
        return temperature;
    }

    @Override
    public String toString() {
        return "WeatherConditions[temperature=" + getTemperature() + "]";
    }

}
