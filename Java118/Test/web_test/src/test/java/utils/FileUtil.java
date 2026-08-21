package utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {


    public static String getLocalPageUrl(String relativePath) {
        return getLocalPageUrl("", relativePath);
    }

    /**
     * 获取本地 HTML 文件的完整 URL
     * @param relativePath 相对于当前项目工作目录的文件路径 (例如 "files/confirm.html")
     * @return 浏览器可识别的 file:/// 绝对路径
     */
    public static String getLocalPageUrl(String baseUrl, String relativePath) {
        // 注意：这里的基准路径可能需要根据你 IDEA 实际的运行目录微调
        // 如果 web_test 是直接在项目根目录下，直接写 relativePath 即可
        // 如果在深层目录，建议把固定的前缀写在这里，例如：
        // "src/test/java/J2025_11_22/web_test"
        Path path = Paths.get(baseUrl, relativePath);

        // 自动将相对路径转换为系统的绝对路径，并格式化为标准的 URI 字符串
        return path.toAbsolutePath().toUri().toString();
    }

    public static String getAbsolutePath(String relativePath) {
        // 注意：这里的基准路径可能需要根据你 IDEA 实际的运行目录微调
        // 如果 web_test 是直接在项目根目录下，直接写 relativePath 即可
        // 如果在深层目录，建议把固定的前缀写在这里，例如：
        // "src/test/java/J2025_11_22/web_test"
        Path path = Paths.get(relativePath);

        // 自动将相对路径转换为系统的绝对路径，并格式化为标准的 URI 字符串
        return path.toAbsolutePath().toString();
    }
}
