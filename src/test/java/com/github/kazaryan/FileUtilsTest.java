package com.github.kazaryan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
    @TempDir
    Path tempDir;

    @Test
    void readTextFileSuccess() {
        Path path = tempDir.resolve("text.txt");
        FileUtils.writeTextFile(path, "test");

        String text = FileUtils.readTextFile(path);
        assertEquals("test", text);
    }

    @Test
    void readTextFileErrorNull() {
        assertThrows(IllegalArgumentException.class, () -> FileUtils.readTextFile(null));
    }

    @Test
    void readTextFileErrorNotExists() {
        assertThrows(FileOperationException.class, () -> FileUtils.readTextFile(tempDir.resolve("missing.txt")));
    }

    @Test
    void readTextFileErrorPathIsDirectory() {
        assertThrows(FileOperationException.class, () -> FileUtils.readTextFile(tempDir));
    }

    @Test
    void writeTextFileSuccess() {
        Path path = tempDir.resolve("text.txt");
        FileUtils.writeTextFile(path, "test");
        assertEquals("test", FileUtils.readTextFile(path));
    }

    @Test
    void writeTextFileErrorsNulls() {
        Path path = tempDir.resolve("text.txt");
        assertThrows(IllegalArgumentException.class, () -> FileUtils.writeTextFile(null, "test"));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.writeTextFile(path, null));
    }

    // TODO проверить правильность тестов на копирование
    @Test
    void copySuccessNoOverwriteWhenTargetMissing() {
        Path src = tempDir.resolve("src.txt");
        Path dst = tempDir.resolve("dst.txt");
        FileUtils.writeTextFile(src, "data");

        FileUtils.copyFile(src, dst, false);
        assertEquals("data", FileUtils.readTextFile(dst));
    }

    @Test
    void copyErrorTargetExistsNoOverwrite() {
        Path src = tempDir.resolve("src.txt");
        Path dst = tempDir.resolve("dst.txt");
        FileUtils.writeTextFile(src, "data");
        FileUtils.writeTextFile(dst, "old");

        assertThrows(FileOperationException.class, () -> FileUtils.copyFile(src, dst, false));
        assertEquals("old", FileUtils.readTextFile(dst));
    }

    @Test
    void copySuccessOverwrite() {
        Path src = tempDir.resolve("src.txt");
        Path dst = tempDir.resolve("dst.txt");
        FileUtils.writeTextFile(src, "new");
        FileUtils.writeTextFile(dst, "old");

        FileUtils.copyFile(src, dst, true);
        assertEquals("new", FileUtils.readTextFile(dst));
    }

    @Test
    void copyErrorsInvalidSource() {
        Path missing = tempDir.resolve("missing.txt");
        Path dst = tempDir.resolve("dst.txt");
        assertThrows(FileOperationException.class, () -> FileUtils.copyFile(missing, dst, true));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.copyFile(null, dst, true));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.copyFile(dst, null, true));
    }

    // TODO проверить правильность тестов на удаление
    @Test
    void deleteSuccess() {
        Path f = tempDir.resolve("a.txt");
        FileUtils.writeTextFile(f, "x");
        assertTrue(Files.exists(f));

        FileUtils.deleteFile(f);
        assertFalse(Files.exists(f));
    }

    @Test
    void deleteErrorNotExists() {
        Path missing = tempDir.resolve("nope");
        assertThrows(FileOperationException.class, () -> FileUtils.deleteFile(missing));
    }

    @Test
    void deleteErrorNull() {
        assertThrows(IllegalArgumentException.class, () -> FileUtils.deleteFile(null));
    }

    // TODO проверить правильность тестов на поиск
    @Test
    void findByExtensionSuccess() throws IOException {
        Path dir = tempDir.resolve("dir");
        Files.createDirectories(dir);

        FileUtils.writeTextFile(dir.resolve("a.txt"), "1");
        FileUtils.writeTextFile(dir.resolve("b.csv"), "2");

        List<Path> found = FileUtils.findFilesByExtension(dir, "txt");
        assertEquals(1, found.size());
        assertEquals("a.txt", found.getFirst().getFileName().toString());
    }

    @Test
    void findByExtensionErrors() {
        Path notDir = tempDir.resolve("file.txt");
        FileUtils.writeTextFile(notDir, "x");

        assertThrows(IllegalArgumentException.class, () -> FileUtils.findFilesByExtension(null, "txt"));
        assertThrows(IllegalArgumentException.class, () -> FileUtils.findFilesByExtension(tempDir, null));

        Path missingDir = tempDir.resolve("missingDir");
        assertThrows(FileOperationException.class, () -> FileUtils.findFilesByExtension(missingDir, "txt"));
        assertThrows(FileOperationException.class, () -> FileUtils.findFilesByExtension(notDir, "txt"));
    }

}
