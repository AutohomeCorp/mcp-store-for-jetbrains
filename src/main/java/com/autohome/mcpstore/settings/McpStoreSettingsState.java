package com.autohome.mcpstore.settings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "McpStoreSettings",
        storages = @Storage("McpStoreSettings.xml")
)
public class McpStoreSettingsState implements PersistentStateComponent<McpStoreSettingsState> {
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private String apiHost;

    public static McpStoreSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(McpStoreSettingsState.class);
    }

    // Add listener support
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Nullable
    @Override
    public McpStoreSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull McpStoreSettingsState state) {
        String oldValue = this.apiHost;
        XmlSerializerUtil.copyBean(state, this);
        propertyChangeSupport.firePropertyChange("mcpstore.apiHost", oldValue, state.apiHost);
    }

    public void reload() {
        loadState(this);
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        String oldApiHost = this.apiHost;
        this.apiHost = apiHost;
        // Notify listeners
        propertyChangeSupport.firePropertyChange("mcpstore.apiHost", oldApiHost, apiHost);
    }
}
