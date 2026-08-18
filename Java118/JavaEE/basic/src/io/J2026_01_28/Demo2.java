package io.J2026_01_28;


import java.io.*;
import java.util.Scanner;

// 复制文件
public class Demo2 {
    private static Scanner in = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        File srcFile = getFile("请输入源文件路径");
        File destFile = getFile("请输入目标文件路径");
        copyFile(srcFile, destFile);
    }

    private static void copyFile(File srcFile, File destFile){
        byte[] bytes = new byte[1024];
        try(InputStream inputStream = new FileInputStream(srcFile);
        OutputStream outputStream = new FileOutputStream(destFile);) {
            int len = -1;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static File getFile(String firstMsg) throws IOException {
        return getFile(firstMsg, "输入不合法，请重新输入：");
    }

    public static File getFile(String firstMsg, String errorMsg) throws IOException {
        File file = null;
        do {
            if (file != null) {
                System.out.println(errorMsg);
            } else {
                System.out.println(firstMsg);
            }
            String src = in.next();
            file = new File(src);
            file.createNewFile();
        } while (!file.isFile());

        return file;
    }
}
