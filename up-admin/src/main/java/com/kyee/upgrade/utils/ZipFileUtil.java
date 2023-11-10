package com.kyee.upgrade.utils;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.AsiExtraField;
import org.apache.tools.zip.ExtraFieldUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipFileUtil {
    private static final Logger log = LoggerFactory.getLogger(ZipFileUtil.class);

    public static boolean ZipFiles(File zip, File... srcFiles) {
        log.info("ZipUtil function ZipFiles start!");
        boolean result = false;
        String fileCode = "";
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zip));
            fileCode = (String) System.getProperties().get("file.encoding");
            zipFiles(out, "", fileCode, srcFiles);
            result = true;
        } catch (Exception e) {
            result = false;
            log.error(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    log.error("", e);
                }
            }
        }
        log.info("OS encoding is " + fileCode);
        log.info("ZipUtil function ZipFiles end!");
        return result;
    }

    private static void zipFiles(ZipOutputStream out, String path, String fileCode,
                                 File... srcFiles) throws IOException {
        if (!"".equals(path)) {
            path = path.replaceAll("\\\\", "/");
            if (!path.endsWith("/")) {
                path += "/";
            }
        }
        byte[] buf = new byte[1024];
        for (int i = 0; i < srcFiles.length; i++) {
            if (srcFiles[i].isDirectory()) {
                File[] files = srcFiles[i].listFiles();
                String srcPath = srcFiles[i].getName();
                srcPath = new String(srcPath.getBytes(fileCode), fileCode);
                srcPath = srcPath.replaceAll("\\\\", "/");
                if (!srcPath.endsWith("/")) {
                    srcPath += "/";
                }
                ZipEntry zipEntry = new ZipEntry(path + srcPath);
                //zipEntry.setExternalAttributes(755);
                setZTFilePermissions(zipEntry);
                zipEntry.setTime(srcFiles[i].lastModified());
                //zipEntry.setUnixMode(755);
                out.putNextEntry(zipEntry);
                zipFiles(out, path + srcPath, fileCode, files);
            } else {
                FileInputStream in = new FileInputStream(srcFiles[i]);
                log.info(path + srcFiles[i].getName());
                ZipEntry zipEntry = new ZipEntry(path + srcFiles[i].getName());
                //zipEntry.setUnixMode(644);
                //zipEntry.setExternalAttributes(644);
                setZTFilePermissions(zipEntry);
                zipEntry.setTime(srcFiles[i].lastModified());
                out.putNextEntry(zipEntry);
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                
                out.setEncoding(fileCode);
                out.closeEntry();
                in.close();
            }
        }
    }

    /**
     * 任务：APPCOMMERCIALBUG-2442
     * 作者：冯泽
     * 说明：设置文件权限
     *
     * @param zipEntry
     * @return
     */
    private static boolean setZTFilePermissions(ZipEntry zipEntry) {
        ZipExtraField[] fields = new ZipExtraField[1];
        AsiExtraField asiExtraField = new AsiExtraField();
        asiExtraField.setDirectory(zipEntry.isDirectory());
        asiExtraField.setMode(0777);
        fields[0] = asiExtraField;
        zipEntry.setExtra(ExtraFieldUtils.mergeLocalFileDataData(fields));
        return true;
    }


    /**
     * 说明：解压zip文件
     * 作者：冯泽
     * 时间：2016-1-20 20:47:01
     *
     * @param zipFile
     * @param descDir
     * @throws IOException
     */
    public static boolean unZipFiles(File zipFile, String descDir) {
        log.info("zipFileUtil function unZipFiles start!");
        boolean result = true;
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
        } catch (IOException e1) {
            e1.printStackTrace();
            log.error("解压文件失败", e1);
        }
        if (zip == null) {
            log.error("解压文件失败");
            return false;
        }
        for (Enumeration entries = zip.getEntries(); entries.hasMoreElements(); ) {
            OutputStream out = null;
            InputStream in = null;
            String filePath = null;
            Long modifyTime = null;
            try {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                in = zip.getInputStream(entry);

                String outPath = descDir + zipEntryName;
                // 判断路径是否存在,不存在则创建文件路径
                /*
                 * File file = new File(outPath.substring(0,
                 * outPath.lastIndexOf("/"))+File.separator);
                 */

                if (outPath.endsWith("/") || outPath.endsWith("\\")
                        || outPath.endsWith(File.separator)) {
                    File file = new File(outPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    Path path = Paths.get(outPath);
                    File file = new File(path.getParent().toString()
                            + File.separator);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }

                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                // 输出文件路径信息
                log.info("outPath:" + outPath);
                out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }

                filePath = outPath;
                modifyTime = entry.getTime();

            } catch (Exception e) {
                result = false;
                log.error("解压文件失败", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.error("解压文件失败", e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.error("解压文件失败", e);
                    }
                }
                if (filePath != null && modifyTime != null) {
                    File tempFile = new File(filePath);
                    if (tempFile.exists() && tempFile.isFile()) {
                        tempFile.setLastModified(modifyTime);
                    }
                }
            }
        }
        if (zip != null) {
            try {
                zip.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("解压文件失败", e);
            }
        }
        log.info("zipFileUtil function unZipFiles end!");
        return result;
    }

    public static boolean unZipFiles(File zipFile, String descDir, String encoding) {
        log.info("zipFileUtil function unZipFiles start!");
        boolean result = true;
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            encoding = encoding + "";
            encoding = encoding.trim();
            if (DotNetToJavaStringHelper.isNullOrEmpty(encoding)) {
                zip = new ZipFile(zipFile, encoding);
            } else {
                zip = new ZipFile(zipFile);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            log.error("解压文件失败", e1);
        }
        if (zip == null) {
            log.error("解压文件失败");
            return false;
        }
        for (Enumeration entries = zip.getEntries(); entries.hasMoreElements(); ) {
            OutputStream out = null;
            InputStream in = null;
            String filePath = null;
            Long modifyTime = null;
            try {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                in = zip.getInputStream(entry);

                String outPath = descDir + zipEntryName;
                // 判断路径是否存在,不存在则创建文件路径
                /*
                 * File file = new File(outPath.substring(0,
                 * outPath.lastIndexOf("/"))+File.separator);
                 */

                if (outPath.endsWith("/") || outPath.endsWith("\\")
                        || outPath.endsWith(File.separator)) {
                    File file = new File(outPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    Path path = Paths.get(outPath);
                    File file = new File(path.getParent().toString()
                            + File.separator);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }

                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                // 输出文件路径信息
                log.info("outPath:" + outPath);
                out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }

                filePath = outPath;
                modifyTime = entry.getTime();

            } catch (Exception e) {
                result = false;
                log.error("", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.error("", e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.error("", e);
                    }
                }
                if (filePath != null && modifyTime != null) {
                    File tempFile = new File(filePath);
                    if (tempFile.exists() && tempFile.isFile()) {
                        tempFile.setLastModified(modifyTime);
                    }
                }
            }
        }
        if (zip != null) {
            try {
                zip.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("", e);
            }
        }
        log.info("zipFileUtil function unZipFiles end!");
        return result;
    }

    public static String unZipFilesReturnLatestModifiedTime(File zipFile, String descDir) {
        log.info("zipFileUtil function unZipFilesReturnLatestModifiedTime start!");
        String result = "";
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
        } catch (IOException e1) {
            e1.printStackTrace();
            log.error("", e1);
        }
        if (zip == null) {
            log.error("解压文件失败");
            return "";
        }
        for (Enumeration entries = zip.getEntries(); entries.hasMoreElements(); ) {
            OutputStream out = null;
            InputStream in = null;
            String filePath = null;
            Long modifyTime = null;
            try {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();

                in = zip.getInputStream(entry);
                zip.getEncoding();

                String outPath = descDir + zipEntryName;
                // 判断路径是否存在,不存在则创建文件路径
                /*
                 * File file = new File(outPath.substring(0,
                 * outPath.lastIndexOf("/"))+File.separator);
                 */

                if (outPath.endsWith("/") || outPath.endsWith("\\")
                        || outPath.endsWith(File.separator)) {
                    File file = new File(outPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    Path path = Paths.get(outPath);
                    File file = new File(path.getParent().toString()
                            + File.separator);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }

                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                // 输出文件路径信息
                log.info("outPath:" + outPath);
                out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }

                filePath = outPath;
                modifyTime = entry.getTime();

            } catch (Exception e) {
                result = "";
                log.error("", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.error("", e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.error("", e);
                    }
                }
                if (filePath != null && modifyTime != null) {
                    File tempFile = new File(filePath);
                    if (tempFile.exists() && tempFile.isFile()) {
                        tempFile.setLastModified(modifyTime);
                    }
                    result = compareAndReturnGreater(result, modifyTime);
                }
            }
        }
        if (zip != null) {
            try {
                zip.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("", e);
            }
        }
        log.info("zipFileUtil function unZipFilesReturnLatestModifiedTime end!");
        return result;
    }

    /**
     * @param zipFile
     * @param descDir
     * @param encoding
     * @return
     */
    public static String unZipFilesReturnLatestModifiedTime(File zipFile, String descDir, String encoding) {
        log.info("zipFileUtil function unZipFilesReturnLatestModifiedTime start!");
        String result = "";
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            encoding = encoding + "";
            encoding = encoding.trim();
            if (DotNetToJavaStringHelper.isNullOrEmpty(encoding)) {
                zip = new ZipFile(zipFile, encoding);
            } else {
                zip = new ZipFile(zipFile);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            log.error("", e1);
        }
        if (zip == null) {
            log.error("解压文件失败");
            return "";
        }
        for (Enumeration entries = zip.getEntries(); entries.hasMoreElements(); ) {
            OutputStream out = null;
            InputStream in = null;
            String filePath = null;
            Long modifyTime = null;
            try {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();

                in = zip.getInputStream(entry);
                zip.getEncoding();

                String outPath = descDir + zipEntryName;
                // 判断路径是否存在,不存在则创建文件路径
                /*
                 * File file = new File(outPath.substring(0,
                 * outPath.lastIndexOf("/"))+File.separator);
                 */

                if (outPath.endsWith("/") || outPath.endsWith("\\")
                        || outPath.endsWith(File.separator)) {
                    File file = new File(outPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    Path path = Paths.get(outPath);
                    File file = new File(path.getParent().toString()
                            + File.separator);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }

                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                // 输出文件路径信息
                log.info("outPath:" + outPath);
                out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }

                filePath = outPath;
                modifyTime = entry.getTime();

            } catch (Exception e) {
                result = "";
                log.error("", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.error("", e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        log.error("", e);
                    }
                }
                if (filePath != null && modifyTime != null) {
                    File tempFile = new File(filePath);
                    if (tempFile.exists() && tempFile.isFile()) {
                        tempFile.setLastModified(modifyTime);
                    }
                    result = compareAndReturnGreater(result, modifyTime);
                }
            }
        }
        if (zip != null) {
            try {
                zip.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("", e);
            }
        }
        log.info("zipFileUtil function unZipFilesReturnLatestModifiedTime end!");
        return result;
    }

    private static String compareAndReturnGreater(String var1, long var2) {
        String result = "";
        if (!DotNetToJavaStringHelper.isNullOrEmpty(var1)) {
            if (Long.parseLong(var1) > var2) {
                result = var1;
            } else {
                result = Long.toString(var2);
            }
        } else {
            if (var2 != 0) {
                result = Long.toString(var2);
            } else {
                result = "";
            }
        }
        return result;
    }

    /**
     * 合并压缩文件
     *
     * @param tartetZipFile
     * @param sourceZipFiles
     * @return
     */
    public static boolean Marge(String tartetZipFile, List<String> sourceZipFiles) {
        boolean flag = true;
        ZipOutputStream out = null;
        List<ZipInputStream> ins = new ArrayList<ZipInputStream>();

        String localPath = RuoYiConfig.getProfile() + "/" + UUID.randomUUID();
        String mergeTempPath = "";
        int i = 0;
        try {
            out = new ZipOutputStream(new FileOutputStream(tartetZipFile));
            HashSet<String> names = new HashSet<String>();
            for (String sourceZipFile : sourceZipFiles) {

                ZipFile zipFile = new ZipFile(sourceZipFile);
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(sourceZipFile));
                ins.add(zipInputStream);
                ZipEntry ze;
                Enumeration<? extends ZipEntry> enumeration = zipFile.getEntries();
                while (enumeration.hasMoreElements()) {
                    ze = enumeration.nextElement();
                    if (ze.isDirectory()) {

                    } else {
                        if (names.contains(ze.getName())) {

                            continue;
                        }

                        ZipEntry oze = new ZipEntry(ze.getName());

                        if ("readMe.txt".equals(ze.getName())) {
                            mergeTempPath = localPath + "/" + i++ +"readMe.txt";
                            File file = new File(mergeTempPath);
                            if (mergeTempPath.isEmpty()) {
                                file.mkdirs();
                            } else {
                               // FileUtils.mergeFiles(localPath + "/" + "readMe.txt", );

                                // String downloadPath = Constants.RESOURCE_PREFIX + "/" + upProduct.getProductCode() +  "/" + zipFileName;
                            }
                        }

                        out.putNextEntry(oze);
                        if (ze.getSize() > 0) {
                            DataInputStream dis = new DataInputStream(zipFile.getInputStream(ze));
                            int len = 0;
                            byte[] bytes = new byte[1024];
                            while ((len = dis.read(bytes)) > 0) {
                                out.write(bytes, 0, len);
                            }
                            out.closeEntry();
                            out.flush();
                        }
                        names.add(oze.getName());
                    }

                }
                zipInputStream.closeEntry();
                flag = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            flag = false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                for (ZipInputStream in : ins) {
                    if (in != null) {
                        in.close();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return flag;
    }

    public static List<String> getFileName(String path) {//path为绝对路径
        List<String> zipFileName = new ArrayList<String>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files.length == 0) {
            System.out.println("This package is empty");
        } else {
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    String name_1 = file2.getName();
                    File[] zipfile = file2.listFiles();
                    for (File zip : zipfile) {
                        zipFileName.add(name_1 + File.separator + zip.getName().replaceAll(".zip", ""));
                    }
                }
            }
        }
        return zipFileName;
    }

    public static List<String> getZipWithAnt(String path) throws Exception {
        List<String> innerJsonFile = new ArrayList<String>();
        String[] strings = new String[0];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CheckedInputStream check = new CheckedInputStream(fis, new Adler32());
        Charset gbk = Charset.forName("gbk");
        ZipInputStream zis = new ZipInputStream(check, gbk);
        ZipEntry ze = (ZipEntry) zis.getNextEntry();
        BufferedReader br = new BufferedReader(new InputStreamReader(zis));
        char[] chars = new char[1024 * 1024];
        while (ze != null) {
            if (ze.toString().endsWith(".txt")) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.substring(1, line.length() - 1);
                    strings = line.split(",\\{");
                    for (int i = 1; i < strings.length; i++) {
                        strings[i] = "{" + strings[i];
                    }
                    break;
                }
            }
            ze = (ZipEntry) zis.getNextEntry();
        }
        br.close();
        zis.close();
        check.close();
        fis.close();
        for (String str : strings) {
            innerJsonFile.add(str);
        }
        System.out.println(path);
        return innerJsonFile;
    }

    public List<String> getZipInnerFile(String path) {//path格式为xxx.zip
        List<String> innerJsonFile = new ArrayList<String>();
        ZipFile zf = null;
        try {
            zf = new ZipFile(path);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        try {
            InputStream in = null;
            in = new BufferedInputStream(new FileInputStream(path));
            Charset gbk = Charset.forName("GBK");
            ZipInputStream zin = new ZipInputStream(in, gbk);
            ZipEntry ze = new ZipEntry(path);
            while ((ze = (ZipEntry) zin.getNextEntry()) != null) {
                if (ze.toString().endsWith("txt")) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(zf.getInputStream(ze)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.substring(1, line.length() - 1);
                        String[] strings = line.split(",\\{");
                        for (int i = 1; i < strings.length; i++) {
                            strings[i] = "{" + strings[i];
                        }
                        for (String str : strings) {
                            innerJsonFile.add(str);
                        }
                        line = br.readLine();
                    }
                    br.close();
                } else if (ze.toString().startsWith("attach")) {
                    break;
                }
            }
            zin.closeEntry();
            return innerJsonFile;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(path);
        }
        return null;
    }

    public static void writeToTxt(String json, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path, true)));
        out.write(json + "\r\n");
        out.close();
    }

}
