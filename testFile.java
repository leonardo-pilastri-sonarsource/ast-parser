package org.example;

public class TestFile {

  void testMethod() {
    System.out.println("Hello, World!");
    var x = java.nio.file.Path.of("SomePath").resolve("foo", "goo");
  }

}