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

package weatherAlarm.endpoints;

import com.google.inject.Inject;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.services.IWeatherAlarmService;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * This class handles requests for weather alarm resources
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/12/2015
 */
public class WeatherAlarmEndpoint implements RequestHandler<ByteBuf, ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(WeatherAlarmEndpoint.class);
    @Inject
    private IWeatherAlarmService alarmService;

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        if (alarmService == null) {
            response.setStatus(HttpResponseStatus.SERVICE_UNAVAILABLE);
            return response.close();
        }
        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            handleGet(response, request.getUri());
        } else if (HttpMethod.PUT.equals(request.getHttpMethod())) {
            handlePut(response, request.getContent());
        } else if (HttpMethod.DELETE.equals(request.getHttpMethod())) {
            handleDelete(response, request.getUri());
        } else {
            response.setStatus(HttpResponseStatus.NOT_IMPLEMENTED);
        }
        return response.close();
    }

    private void handleGet(HttpServerResponse<ByteBuf> response, String uri) {
        String[] parts = uri.substring(1).split("/");
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (parts.length == 1) {
                response.writeBytes(mapper.writeValueAsBytes(alarmService.getAlarms()));
            } else if (parts.length == 2) {
                String alarmName = URLDecoder.decode(parts[1], "UTF-8");
                WeatherAlarm alarm = alarmService.getAlarm(alarmName);
                if (alarm != null) {
                    response.writeBytes(mapper.writeValueAsBytes(alarm));
                } else {
                    logger.debug("No alarm found with name " + alarmName);
                    response.setStatus(HttpResponseStatus.NOT_FOUND);
                }
            } else {
                logger.error("Unsupported resource request " + uri);
                response.setStatus(HttpResponseStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            logger.error("Failed to write JSON to response", e);
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void handlePut(HttpServerResponse<ByteBuf> response, Observable<ByteBuf> content) {
        ObjectMapper mapper = new ObjectMapper();
        content.forEach(byteBuf -> {
            try {
                WeatherAlarm alarm = mapper.readValue(byteBuf.toString(Charset.defaultCharset()), WeatherAlarm.class);
                alarmService.addAlarm(alarm);
            } catch (IOException e) {
                logger.error("Failed to read JSON from request", e);
                response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    private void handleDelete(HttpServerResponse<ByteBuf> response, String uri) {
        String[] parts = uri.substring(1).split("/");
        try {
            if (parts.length == 1) {
                //Not allowed to delete all alarms
                response.setStatus(HttpResponseStatus.UNAUTHORIZED);
            } else if (parts.length == 2) {
                String alarmName = URLDecoder.decode(parts[1], "UTF-8");
                boolean removed = alarmService.removeAlarm(alarmName);
                if (!removed) {
                    logger.debug("No alarm found with name " + alarmName);
                    response.setStatus(HttpResponseStatus.NOT_FOUND);
                }
            } else {
                logger.error("Unsupported resource request " + uri);
                response.setStatus(HttpResponseStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            logger.error("Failed to write JSON to response", e);
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void setAlarmService(IWeatherAlarmService alarmService) {
        this.alarmService = alarmService;
    }
}
