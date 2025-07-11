package com.autohome.mcpstore.actions;

import com.autohome.mcpstore.toolwindow.McpStoreSideWindow;
import com.autohome.mcpstore.utils.Constant;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class RefreshPage extends AnAction {

    private final McpStoreSideWindow mcpStoreSideWindow;

    public RefreshPage(McpStoreSideWindow mcpStoreSideWindow) {
        super(() -> "Refresh Page", AllIcons.Actions.Refresh);
        this.mcpStoreSideWindow = mcpStoreSideWindow;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        mcpStoreSideWindow.jbCefBrowser().getCefBrowser().loadURL(Constant.BROWSER_URL);
        ;
    }
}
