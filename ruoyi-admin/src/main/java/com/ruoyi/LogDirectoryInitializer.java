package com.ruoyi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 日志目录初始化器
 * 确保应用启动时必要的目录存在
 */
@Component
public class LogDirectoryInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 创建日志目录
        createDirectory("logs");
        // 创建文件上传目录
        createDirectory("uploadPath");
        
        System.out.println("日志目录初始化完成");
    }
    
    private void createDirectory(String dirName) {
        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("成功创建目录: " + dir.getAbsolutePath());
                } else {
                    System.err.println("无法创建目录: " + dir.getAbsolutePath());
                }
            } else {
                System.out.println("目录已存在: " + dir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("创建目录时发生错误: " + e.getMessage());
        }
    }
}
