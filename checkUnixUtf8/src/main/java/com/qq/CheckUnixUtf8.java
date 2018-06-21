package com.qq;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * 检查文件的主程序类
 *
 * @Author: Charly Wan
 */
public class CheckUnixUtf8 {

    static final String LINE_SEPARATOR_UNIX = "\n";
    static final String STRING_UTF8 = "UTF-8";

    public static void main(String[] args) throws IOException {

        if (null == args || (args.length != 1)) {
            System.out.println("Usage: checkUnixUtf8 [FileName or DirectoryName]");
            return;
        }

        Path p = Paths.get(args[0]);
        FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (!attrs.isDirectory()) {
                    checkFileUnixUtf8(file.toString());
                }
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(p, fv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean checkFileUnixUtf8(String fileName) {

        boolean retValue = false;

        File file = new File(fileName);
        try {
            boolean isUnixLine = fileUnixLineSeparator(file);
            boolean isUtf8Encoding = fileUtf8Encoding(file);

            if (!isUnixLine) {
                System.out.printf("%s is not UNIX line\n", fileName);
            }

            if (!isUtf8Encoding) {
                System.out.printf("%s is not UTF-8 encoding\n", fileName);
            }

            retValue = isUnixLine && isUtf8Encoding;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retValue;
    }

    static boolean fileUtf8Encoding(File file) throws IOException {

        String encoding = UniversalDetector.detectCharset(file);
        if ((null != encoding) && encoding.equalsIgnoreCase(STRING_UTF8)) {
            return true;
        }

        return false;
    }

    static boolean fileUnixLineSeparator(File file) throws IOException {

        return retrieveLineSeparator(file).equals(LINE_SEPARATOR_UNIX);
    }

    static String retrieveLineSeparator(File file) throws IOException {

        char current;
        String lineSeparator = "";
        FileInputStream fis = new FileInputStream(file);
        try {
            while (fis.available() > 0) {
                current = (char) fis.read();
                if ((current == '\n') || (current == '\r')) {
                    lineSeparator += current;
                    if (fis.available() > 0) {
                        char next = (char) fis.read();
                        if ((next != current)) {
                            if ((next == '\r') || (next == '\n')){
                                lineSeparator += next;
                            }
                        }
                    }
                    return lineSeparator;
                }
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return null;
    }
}
