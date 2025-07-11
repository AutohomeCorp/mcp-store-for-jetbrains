package com.autohome.mcpstore.utils;

import com.autohome.mcpstore.services.NotificationService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class JetbrainsUtil {

    //TODO 修改为future，同步获取结果

    public static void openFileInEditor(@NotNull Project project, @NotNull String path) {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                // Get virtual file
                VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
                if (file == null) return;

                // Force refresh file content (reload from disk)
                file.refresh(false, false);

                // Get document manager and check document status
                FileDocumentManager docManager = FileDocumentManager.getInstance();
                Document document = docManager.getDocument(file);

                // If file is modified but not saved, reload from disk
                if (document != null && docManager.isFileModified(file)) {
                    docManager.reloadFromDisk(document);
                }

                // Open file (focus on existing editor or open new one)
                FileEditorManager.getInstance(project).openFile(file, true);

            } catch (Exception e) {
                project.getService(NotificationService.class).error("replaceDocument error", e.getMessage());
            }
        });
    }
}
