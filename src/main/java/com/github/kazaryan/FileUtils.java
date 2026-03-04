package com.github.kazaryan;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {
    public static String readTextFile(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Путь к файлу не может быть null");
        }
        if (!Files.exists(path)) {
            throw new FileOperationException("Файла не существует: " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new FileOperationException("Не обычный файл: " + path); // TODO поменять сообщение?
        }
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка при чтении файла: " + path, ex);
        }
    }

    public static void writeTextFile(Path path, String content) {
        if (path == null) {
            throw new IllegalArgumentException("Путь к файлу не может быть null");
        }
        if (content == null) {
            throw new IllegalArgumentException("Содержимое файла не может быть null");
        }
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка при записи файла: " + path, ex);
        }
    }

    public static void copyFile(Path src, Path dst, boolean overwrite) {
        if (src == null || dst == null) { // TODO разделить на две?
            throw new IllegalArgumentException("Путь к файлу не может быть null");
        }
        try {
            if (overwrite) {
                Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            } else {
                Files.copy(src, dst, StandardCopyOption.COPY_ATTRIBUTES);
            }
        } catch (FileAlreadyExistsException ex) {
            throw new FileOperationException("Файл по указанному пути уже существует: " + dst, ex);
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка при копировании файла из  " + src + " в " + dst, ex);
        }
    }

    public static void deleteFile(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Путь к файлу не может быть null");
        }
        if (!Files.exists(path)) {
            throw new FileOperationException("Файла не существует: " + path);
        }
        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка при удалении файла: " + path, ex);
        }
    }

    public static List<Path> findFilesByExtension(Path dir, String extension) {
        if (dir == null) {
            throw new IllegalArgumentException("Путь к папке не может быть null");
        }
        if (extension == null) {
            throw new IllegalArgumentException("Расширение файла не может быть null");
        }
        if (!Files.exists(dir)) {
            throw new FileOperationException("Папки не существует: " + dir);
        }
        if (!Files.isDirectory(dir)) {
            throw new FileOperationException("Указана не папка: " + dir);
        }
        try (Stream<Path> stream = Files.list(dir)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(extension)).toList();
        } catch (IOException ex) {
            throw new FileOperationException("Ошибка при поиске в папке: " + dir, ex);
        }
    }
}