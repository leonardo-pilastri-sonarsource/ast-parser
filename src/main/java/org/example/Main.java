package org.example;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Main {


  public static void main(String[] args) throws IOException {
    var file = new File("TestFile.java").toPath();
    String content = """
      package org.example;
            
      public class TestFile {

        void testMethod() {
          System.out.println("Hello, World!");
          var x = java.nio.file.Path.of("SomePath").resolve("foo", "goo");
        }
        
      }""";
    Files.write(file, content.getBytes());
    try {
      parse(file.toFile(), "C:\\Users\\leonardo.pilastri\\java\\jdk-23");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println(Arrays.toString(e.getStackTrace()));
    }
  }

  public static void parse(File file, @Nullable String jdkHome) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JavaCompiler compiler;
    if (jdkHome != null) {
      File jdkModules = new File(jdkHome, "jmods");
      ModuleFinder finder = ModuleFinder.of(Paths.get(jdkModules.toURI()));
      Optional<ModuleReference> moduleReference = finder.find("java.compiler");
      URL[] urls = {new File(jdkHome, "lib").toURI().toURL()};
      URLClassLoader classLoader = new URLClassLoader(urls);
      Class<?> toolProviderClass = classLoader.loadClass("javax.tools.ToolProvider");
      Method getSystemJavaCompilerMethod = toolProviderClass.getMethod("getSystemJavaCompiler");
      compiler = (JavaCompiler) getSystemJavaCompilerMethod.invoke(null);
    } else {
      compiler = ToolProvider.getSystemJavaCompiler();
    }

    //var opts = List.of("-source", "23", "-target", "23");
    List<String> opts = Collections.emptyList();

    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);
    var javaFileObjects = fileManager.getJavaFileObjectsFromFiles(List.of(file));
    JavacTask task = (JavacTask) compiler.getTask(null, fileManager, diagnostics, opts, null, javaFileObjects);
    var cus = task.parse();
    task.analyze();
//    task.setTaskListener(new TaskListener() {
//      @Override
//      public void finished(TaskEvent e) {
//        if (e.getKind() == TaskEvent.Kind.ANALYZE) {
//          CompilationUnitTree compilationUnit = e.getCompilationUnit();
//          ClassTree x = (ClassTree) compilationUnit.getTypeDecls().get(0);
//          x.accept(new TreeScanner<Void, Void>() {
//            @Override
//            public Void visitVariable(VariableTree node, Void unused) {
//              System.out.println(node);
//              return super.visitVariable(node, unused);
//            }
//          }, null);
//
//        }
//      }
//
//    });
//
//    task.call();
    diagnostics.getDiagnostics().forEach(System.out::println);
  }
}

//Running with OpenJDK 21,
//      parsing java 21 syntax works
//      parsing records, with -source 11 does not compile, as expected

//Running with OpenJDK 11
//      cannot parse higher language versions

//Built with OpenJDK 11, ran with OpenJDK 17
//     can parse java 17 syntax