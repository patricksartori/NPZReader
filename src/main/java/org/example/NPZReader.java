package org.example;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;

public class NPZReader {

    public static void extractNPZ(String npzFilePath, String outputDir) throws IOException {
        try (ZipFile zipFile = new ZipFile(new File(npzFilePath))) {
            Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                Path outputPath = new File(outputDir, entry.getName()).toPath();
                if (entry.isDirectory()) {
                    Files.createDirectories(outputPath);
                } else {
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        Files.createDirectories(outputPath.getParent());
                        Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            extractNPZ("C:\\Users\\patri\\OneDrive\\Desktop\\Tesi Bachelor\\project_material\\Data\\Dptb-xl_S302_RHR02510_T01_sig.npz", "src/resources");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
