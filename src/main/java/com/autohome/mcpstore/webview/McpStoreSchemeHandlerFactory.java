package com.autohome.mcpstore.webview;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

public class McpStoreSchemeHandlerFactory implements CefSchemeHandlerFactory {

    public McpStoreSchemeHandlerFactory() {
    }

    @Override
    public CefResourceHandler create(CefBrowser browser, CefFrame frame, String schemeName, CefRequest request) {
        return new JCEFResourceHandler();
    }
}
