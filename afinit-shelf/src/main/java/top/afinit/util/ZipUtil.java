package top.afinit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static File zip(File sourceDir) throws IOException {

        File zipFile = new File(sourceDir.getParent(), sourceDir.getName() + ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipFile(sourceDir, sourceDir.getName(), zos);
        }

        return zipFile;
    }

    private static void zipFile(File file, String path, ZipOutputStream zos) throws IOException {

        if (file.isDirectory()) {

            for (File f : file.listFiles()) {
                zipFile(f, path + "/" + f.getName(), zos);
            }

        } else {

            zos.putNextEntry(new ZipEntry(path));

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;

                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }

            zos.closeEntry();
        }
    }
}
