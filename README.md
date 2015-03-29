WeatherAlarm
============

Sends alert messages when current conditions meet specified criteria, e.g. temperature.

The implementation depends on Netflix OSS projects: karyon and ribbon, and either the Yahoo Weather API or
the Weather Underground API.

Configuration is defined via config.properties file found in src/main/conf.

JVM args can also be used for configuration or to override values defined in the config.properties file:

```
-DweatherAlarm.weatherServiceQueryInterval=[seconds]
-DweatherAlarm.weatherServiceApiKey=[api key]
-DweatherAlarm.initialAlarms=[a json file containing initial alarm entries in a list, e.g. /path/to/json/init.json]
-DweatherAlarm.emailHostName=[smtp server, e.g. smtp.gmail.com]
-DweatherAlarm.emailAuthUser=[service's email account, e.g. service.email@serviceEmail.com]
-DweatherAlarm.emailAuthPass=[service email account password]
```
