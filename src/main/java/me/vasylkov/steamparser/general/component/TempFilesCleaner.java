package me.vasylkov.steamparser.general.component;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Component
public class TempFilesCleaner
{
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String FILE_PREFIX = "checker";
    private static final String DIR_PREFIX = "chrome-profile-";

    private void cleanAllTempFiles() throws IOException
    {
        Files.walkFileTree(Paths.get(TEMP_DIR), new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                if (file.getFileName().toString().startsWith(FILE_PREFIX))
                {
                    Files.deleteIfExists(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void cleanAllTempDirectories() throws IOException
    {
        Files.walkFileTree(Paths.get(TEMP_DIR), new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                if (dir.getFileName().toString().startsWith(DIR_PREFIX))
                {
                    deleteDirectoryAndContents(dir);
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void deleteDirectoryAndContents(Path dir) throws IOException
    {
        Files.walkFileTree(dir, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @PreDestroy
    public void cleanUp() throws IOException
    {
        cleanAllTempFiles();
        cleanAllTempDirectories();
    }
}
