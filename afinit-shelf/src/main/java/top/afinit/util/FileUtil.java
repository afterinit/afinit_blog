package top.afinit.util;

import java.io.File;

public class FileUtil {

    private FileUtil() {
    }

    public static void deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }

        if (!file.delete()) {
            throw new IllegalStateException("Failed to delete: " + file.getAbsolutePath());
        }
    }
}
