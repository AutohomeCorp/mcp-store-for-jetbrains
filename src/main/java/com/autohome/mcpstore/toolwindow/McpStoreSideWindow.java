package com.autohome.mcpstore.toolwindow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;

import com.autohome.mcpstore.listeners.McpStoreSettingChangeListener;
import com.autohome.mcpstore.listeners.PluginStateChangeListener;
import com.autohome.mcpstore.models.HandlerRequest;
import com.autohome.mcpstore.models.McpServerInstallConfig;
import com.autohome.mcpstore.models.McpStoreConfig;
import com.autohome.mcpstore.models.QueryServerListParam;
import com.autohome.mcpstore.models.Result;
import com.autohome.mcpstore.services.McpClientManagerService;
import com.autohome.mcpstore.services.McpStoreService;
import com.autohome.mcpstore.services.NotificationService;
import com.autohome.mcpstore.settings.McpStoreSettingsState;
import com.autohome.mcpstore.utils.Constant;
import com.autohome.mcpstore.utils.JacksonHelper;
import com.autohome.mcpstore.utils.SystemUtil;
import com.autohome.mcpstore.webview.McpStoreSchemeHandlerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import org.apache.commons.lang3.StringUtils;
import org.cef.CefApp;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLifeSpanHandlerAdapter;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;

public class McpStoreSideWindow {

    private JBCefBrowser jbCefBrowser;
    private boolean webLoaded;
    private McpClientManagerService mcpClientManager;
    private NotificationService notificationService;
    private McpStoreService mcpStoreService;
    private Project project;

    public McpStoreSideWindow(Project project) {
        super();

        this.project = project;
        this.webLoaded = false;
        this.mcpClientManager = project.getService(McpClientManagerService.class);
        this.notificationService = project.getService(NotificationService.class);
        this.mcpStoreService = project.getService(McpStoreService.class);
    }

    @FunctionalInterface
    private interface Handler {
        Object handle(String param) throws Exception;
    }

    private Map<String, Handler> getMessageHandlers() {
        Map<String, Handler> handlers = new HashMap<>();
        handlers.put("showInformationMessage", (String param) -> {
            showInformationMessage(param);
            return null;
        });
        handlers.put("getLoginUserName", (String param) -> getLoginUserName());
        handlers.put("getMcpClientList", (String param) -> getMcpClientList());
        handlers.put("getInstalledMcpServerList", (String param) -> getInstalledMcpServerList());
        handlers.put("installMcpServer", (String param) -> installMcpServer(param));
        handlers.put("uninstallMcpServer", (String param) -> uninstallMcpServer(param));
        handlers.put("openSettings", (String param) -> {
            openSettings();
            return null;
        });
        handlers.put("getSubmitMcpAddr", (String param) -> getSubmitMcpAddr());
        handlers.put("getIssueAddr", (String param) -> getIssueAddr());
        handlers.put("checkConfigIsValid", (String param) -> checkConfigIsValid());
        handlers.put("fetchMarketplace", (String param) -> fetchMarketplace(param));
        handlers.put("getSourceType", (String param) -> getSourceType());
        handlers.put("getUITheme", (String param) -> getUITheme());
        return handlers;
    }

    private Object getSourceType() {
        return mcpStoreService.getServerSources();
    }

    private Object getUITheme() {
        LafManager lafManager = LafManager.getInstance();
        //return lafManager.getCurrentUIThemeLookAndFeel().getName();
        return lafManager.getCurrentLookAndFeel().getName();
    }

    private Object fetchMarketplace(String param) {
        try {
            HandlerRequest<QueryServerListParam> handlerRequest = JacksonHelper.deSerialize(param, new TypeReference<HandlerRequest<QueryServerListParam>>() {
            });
            return mcpStoreService.getServerList(handlerRequest.getValue());
        } catch (Exception e) {
            notificationService.error("installMcpServer error", e.getMessage());
            return new Result(-1, e.getMessage(), null);
        }
    }

    public List<String> getMcpClientList() {
        try {
            return mcpClientManager.getInstalledMcpClients();
        } catch (Exception e) {
            notificationService.error("getMcpClientList error", e.getMessage());
            return null;
        }
    }

    public Map<String, List<String>> getInstalledMcpServerList() {
        try {
            return mcpClientManager.getInstalledMcpServerList();
        } catch (Exception e) {
            notificationService.error("getInstalledMcpServerList error", e.getMessage());
            return null;
        }
    }

