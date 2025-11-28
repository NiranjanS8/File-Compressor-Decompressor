package comp_decomp;

import org.tukaani.xz.XZInputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class decompressor {

    // Auto-detect by extension
    public static File method(File compressedFile) throws IOException {
        if (compressedFile == null || !compressedFile.exists()) {
            throw new FileNotFoundException("Compressed file not found.");
        }

        String name = compressedFile.getName().toLowerCase();

        if (name.endsWith(".xz")) {
            return decompressXZ(compressedFile);
        } else if (name.endsWith(".gz")) {
            return decompressGzip(compressedFile);
        } else {
            throw new IllegalArgumentException("Unknown compression type. Expected .gz or .xz");
        }
    }

    private static File decompressGzip(File compressedFile) throws IOException {
        String baseName = stripExtension(compressedFile.getName());
        File outputFile = new File(compressedFile.getParentFile(), baseName);

        try (FileInputStream fis = new FileInputStream(compressedFile);
             GZIPInputStream gzipIn = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = gzipIn.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }

        return outputFile;
    }

    private static File decompressXZ(File compressedFile) throws IOException {
        String baseName = stripExtension(compressedFile.getName());
        File outputFile = new File(compressedFile.getParentFile(), baseName);

        try (FileInputStream fis = new FileInputStream(compressedFile);
             XZInputStream xzIn = new XZInputStream(fis);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = xzIn.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }

        return outputFile;
    }

    private static String stripExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return name + "_decompressed";
        return name.substring(0, dotIndex);
    }
}
