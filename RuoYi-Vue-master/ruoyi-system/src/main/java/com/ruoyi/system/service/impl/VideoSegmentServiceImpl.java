package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.IVideoSegmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频分段服务实现类
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@Service
public class VideoSegmentServiceImpl implements IVideoSegmentService {
    private static final Logger log = LoggerFactory.getLogger(VideoSegmentServiceImpl.class);

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("mp4", "avi", "mov", "mkv");

    @Override
    public List<File> segmentVideo(File videoFile, int segmentDuration, long maxFileSize) {
        // 先按时间分段，再检查每段大小，超出则再细分
        List<File> timeSegments = segmentVideoByTime(videoFile, segmentDuration);
        List<File> finalSegments = new ArrayList<>();
        for (File seg : timeSegments) {
            if (seg.length() > maxFileSize) {
                finalSegments.addAll(segmentVideoBySize(seg, maxFileSize));
                seg.delete();
            } else {
                finalSegments.add(seg);
            }
        }
        return finalSegments;
    }

    @Override
    public List<File> segmentVideoByTime(File videoFile, int segmentDuration) {
        List<File> segments = new ArrayList<>();
        Map<String, Object> info = getVideoInfo(videoFile);
        int totalDuration = (int) info.getOrDefault("duration", 0);
        int count = calculateSegmentCount(totalDuration, segmentDuration);
        String baseName = videoFile.getName().replaceAll("\\.[^.]+$", "");
        String ext = videoFile.getName().substring(videoFile.getName().lastIndexOf('.') + 1);
        for (int i = 0; i < count; i++) {
            int start = i * segmentDuration;
            int end = Math.min((i + 1) * segmentDuration, totalDuration);
            String outPath = System.getProperty("java.io.tmpdir") + File.separator + baseName + "_seg" + (i + 1) + "." + ext;
            List<String> cmd = Arrays.asList(
                    "ffmpeg", "-y", "-i", videoFile.getAbsolutePath(),
                    "-ss", String.valueOf(start),
                    "-t", String.valueOf(end - start),
                    "-c", "copy", outPath
            );
            try {
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();
                proc.waitFor();
                File segFile = new File(outPath);
                if (segFile.exists() && segFile.length() > 0) {
                    segments.add(segFile);
                }
            } catch (Exception e) {
                log.error("ffmpeg分段失败: {}", e.getMessage(), e);
            }
        }
        return segments;
    }

    @Override
    public List<File> segmentVideoBySize(File videoFile, long maxFileSize) {
        // 简单实现：递归二分，直到每段小于maxFileSize
        List<File> result = new ArrayList<>();
        if (videoFile.length() <= maxFileSize) {
            result.add(videoFile);
            return result;
        }
        Map<String, Object> info = getVideoInfo(videoFile);
        int duration = (int) info.getOrDefault("duration", 0);
        int mid = duration / 2;
        List<File> first = segmentVideoByTime(videoFile, mid);
        for (File f : first) {
            result.addAll(segmentVideoBySize(f, maxFileSize));
            f.delete();
        }
        return result;
    }

    @Override
    public Map<String, Object> getVideoInfo(File videoFile) {
        Map<String, Object> info = new HashMap<>();
        try {
            List<String> cmd = Arrays.asList("ffmpeg", "-i", videoFile.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            Scanner scanner = new Scanner(proc.getInputStream());
            StringBuilder output = new StringBuilder();
            while (scanner.hasNextLine()) {
                output.append(scanner.nextLine()).append("\n");
            }
            proc.waitFor();
            // 提取时长
            Pattern p = Pattern.compile("Duration: (\\d+):(\\d+):(\\d+)\\.(\\d+)");
            Matcher m = p.matcher(output.toString());
            if (m.find()) {
                int h = Integer.parseInt(m.group(1));
                int min = Integer.parseInt(m.group(2));
                int s = Integer.parseInt(m.group(3));
                int total = h * 3600 + min * 60 + s;
                info.put("duration", total);
            }
        } catch (Exception e) {
            log.error("获取视频信息失败: {}", e.getMessage(), e);
        }
        return info;
    }

    @Override
    public int calculateSegmentCount(int totalDuration, int segmentDuration) {
        return (totalDuration + segmentDuration - 1) / segmentDuration;
    }

    @Override
    public List<Map<String, Object>> generateSegmentInfo(int totalDuration, int segmentDuration) {
        List<Map<String, Object>> list = new ArrayList<>();
        int count = calculateSegmentCount(totalDuration, segmentDuration);
        for (int i = 0; i < count; i++) {
            Map<String, Object> seg = new HashMap<>();
            seg.put("segment_id", i + 1);
            seg.put("start_time", i * segmentDuration);
            seg.put("end_time", Math.min((i + 1) * segmentDuration, totalDuration));
            seg.put("duration", Math.min((i + 1) * segmentDuration, totalDuration) - i * segmentDuration);
            list.add(seg);
        }
        return list;
    }

    @Override
    public void cleanupSegmentFiles(List<File> segmentFiles) {
        for (File f : segmentFiles) {
            if (f.exists()) f.delete();
        }
    }

    @Override
    public boolean isVideoFormatSupported(File videoFile) {
        String name = videoFile.getName().toLowerCase();
        for (String ext : SUPPORTED_FORMATS) {
            if (name.endsWith("." + ext)) return true;
        }
        return false;
    }

    @Override
    public List<String> getSupportedVideoFormats() {
        return SUPPORTED_FORMATS;
    }

    @Override
    public int getVideoDuration(File videoFile) {
        Map<String, Object> info = getVideoInfo(videoFile);
        return (int) info.getOrDefault("duration", 0);
    }
} 