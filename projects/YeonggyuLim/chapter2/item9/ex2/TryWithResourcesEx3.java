package chapter2.item9.ex2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesEx3 {
    //try with resources 구문에 catch 절 적용
    static String firstLineOfFile(String path, String defaultVal) {
        try(BufferedReader br = new BufferedReader(
                new FileReader(path))) {
            return br.readLine();
        } catch (IOException e) {
            return defaultVal;
        }
    }
}
