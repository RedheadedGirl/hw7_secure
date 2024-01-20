package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecodingClassloaderTest {

    private final static String key = "cheburek";

    @BeforeAll
    public static void encode() throws IOException {

        Path fullPathOfCompiledClass = Paths.get("src/test/resources").resolve("Hello.class");
        byte[] classBytes = Files.readAllBytes(fullPathOfCompiledClass);
        byte dif = (byte) key.length();
        for (var i = 0; i < classBytes.length; i++) {
            classBytes[i] += dif;
        }

        Files.write(new File("src/test/resources/Hello").toPath(), classBytes);
    }

    @Test
    void givenEncodedClass_whenFindClass_thenOk() {
        DecodingClassloader classloader = new DecodingClassloader(key, Paths.get("src/test/resources"),
                getClass().getClassLoader());
        var encodedClassName = "Hello";

        assertDoesNotThrow(() -> classloader.findClass(encodedClassName));
    }

    @Test
    void givenEncodedClass_whenFindClassWithBadKey_thenThrow() {
        DecodingClassloader classloader = new DecodingClassloader("bad_key", Paths.get("src/test/resources"),
                getClass().getClassLoader());
        var encodedClassName = "Hello";

        assertThrows(ClassNotFoundException.class, () -> classloader.findClass(encodedClassName));
    }

    @Test
    void givenEncodedClass_whenBadName_thenThrow() {
        DecodingClassloader classloader = new DecodingClassloader(key, Paths.get("src/test/resources"),
                getClass().getClassLoader());
        var encodedClassName = "HelloUnknown";

        assertThrows(ClassNotFoundException.class, () -> classloader.findClass(encodedClassName));
    }

}
