package com.autohome.mcpstore.settings;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ui.FormBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class McpStoreSettingsConfigurablePanel implements Configurable, Disposable {
    private JTextField apiUrlField;
    private JLabel errorLabel;
    private McpStoreSettingsState mcpStoreSettingsState;


    public McpStoreSettingsConfigurablePanel() {
        this.mcpStoreSettingsState = McpStoreSettingsState.getInstance().getState();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "MCP Store Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        apiUrlField = new JTextField(mcpStoreSettingsState.getApiHost());
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);

        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent("MCP Store API HOST:", apiUrlField, 1, false)
                .addComponent(errorLabel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        return panel;
    }

    @Override
    public boolean isModified() {
        return !apiUrlField.getText().equals(mcpStoreSettingsState.getApiHost());
    }

    @Override
    public void apply() throws ConfigurationException {
        String url = apiUrlField.getText();
        if (!StringUtils.isEmpty(url) && !url.startsWith("http://") && !url.startsWith("https://")) {
            errorLabel.setText("API URL must start with http:// or https://");
            errorLabel.setVisible(true);
            throw new ConfigurationException("API URL must start with http:// or https://");
        }
        errorLabel.setVisible(false);
        mcpStoreSettingsState.setApiHost(url);
    }

    @Override
    public void reset() {
        apiUrlField.setText(mcpStoreSettingsState.getApiHost());
    }

    @Override
    public void dispose() {

    }

}