    public Result installMcpServer(String param) {
        try {
            HandlerRequest<McpServerInstallConfig> handlerRequest = JacksonHelper.deSerialize(param, new TypeReference<HandlerRequest<McpServerInstallConfig>>() {
            });
            return mcpClientManager.installMcpServer(handlerRequest.getValue());
        } catch (Exception e) {
            notificationService.error("installMcpServer error", e.getMessage());
            return new Result(-1, e.getMessage(), null);
        }
    }

    public Result uninstallMcpServer(String param) {
        try {
            HandlerRequest<McpServerInstallConfig> handlerRequest = JacksonHelper.deSerialize(param, new TypeReference<HandlerRequest<McpServerInstallConfig>>() {
            });
            return mcpClientManager.uninstallMcpServer(handlerRequest.getValue());
        } catch (Exception e) {
            notificationService.error("uninstallMcpServer error", e.getMessage());
            return new Result(-1, e.getMessage(), null);
        }
    }

    public void openSettings() {
        try {
            ApplicationManager.getApplication().invokeLater(() -> {
                ShowSettingsUtilImpl.getInstance().showSettingsDialog(null, "MCP Store Settings");
            });
        } catch (Exception e) {
            notificationService.error("openSettings error", e.getMessage());
        }
    }

    public String getSubmitMcpAddr() {
        try {
            return getMcpStoreConfig().getSubmitServerUrl();
        } catch (Exception e) {
            notificationService.error("getSubmitMcpAddr error", e.getMessage());
            return null;
        }
    }

    public String getIssueAddr() {
        try {
            return getMcpStoreConfig().getIssueUrl();
        } catch (Exception e) {
            notificationService.error("getIssueAddr error", e.getMessage());
            return null;
        }
    }

    public Boolean checkConfigIsValid() {
        McpStoreSettingsState config = McpStoreSettingsState.getInstance();
        return config != null && StringUtils.isNotEmpty(config.getApiHost());
    }

    public McpStoreConfig getMcpStoreConfig() {
        return mcpStoreService.getMcpStoreConfig();
    }

    public void refreshApiHost(String apiHost) {
        if (apiHost != null) {
            apiHost = apiHost.trim();
        }
        mcpStoreService.setMcpStoreApi(apiHost);
        if (this.jbCefBrowser != null) {
            jbCefBrowser.getCefBrowser().executeJavaScript(
                    "window.postMessage({command: 'apiHostChange', value: '" + apiHost + "'}, '*');",
                    null,
                    0
            );
        }
    }

    public void refreshMcpClientList(List<String> mcpClientList) {
        if (this.jbCefBrowser != null) {
            jbCefBrowser.getCefBrowser().executeJavaScript(
                    "window.postMessage({command: 'mcpClientListChange', value: " +
                            JacksonHelper.serialize(mcpClientList) + "}, '*');",
                    null,
                    0
            );
        }
    }

