package com.autohome.mcpstore.mcpclient.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.autohome.mcpstore.enums.CodeEnum;
import com.autohome.mcpstore.mcpclient.IMcpClient;
import com.autohome.mcpstore.models.McpServerConfig;
import com.autohome.mcpstore.models.Result;
import com.autohome.mcpstore.services.NotificationService;
import com.autohome.mcpstore.utils.JacksonHelper;
import com.autohome.mcpstore.utils.JetbrainsUtil;
import com.autohome.mcpstore.utils.SystemUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;

public class LingmaMcpClient implements IMcpClient {

    private static final String name = "lingma";
    private static final String pluginId = "com.alibabacloud.intellij.cosy";
    private static final String mcpSettings = "lingma_mcp.json";
    private static final String DEFAULT_SETTINGS = "{\"mcpServers\":{}}";
    private Project project;
    private NotificationService notificationService;


    public LingmaMcpClient(Project project) {
        this.project = project;
        this.notificationService = project.getService(NotificationService.class);
    }

    @Override
    public Boolean isInstalled() {
        PluginId pluginId = PluginId.findId(LingmaMcpClient.pluginId);
        if (pluginId == null) {
            //TODO 修改为false
            return false;
        }
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(pluginId);
        if (plugin == null) {
            return false;
        }

        return plugin.isEnabled();
    }

    @Override
    public Result installMcpServer(String name, McpServerConfig config) {
        try {
            if (!isInstalled()) {
                return new Result(CodeEnum.ERROR.getValue(),
                        "install" + name + "failed，" + this.name + "not installed!");
            }

            Map<String, Object> mcpSetting = readAndValidateMcpSettingsFile();
            if (mcpSetting == null || config == null) {
                return new Result(CodeEnum.ERROR.getValue(),
                        "install" + name + "failed，readAndValidateMcpSettingsFile failed");
            }

            if (mcpSetting.get("mcpServers") instanceof Map &&
                    ((Map<?, ?>) mcpSetting.get("mcpServers")).containsKey(name)) {
                return new Result(CodeEnum.ERROR.getValue(),
                        name + "already exists");
            }

            ((Map<String, Object>) mcpSetting.get("mcpServers")).put(name, config);
            String settingsPath = getMcpSettingsFilePath();
            Files.writeString(Paths.get(settingsPath), JacksonHelper.serialize(mcpSetting));

            JetbrainsUtil.openFileInEditor(project, settingsPath);
            return new Result(CodeEnum.SUCCESS.getValue(),
                    "install " + name + " success");
        } catch (Exception e) {

            return new Result(CodeEnum.ERROR.getValue(),
                    "install " + name + " failed: " + e.getMessage());
        }
    }

    @Override
    public Result uninstallMcpServer(String name) {
        try {
            if (!isInstalled()) {
                return new Result(CodeEnum.ERROR.getValue(),
                        "uninstall " + name + " failed，" + this.name + " not installed!");
            }

            Map<String, Object> mcpSetting = readAndValidateMcpSettingsFile();
            if (mcpSetting == null) {
                return new Result(CodeEnum.ERROR.getValue(),
                        "uninstall " + name + " failed，readAndValidateMcpSettingsFile failed");
            }

            if (mcpSetting.get("mcpServers") instanceof Map &&
                    ((Map<?, ?>) mcpSetting.get("mcpServers")).containsKey(name)) {
                ((Map<?, ?>) mcpSetting.get("mcpServers")).remove(name);
                String settingsPath = getMcpSettingsFilePath();
                //同步修改，idea智能异步修改，获取不到结果
                Files.writeString(Paths.get(settingsPath), JacksonHelper.serialize(mcpSetting));
                JetbrainsUtil.openFileInEditor(project, settingsPath);
            }

            return new Result(CodeEnum.SUCCESS.getValue(),
                    "uninstall " + name + " success");
        } catch (Exception e) {

            return new Result(CodeEnum.ERROR.getValue(),
                    "uninstall " + name + " failed: " + e.getMessage());
        }
    }

    @Override
    public List<String> getInstalledMcpServer() {
        try {
            if (!isInstalled()) {
                return Arrays.asList();
            }
            Map<String, Object> mcpSetting = readAndValidateMcpSettingsFile();
            if (mcpSetting == null) {
                return null;
            }
            Object mcpServers = mcpSetting.get("mcpServers");
            if (mcpServers instanceof Map) {
                return ((Map<String, ?>) mcpServers).keySet().stream().collect(Collectors.toList());
            }
        } catch (Exception e) {
            notificationService.error("error", "Failed to get installed MCP servers," + e.getMessage());
        }
        return Arrays.asList();
    }

    private String getSettingsDirectory() throws Exception {
        Path settingsDir = Paths.get(SystemUtil.getUserHome(), "." + name);
        Files.createDirectories(settingsDir);
        return settingsDir.toString();
    }

    private String getMcpSettingsFilePath() throws Exception {
        String settingsDir = getSettingsDirectory();
        Path settingsPath = Paths.get(settingsDir, mcpSettings);
        if (!Files.exists(settingsPath)) {
            Files.writeString(settingsPath, DEFAULT_SETTINGS);
        }
        return settingsPath.toString();
    }

    private Map<String, Object> readAndValidateMcpSettingsFile() {
        try {
            String settingsPath = getMcpSettingsFilePath();
            String content = Files.readString(Paths.get(settingsPath));

            Map<String, Object> config;
            if (content == null || content.trim().isEmpty()) {
                config = new HashMap<>();
            } else {
                config = JacksonHelper.deSerialize(content, Map.class);
            }

            if (config.get("mcpServers") == null) {
                config.put("mcpServers", new HashMap<>());
            }
            return config;
        } catch (Exception e) {
            notificationService.error("error", "Failed to read MCP settings," + e.getMessage());
            return null;
        }
    }


}
