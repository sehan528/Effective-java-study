package chapter2.item9.ex1;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryFinallyEx1 {
    //finally 이용해 자원 명시적으로 해제
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }
}
