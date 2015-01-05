WeatherAlarm
============

Sends alert messages when current conditions meet specified criteria, e.g. temperature.

The implementation depends on Netflix OSS projects: karyon and ribbon, and the Yahoo Weather API.

This application is currently under development and is not finished...

For testing purposes, JVM args are used to configure the application:

```
-DweatherAlarm.locationWoeid=[yahoo location id]
-DweatherAlarm.weatherServiceQueryInterval=[seconds] 
-DweatherAlarm.userName=[user name]
-DweatherAlarm.userEmail=[your.name@yourEmail.com]
-DweatherAlarm.temperaturePredicate=[LT,LE,GT,GE,EQ,NE]
-DweatherAlarm.temperatureValue=[integer value]
```

Yahoo location ids can be looked up here: http://woeid.rosselliot.co.nz/
