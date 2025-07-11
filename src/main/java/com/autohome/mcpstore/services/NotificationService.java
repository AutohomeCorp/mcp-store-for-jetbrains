package com.autohome.mcpstore.services;

import java.awt.*;
import javax.swing.JPanel;

import com.autohome.mcpstore.utils.Constant;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

public class NotificationService {
    private ConsoleView consoleView;

    public NotificationService(Project project) {
        this.consoleView = openConsole(project);
    }

    private ConsoleView openConsole(Project project) {

        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        ApplicationManager.getApplication().invokeLater(() -> {
            // Create main panel (as toolbar target component)
            JPanel mainPanel = new JPanel(new BorderLayout());

            // Add console view
            mainPanel.add(consoleView.getComponent(), BorderLayout.CENTER);

            // Create action group and toolbar
            DefaultActionGroup actionGroup = new DefaultActionGroup();
            ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(
                    "MCP.Store.Console.Toolbar", // Unique ID
                    actionGroup,
                    false
            );

            // Critical fix: Set target component
            actionToolbar.setTargetComponent(mainPanel);

            // Create toolbar panel
            JPanel toolbarPanel = new JPanel(new BorderLayout());
            toolbarPanel.add(actionToolbar.getComponent(), BorderLayout.WEST);

            // Add toolbar to top of main panel
            mainPanel.add(toolbarPanel, BorderLayout.NORTH);

            // Create run content descriptor
            RunContentDescriptor descriptor = new RunContentDescriptor(
                    consoleView,
                    null,
                    mainPanel,  // 使用包含工具栏的主面板
                    "MCP Store Console"
            );

            // Show in run tool window
            Executor executor = DefaultRunExecutor.getRunExecutorInstance();
            ExecutionManager.getInstance(project).getContentManager().showRunContent(executor, descriptor);
        });

        return consoleView;
    }

    public void debug(String title, String content) {
        String message = String.format("[%s] %s", title, content);
        this.consoleView.print(message + "\n", ConsoleViewContentType.LOG_DEBUG_OUTPUT);
    }

    public void info(String title, String content) {
        String message = String.format("[%s] %s", title, content);
        this.consoleView.print(message + "\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
        Notification notification = new Notification(Constant.GROUP, title, content, NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }

    public void error(String title, String content) {
        String message = String.format("[%s] %s", title, content);
        this.consoleView.print(message + "\n", ConsoleViewContentType.LOG_ERROR_OUTPUT);
        Notification notification = new Notification(Constant.GROUP, title, content, NotificationType.ERROR);
        Notifications.Bus.notify(notification);
    }

    public void warn(String title, String content) {
        String message = String.format("[%s] %s", title, content);
        this.consoleView.print(message + "\n", ConsoleViewContentType.LOG_WARNING_OUTPUT);
        Notification notification = new Notification(Constant.GROUP, title, content, NotificationType.WARNING);
        Notifications.Bus.notify(notification);
    }
}
