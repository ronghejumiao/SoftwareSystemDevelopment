package com.ruoyi.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import com.ruoyi.common.config.RuoYiConfig;

public class VideoUtils {

    // ✅ 你的 ffmpeg.exe 路径
    private static final String FFMPEG_PATH = "C:\\SoftwareSystemDevelopment\\ffmpeg-7.1.1-essentials_build\\bin\\ffmpeg.exe";

    /**
     * 提取视频首帧为图片
     * @param videoPath 视频文件相对路径
     * @param coverPath 输出图片相对路径
     * @return 图片相对路径
     */
    public static String generateFirstFrame(String videoPath, String coverPath) throws IOException, InterruptedException {
        // 获取完整的物理路径，注意去掉开头的/profile
        String basePath = RuoYiConfig.getProfile();
        if (videoPath.startsWith("/profile")) {
            videoPath = videoPath.substring("/profile".length());
        }
        if (coverPath.startsWith("/profile")) {
            coverPath = coverPath.substring("/profile".length());
        }
        
        String fullVideoPath = new File(basePath + videoPath).getAbsolutePath();
        String fullCoverPath = new File(basePath + coverPath).getAbsolutePath();
        
        // 确保目录存在
        File coverFile = new File(fullCoverPath);
        if (!coverFile.getParentFile().exists()) {
            coverFile.getParentFile().mkdirs();
        }

        // 检查视频文件是否存在
        File videoFile = new File(fullVideoPath);
        if (!videoFile.exists()) {
            throw new IOException("视频文件不存在: " + fullVideoPath + "\n基础路径: " + basePath + "\n相对路径: " + videoPath);
        }
        
        // 打印调试信息
        System.out.println("基础路径: " + basePath);
        System.out.println("视频相对路径: " + videoPath);
        System.out.println("视频文件是否存在: " + videoFile.exists());
        System.out.println("视频文件大小: " + videoFile.length());
        System.out.println("视频文件完整路径: " + fullVideoPath);
        System.out.println("封面文件完整路径: " + fullCoverPath);
        
        List<String> command = Arrays.asList(
                FFMPEG_PATH,
                "-i", fullVideoPath,
                "-ss", "00:00:01",
                "-vframes", "1",
                fullCoverPath
        );

        System.out.println("执行命令: " + String.join(" ", command));
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // 打印输出，便于调试
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            System.out.println(line);
        }
        
        int exitCode = process.waitFor();
        if (exitCode == 0 && new File(fullCoverPath).exists()) {
            return "/profile" + coverPath.replace("\\", "/");
        } else {
            throw new IOException("提取视频首帧失败，退出码: " + exitCode + "\n命令输出:\n" + output.toString());
        }
    }

    /**
     * 自动生成封面图片路径（视频同目录，带 _cover.jpg）
     */
    public static String generateFirstFrame(String videoPath) throws IOException, InterruptedException {
        String coverPath = videoPath.substring(0, videoPath.lastIndexOf(".")) + "_cover.jpg";
        return generateFirstFrame(videoPath, coverPath);
    }
}
