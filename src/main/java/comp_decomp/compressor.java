package comp_decomp;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class compressor {

    // Main method: choose algorithm
    public static void method(File file, CompressionType type) throws IOException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("Input file not found.");
        }

        if (type == null) {
            type = CompressionType.GZIP; // default
        }

        switch (type) {
            case XZ -> compressXZ(file);
            case GZIP -> compressGzip(file);
        }
    }

    // Backward-compat: default = GZIP
    public static void method(File file) throws IOException {
        method(file, CompressionType.GZIP);
    }

    // ---- GZIP / DEFLATE ----
    private static void compressGzip(File file) throws IOException {
        File compressedFile = new File(file.getParentFile(), file.getName() + ".gz");

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(compressedFile);
             GZIPOutputStream gzipOut = new GZIPOutputStream(fos)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOut.write(buffer, 0, len);
            }
        }
    }

    // ---- XZ / LZMA2 ----
    private static void compressXZ(File file) throws IOException {
        File compressedFile = new File(file.getParentFile(), file.getName() + ".xz");

        LZMA2Options options = new LZMA2Options(LZMA2Options.PRESET_MAX); // best compression

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(compressedFile);
             XZOutputStream xzOut = new XZOutputStream(fos, options)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                xzOut.write(buffer, 0, len);
            }
        }
    }
}
