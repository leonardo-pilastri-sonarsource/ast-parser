package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UsingJavaParser {

  public static void main(String[] args) throws IOException {
    String source = """
      package org.example;

      import org.junit.jupiter.api.Test;
      import static org.junit.jupiter.api.Assertions.assertEquals;

      public class HelloWorld {

        @Test
        void test() {
          System.out.println("Hello, World");
          assertEquals(2, 1 + 1);
        }
      }
      """;

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());

    //Add dependencies jars to the classpath
    combinedTypeSolver.add(new JarTypeSolver(
      "C:\\Users\\leonardo.pilastri\\.m2\\repository\\org\\junit\\jupiter\\junit-jupiter-api\\5.8.1\\junit-jupiter-api-5.8.1.jar"
    ));

    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

    ParserConfiguration parserConfiguration = new ParserConfiguration()
      .setSymbolResolver(symbolSolver);

    JavaParser parser = new JavaParser(parserConfiguration);
    CompilationUnit cu = parser.parse(source).getResult().orElseThrow();

    // Now you can work with the CompilationUnit (AST)
    BodyDeclaration<?> m = cu.getType(0).getMember(0);
    System.out.println(m);
    System.out.println(m.getAnnotation(0).resolve().asType());


  }
}