    private void showInformationMessage(String param) {
        HandlerRequest<String> handlerRequest = JacksonHelper.deSerialize(param, new TypeReference<HandlerRequest<String>>() {
        });
        Notification notification = new Notification(Constant.GROUP, "Information", handlerRequest.getValue(), NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }


    private String getLoginUserName() {
        return SystemUtil.getLoginUserName();
    }

    public synchronized JBCefBrowser jbCefBrowser() {
        return !this.webLoaded ? lazyLoad() : this.jbCefBrowser;
    }

    private JBCefBrowser lazyLoad() {
        try {
            if (!this.webLoaded) {
                boolean isOffScreenRendering = false;

                JBCefBrowser browser;
                try {
                    Map<String, String> jcefSettings = new HashMap<>();
                    jcefSettings.put("remote-debugging-port", "9222");
                    browser = JBCefBrowser.createBuilder()
                            .setOffScreenRendering(isOffScreenRendering)
                            .build();
                    browser.getJBCefClient().setProperty("browser.subprocess.disable.devtools", "0");
                    browser.getJBCefClient().setProperty("contextMenu.devTools.enabled", "1");
                    browser.getJBCefClient().setProperty("contextMenu.devTools.available", "1");
                } catch (Exception e) {
                    notificationService.warn("JBCefBrowser# build Browser not supported", e.getMessage());
                    browser = new JBCefBrowser();
                }

                registerLifeSpanHandler(browser);
                registerJsCallJavaHandler(browser);
                registerListener(browser);
                browser.loadURL(Constant.BROWSER_URL);
                //browser.loadURL("http://mcpstore.corpautohome.com/servers");
                //browser.loadURL("http://mcpstore/index.html");
                this.jbCefBrowser = browser;
                this.webLoaded = true;
            }
        } catch (Exception e) {
            notificationService.warn("JBCefBrowser lazyLoad error", e.getMessage());
        }
        return this.jbCefBrowser;
    }

    private void registerJsCallJavaHandler(JBCefBrowser browser) {
        JBCefJSQuery query = JBCefJSQuery.create((JBCefBrowserBase) browser);
        Map<String, Handler> messageHandlers = getMessageHandlers();
        query.addHandler((String arg) -> {
            HashMap<String, Object> response = new HashMap<>();
            try {
                HandlerRequest handlerRequest = JacksonHelper.deSerialize(arg, HandlerRequest.class);
                String command = handlerRequest.getCommand();
                if (messageHandlers.containsKey(command)) {
                    Object data = messageHandlers.get(command).handle(arg);
                    response.put("data", data);
                } else {
                    response.put("error", "Unknown command: " + command);
                }
            } catch (Exception e) {
                notificationService.warn("JBCefJSQuery error", e.getMessage());
                response.put("error", "JBCefJSQuery error:" + e.getMessage());
            }
            return new JBCefJSQuery.Response(JacksonHelper.serialize(response));
        });
        browser.getJBCefClient().addLoadHandler(new CefLoadHandler() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {

            }

            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                browser.executeJavaScript(
                        "window.callJava = function(arg) { " +
                                "   return new Promise((resolve, reject) => { " +
                                "       " + query.inject(
                                "arg",
                                "response => resolve(response)",
                                "(error_code, error_message) => reject(error_message)"
                        ) +
                                "   });" +
                                "};",
                        null, 0);
            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                notificationService.debug("JBCefBrowser", "Injected JavaScript callJava handler");
            }

            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                notificationService.info("JBCefBrowser", String.format("JBCefBrowser# onLoadError, failedUrl:{},errorCodeL:{} errorText:{}", failedUrl, errorCode, errorText));
            }

        }, browser.getCefBrowser());
        //输出控制台日志
        /*browser.getJBCefClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                System.out.println("Console [" + level + "]: " + message + " at " + source + ":" + line);
                return false;
            }
        }, browser.getCefBrowser());*/
    }

    private void registerLifeSpanHandler(JBCefBrowser browser) {
        final CefLifeSpanHandlerAdapter lifeSpanHandlerAdapter = new CefLifeSpanHandlerAdapter() {
            @Override
            public void onAfterCreated(CefBrowser browse) {
                CefApp.getInstance().registerSchemeHandlerFactory("http", "mcpstore", new McpStoreSchemeHandlerFactory());
            }

            @Override
            public boolean onBeforePopup(CefBrowser browser, CefFrame frame,
                                         String targetUrl, String targetFrameName) {
                // 方式1：在当前窗口打开链接
                //browser.loadURL(targetUrl);

                // Method 2: Open in IDE built-in browser
                BrowserUtil.browse(targetUrl);

                // Prevent creating new popup windows
                return true;
            }
        };
        browser.getJBCefClient().addLifeSpanHandler(lifeSpanHandlerAdapter, browser.getCefBrowser());
        final JBCefBrowser tempBrowser = browser;
        Disposer.register(ApplicationManager.getApplication(), () -> tempBrowser.getJBCefClient().removeLifeSpanHandler(lifeSpanHandlerAdapter, tempBrowser.getCefBrowser()));
    }

    public void registerListener(JBCefBrowser browser) {
        // 注册监听器,监听主题变化
        ApplicationManager.getApplication().getMessageBus().connect()
                .subscribe(LafManagerListener.TOPIC, new LafManagerListener() {
                    @Override
                    public void lookAndFeelChanged(LafManager source) {
                        // Theme change handling logic
                        LafManager lafManager = LafManager.getInstance();
                        //String theme = lafManager.getCurrentUIThemeLookAndFeel().getName();
                        String theme = lafManager.getCurrentLookAndFeel().getName();
                        browser.getCefBrowser().executeJavaScript(
                                "window.postMessage({command: 'uIThemeChange', value: '" + theme + "'}, '*');",
                                null,
                                0
                        );
                    }
                });

        McpStoreSettingChangeListener listener = new McpStoreSettingChangeListener(this.project);
        McpStoreSettingsState.getInstance().addPropertyChangeListener(listener);

        ApplicationManager.getApplication().getMessageBus().connect()
                .subscribe(DynamicPluginListener.TOPIC, new PluginStateChangeListener(this.project));
    }

    public JComponent getComponent() {
        if (Objects.nonNull(jbCefBrowser())) {
            return jbCefBrowser().getComponent();
        }
        return null;
    }


}
