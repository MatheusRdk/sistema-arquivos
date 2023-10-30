package br.edu.utfpr.sistemarquivos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {
    public void read(Path path) {
        if (Files.isRegularFile(path)) {
            try {
                Files.readAllLines(path).forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("The path does not refer to a valid file.");
        }
    }
}
