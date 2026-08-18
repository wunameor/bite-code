package io.J2026_01_28;


import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

// 查询指定文件目录内容
public class Demo1 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        File srcFile = null;
        do {
            if (srcFile != null) {
                System.out.println("输入不合法，请重新输入：");
            } else {
                System.out.println("请输入需要查询的目录：");
            }
//            String src = in.next();
            String src = "D:\\learn\\bite\\教师代码\\java118mysql-ee-beginner";
            srcFile = new File(src);
        } while (!srcFile.isDirectory());

        System.out.println("请输入需要删除的文件名");
//        String fileName = in.next();
        String fileName = "Demo1";
        List<File> files = new LinkedList<>();
        searchFiles(srcFile, fileName, files);

        for(File file : files) {
            // 如果要实现删除，只需要在这里实现即可
            System.out.println(file);
        }
    }

    public static void searchFiles(File srcFile, String fileName, List<File> matchFiles) {
        File[] files = srcFile.listFiles();
        if (files == null || files.length == 0) return;
        for (File file : files) {
            if (file.isDirectory()) {
                // 是目录 递归
                searchFiles(file, fileName, matchFiles);
            } else {
                // 不是目录
                if (file.getName().contains(fileName)) {
                    matchFiles.add(file);
                }
            }
        }
    }
}
