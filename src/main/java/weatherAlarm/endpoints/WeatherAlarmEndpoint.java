/*
 * Copyright 2015 John Scattergood
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
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import weatherAlarm.services.IWeatherAlarmService;

import java.io.IOException;

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
        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            handleGet(response);
        }
        return response.close();
    }

    private void handleGet(HttpServerResponse<ByteBuf> response) {
        if (alarmService != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                response.writeBytes(mapper.writeValueAsBytes(alarmService.getAlarms()));
            } catch (IOException e) {
                logger.error("Failed to write JSON to response", e);
                response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public void setAlarmService(IWeatherAlarmService alarmService) {
        this.alarmService = alarmService;
    }
}
