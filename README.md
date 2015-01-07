WeatherAlarm
============

Sends alert messages when current conditions meet specified criteria, e.g. temperature.

The implementation depends on Netflix OSS projects: karyon and ribbon, and the Yahoo Weather API.

This application is currently under development and is not finished...

For testing purposes, JVM args are used to configure the application:

```
-DweatherAlarm.locationWoeid=[yahoo location id]
-DweatherAlarm.weatherServiceQueryInterval=[seconds] 
-DweatherAlarm.userName=[user's name]
-DweatherAlarm.userEmail=[user's email address, e.g. user.name@userEmail.com]
-DweatherAlarm.temperaturePredicate=[LT,LE,GT,GE,EQ,NE]
-DweatherAlarm.temperatureValue=[integer value]
-DweatherAlarm.emailHostName=[smtp server, e.g. smtp.gmail.com]
-DweatherAlarm.emailAuthUser=[service's email account, e.g. service.email@serviceEmail.com]
-DweatherAlarm.emailAuthPass=[service email account password]
```

Yahoo location ids can be looked up here: http://woeid.rosselliot.co.nz/
