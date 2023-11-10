package com.kyee.upgrade.utils;

import org.apache.commons.io.FileDeleteStrategy;

import java.io.*;
import java.util.ArrayList;

public class FileUtil {
    public static boolean delFile(File file){
        boolean result = true;
        if (file.isDirectory()) {
            String[] children = file.list();
            for(int i=0; i<children.length; i++) {
                if(!delFile(new File(file, children[i]))){
                    result = false;
                }
            }
        }
        if(!file.delete()){
            try {
                FileDeleteStrategy.FORCE.delete(file);
                result = true;
            } catch (IOException e) {
                result = false;
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return result;
    }

    // 复制文件
    public static Long copyFile(File sourceFile, File targetFile,ArrayList<String> notCopyFileOrdir) throws Exception {
        if(sourceFile == null || !isNeedCopy(sourceFile.getAbsolutePath(), notCopyFileOrdir)){
            return null;
        }
        Long lastModified = null;
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            lastModified = sourceFile.lastModified();
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        }catch(Exception e){
            throw e;
        } finally {
            // 关闭流
            if (inBuff != null)
                try {
                    inBuff.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (outBuff != null)
                try {
                    outBuff.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if(lastModified != null && targetFile != null){
                if(targetFile.exists()){
                    targetFile.setLastModified(lastModified);
                }
            }
        }
        return lastModified;
    }

    public static boolean isNeedCopy(String path, ArrayList<String> notCopyFileOrdir){
        path = path.replaceAll("\\\\", "/").toLowerCase();
        if(notCopyFileOrdir != null){
            for(String tmp : notCopyFileOrdir){
                tmp = tmp.replaceAll("\\\\", "/").toLowerCase();
                if(path.endsWith(tmp)){
                    return false;
                }
            }
            return true;
        }else{
            return true;
        }
    }

    public static ConstantUtil.FileType getFileType(String codePath){
        if(codePath != null){
            String codePathLowerCase = codePath.toLowerCase();
            if(codePathLowerCase.endsWith(".sql")){
                return ConstantUtil.FileType.SQL;
            }else if(codePathLowerCase.endsWith(".java")){
                return ConstantUtil.FileType.JAVA;
            }else{
                return ConstantUtil.FileType.OTHER;
            }
        }else{
            return ConstantUtil.FileType.OTHER;
        }
    }
}
