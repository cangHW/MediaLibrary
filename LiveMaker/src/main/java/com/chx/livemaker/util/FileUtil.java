package com.chx.livemaker.util;

import java.io.File;

/**
 * Created by cangHX
 * on 2018/12/21  15:19
 */
public class FileUtil {

    /**
     * 文件重命名
     *
     * @param path    文件路径
     * @param newName 新文件名
     * @throws NullPointerException   文件不存在抛出异常
     * @throws IllegalAccessException 已存在同名文件
     */
    public static void renameFile(String path, String newName) throws NullPointerException, IllegalAccessException {
        File file = checkFileIsEmptyToThrow(path);
        String name = file.getName();
        if (name.equals(newName)) {
            return;
        }
        String parent = file.getParent();
        File newFile = checkFileIsHasToThrow(new File(parent, newName).getPath());
        boolean flag = file.renameTo(newFile);
        if (!flag) {
            throw new IllegalAccessError("there is failed");
        }
    }

    /**
     * 移动文件
     *
     * @param path    文件路径
     * @param newPath 目标位置
     * @throws NullPointerException   准备移动的文件不存在
     * @throws IllegalAccessException 目标位置已存在同名文件
     */
    public static void removeFile(String path, String newPath) throws NullPointerException, IllegalAccessException {
        File file = checkFileIsEmptyToThrow(path);
        File newFile = checkFileIsHasToThrow(newPath);
        boolean flag = file.renameTo(newFile);
        if (!flag) {
            throw new IllegalAccessError("there is failed");
        }
    }

    /**
     * 保证父路径存在,如果不存在则创建
     *
     * @param path 文件地址
     */
    public static void createParentFile(String path) {
        File file = new File(path);
        File parentFile = new File(file.getParent());
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

    /**
     * 当文件不存在则抛出异常
     *
     * @param path 文件路径
     * @throws NullPointerException 文件不存在
     */
    private static File checkFileIsEmptyToThrow(String path) throws NullPointerException {
        File file = new File(path);
        if (!file.exists()) {
            throw new NullPointerException("file is null");
        }
        return file;
    }

    /**
     * 当文件存在则抛出异常
     *
     * @param path 文件路径
     * @throws IllegalAccessException 文件已存在
     */
    private static File checkFileIsHasToThrow(String path) throws IllegalAccessException {
        File file = new File(path);
        if (file.exists()) {
            throw new IllegalAccessException("file is has");
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return file;
    }
}
