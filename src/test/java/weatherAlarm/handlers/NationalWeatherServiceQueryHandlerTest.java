/*
 * Copyright 2019 John Scattergood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package weatherAlarm.handlers;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * This class is responsible for testing {@link YahooWeatherQueryHandler}
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/10/2015
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(NationalWeatherServiceQueryHandler.class)
public class NationalWeatherServiceQueryHandlerTest extends AbstractWeatherQueryHandlerTest {
    @Override
    protected Class<? extends AbstractWeatherQueryHandler> getHandlerClass() {
        return NationalWeatherServiceQueryHandler.class;
    }

    @Override
    protected String getMockJsonResult() {
        return "{\n" +
                "    \"@context\": [\n" +
                "        \"https://raw.githubusercontent.com/geojson/geojson-ld/master/contexts/geojson-base.jsonld\",\n" +
                "        {\n" +
                "            \"wx\": \"https://api.weather.gov/ontology#\",\n" +
                "            \"s\": \"https://schema.org/\",\n" +
                "            \"geo\": \"http://www.opengis.net/ont/geosparql#\",\n" +
                "            \"unit\": \"http://codes.wmo.int/common/unit/\",\n" +
                "            \"@vocab\": \"https://api.weather.gov/ontology#\",\n" +
                "            \"geometry\": {\n" +
                "                \"@id\": \"s:GeoCoordinates\",\n" +
                "                \"@type\": \"geo:wktLiteral\"\n" +
                "            },\n" +
                "            \"city\": \"s:addressLocality\",\n" +
                "            \"state\": \"s:addressRegion\",\n" +
                "            \"distance\": {\n" +
                "                \"@id\": \"s:Distance\",\n" +
                "                \"@type\": \"s:QuantitativeValue\"\n" +
                "            },\n" +
                "            \"bearing\": {\n" +
                "                \"@type\": \"s:QuantitativeValue\"\n" +
                "            },\n" +
                "            \"value\": {\n" +
                "                \"@id\": \"s:value\"\n" +
                "            },\n" +
                "            \"unitCode\": {\n" +
                "                \"@id\": \"s:unitCode\",\n" +
                "                \"@type\": \"@id\"\n" +
                "            },\n" +
                "            \"forecastOffice\": {\n" +
                "                \"@type\": \"@id\"\n" +
                "            },\n" +
                "            \"forecastGridData\": {\n" +
                "                \"@type\": \"@id\"\n" +
                "            },\n" +
                "            \"publicZone\": {\n" +
                "                \"@type\": \"@id\"\n" +
                "            },\n" +
                "            \"county\": {\n" +
                "                \"@type\": \"@id\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"type\": \"FeatureCollection\",\n" +
                "    \"features\": [\n" +
                "        {\n" +
                "            \"id\": \"https://api.weather.gov/stations/KMYZ/observations/2019-06-16T17:35:00+00:00\",\n" +
                "            \"type\": \"Feature\",\n" +
                "            \"geometry\": {\n" +
                "                \"type\": \"Point\",\n" +
                "                \"coordinates\": [\n" +
                "                    -96.629999999999995,\n" +
                "                    39.25\n" +
                "                ]\n" +
                "            },\n" +
                "            \"properties\": {\n" +
                "                \"@id\": \"https://api.weather.gov/stations/KMYZ/observations/2019-06-16T17:35:00+00:00\",\n" +
                "                \"@type\": \"wx:ObservationStation\",\n" +
                "                \"elevation\": {\n" +
                "                    \"value\": 319,\n" +
                "                    \"unitCode\": \"unit:m\"\n" +
                "                },\n" +
                "                \"station\": \"https://api.weather.gov/stations/KMYZ\",\n" +
                "                \"timestamp\": \"2019-06-16T17:35:00+00:00\",\n" +
                "                \"rawMessage\": \"KMYZ 161735Z AUTO 03005KT 10SM CLR 27/16 A2990 RMK AO2\",\n" +
                "                \"textDescription\": \"Clear\",\n" +
                "                \"icon\": \"https://api.weather.gov/icons/land/day/skc?size=medium\",\n" +
                "                \"presentWeather\": [],\n" +
                "                \"temperature\": {\n" +
                "                    \"value\": 27,\n" +
                "                    \"unitCode\": \"unit:degC\",\n" +
                "                    \"qualityControl\": \"qc:V\"\n" +
                "                },\n" +
                "                \"dewpoint\": {\n" +
                "                    \"value\": 16,\n" +
                "                    \"unitCode\": \"unit:degC\",\n" +
                "                    \"qualityControl\": \"qc:V\"\n" +
                "                },\n" +
                "                \"windDirection\": {\n" +
                "                    \"value\": 29.999999999999996,\n" +
                "                    \"unitCode\": \"unit:degree_(angle)\",\n" +
                "                    \"qualityControl\": \"qc:V\"\n" +
                "                },\n" +
                "                \"windSpeed\": {\n" +
                "                    \"value\": 2.6000000000000001,\n" +
                "                    \"unitCode\": \"unit:m_s-1\",\n" +
                "                    \"qualityControl\": \"qc:V\"\n" +
                "                },\n" +
                "                \"windGust\": {\n" +
                "                    \"value\": null,\n" +
                "                    \"unitCode\": \"unit:m_s-1\",\n" +
                "                    \"qualityControl\": \"qc:Z\"\n" +
                "                },\n" +
                "                \"barometricPressure\": {\n" +
                "                    \"value\": 101250,\n" +
                "                    \"unitCode\": \"unit:Pa\",\n" +
                "                    \"qualityControl\": \"qc:V\"\n" +
                "                },\n" +
                "                \"seaLevelPressure\": {\n" +
                "                    \"value\": null,\n" +
                "                    \"unitCode\": \"unit:Pa\",\n" +
                "                    \"qualityControl\": \"qc:Z\"\n" +
                "                },\n" +
                "                \"visibility\": {\n" +
                "                    \"value\": 16090,\n" +
                "                    \"unitCode\": \"unit:m\",\n" +
                "                    \"qualityControl\": \"qc:C\"\n" +
                "                },\n" +
                "                \"maxTemperatureLast24Hours\": {\n" +
                "                    \"value\": null,\n" +
                "                    \"unitCode\": \"unit:degC\",\n" +
                "                    \"qualityControl\": null\n" +
                "                },\n" +
                "                \"minTemperatureLast24Hours\": {\n" +
                "                    \"value\": null,\n" +
                "                    \"unitCode\": \"unit:degC\",\n" +
                "                    \"qualityControl\": null\n" +
                "                },\n" +
                "                \"precipitationLastHour\": {\n" +
                "                    \"value\": null,\n" +
                "                    \"unitCode\": \"unit:m\",\n" +
                "                    \"qualityControl\": \"qc:Z\"\n" +
                "                },\n" +
                "                \"precipitationLast3Hours\": {\n" +
                "                    \"value\": null,\n" +
                "                    \"unitCode\": \"unit:m\",\n" +
                "                    \"qualityControl\": \"qc:Z\"\n" +
                "                },\n" +
                "                \"precipitationLast6Hours\": {\n" +
                "                    \"value\": null,\n" +
                "                    \"unitCode\": \"unit:m\",\n" +
                "                    \"qualityControl\": \"qc:Z\"\n" +
                "                },\n" +
                "                \"relativeHumidity\": {\n" +
                "                    \"value\": 50.986771578907181,\n" +
                "                    \"unitCode\": \"unit:percent\",\n" +
                "                    \"qualityControl\": \"qc:C\"\n" +
                "                },\n" +
                "                \"windChill\": {\n" +
                "                    \"value\": null,\n" +
                "                    \"unitCode\": \"unit:degC\",\n" +
                "                    \"qualityControl\": \"qc:V\"\n" +
                "                },\n" +
                "                \"heatIndex\": {\n" +
                "                    \"value\": 27.477667847471821,\n" +
                "                    \"unitCode\": \"unit:degC\",\n" +
                "                    \"qualityControl\": \"qc:V\"\n" +
                "                },\n" +
                "                \"cloudLayers\": [\n" +
                "                    {\n" +
                "                        \"base\": {\n" +
                "                            \"value\": null,\n" +
                "                            \"unitCode\": \"unit:m\"\n" +
                "                        },\n" +
                "                        \"amount\": \"CLR\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }
}
