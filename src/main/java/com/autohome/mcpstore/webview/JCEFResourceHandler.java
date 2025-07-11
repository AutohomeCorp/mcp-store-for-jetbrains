package com.autohome.mcpstore.webview;


import java.net.URL;

import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

public class JCEFResourceHandler implements CefResourceHandler {
    private ResourceHandlerState state = ClosedConnection.getInstance();

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        String processedUrl = request.getURL();
        if (processedUrl.startsWith("http://mcpstore/")) {
            String pathToResource = processedUrl.replace("http://mcpstore/", "webview-ui/dist/");
            URL newUrl = getClass().getClassLoader().getResource(pathToResource);
            try {
                state = new JCEFConnection(newUrl.openConnection());
                callback.Continue();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
        state.getResponseHeaders(response, responseLength, redirectUrl);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        return state.readResponse(dataOut, bytesToRead, bytesRead, callback);
    }

    @Override
    public void cancel() {
        state.close();
        state = ClosedConnection.getInstance();
    }
}
