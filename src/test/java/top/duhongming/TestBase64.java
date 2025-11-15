package top.duhongming;

import java.util.Base64;

public class TestBase64 {
    public static final String TOKEN = "nacosnacosnacosnacosnacosnacosnacos";
    public static void main(String[] args) {
        System.out.println("TOKEN = " + TOKEN.length());
        String base64 = Base64.getEncoder().encodeToString(TOKEN.getBytes());
        System.out.println(base64);
    }
}
