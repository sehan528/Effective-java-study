package chapter2.item9.ex2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesEx1 {
    //try with resources 이용한 예제
    static String firstLineOfFile(String path) throws IOException {
        try(BufferedReader br = new BufferedReader(
                new FileReader(path)
        )) {
            return br.readLine();
        }
    }
}
