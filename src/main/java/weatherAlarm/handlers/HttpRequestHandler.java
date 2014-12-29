package weatherAlarm.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/28/2014
 */
public class HttpRequestHandler implements RequestHandler<ByteBuf, ByteBuf> {

    private final String uri;
    private final RequestHandler<ByteBuf, ByteBuf> uriHandler;

    public HttpRequestHandler(String inUri, RequestHandler<ByteBuf, ByteBuf> inRequestHandler) {
        this.uri = inUri;
        this.uriHandler = inRequestHandler;
    }

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> inRequest, HttpServerResponse<ByteBuf> inResponse) {
        if (inRequest.getUri().startsWith(uri)) {
            return uriHandler.handle(inRequest, inResponse);
        } else {
            inResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            return inResponse.close();
        }
    }
}
