package pt.isel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        try (FileReader reader = new FileReader("ola.txt")) {
            reader.read();
        }
        // reader // cannot be used out of the scope of the try
    }
}
