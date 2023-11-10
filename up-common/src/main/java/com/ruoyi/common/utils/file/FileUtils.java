package com.ruoyi.common.utils.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.http.MediaType;

/**
 * 文件处理工具类
 *
 * @author ruoyi
 */
public class FileUtils extends org.apache.commons.io.FileUtils
{
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os 输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException
    {
        FileInputStream fis = null;
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0)
            {
                os.write(b, 0, length);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath)
    {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists())
        {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename)
    {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 检查文件是否可下载
     *
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource)
    {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, ".."))
        {
            return false;
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource)))
        {
            return true;
        }

        // 不在允许下载的文件规则
        return false;
    }

    /**
     * 下载文件名重新编码
     *
     * @param request 请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException
    {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE"))
        {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        else if (agent.contains("Firefox"))
        {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        }
        else if (agent.contains("Chrome"))
        {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        else
        {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response 响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException
    {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.setHeader("Content-disposition", contentDispositionValue.toString());
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException
    {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 上传文件
     *
     * @param sourceUrl
     * @return
     * @throws Exception
     */
    public static String uploadFile(String sourceUrl, String uploadUrl, String serverUrl) throws Exception {
        //获取原文件名
        String fileName = sourceUrl.substring(sourceUrl.lastIndexOf("/") + 1);

        sourceUrl = sourceUrl.substring(0,sourceUrl.lastIndexOf("/") +1) + URLEncoder.encode(fileName);
        //创建URL对象，参数传递一个String类型的URL解析地址
        URL url = new URL(sourceUrl);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();

        //从HTTP响应消息获取状态码
        int code = huc.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            //获取输入流
            BufferedInputStream ips = new BufferedInputStream(huc.getInputStream());
            byte[] buffer = new byte[2048];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            while ((len = ips.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }

            try {
                if (bos != null) {
                    bos.close();
                }
                if (ips != null) {
                    ips.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return uploadFileByBytes(bos.toByteArray(), fileName, uploadUrl, serverUrl);
        } else {
            throw new Exception("文件读取失败！");
        }
    }

    /**
     * 上传二进制文件
     *
     * @param bytes
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String uploadFileByBytes(byte[] bytes, String fileName, String uploadUrl, String serverUrl) throws Exception {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < 0) {
                bytes[i] += 256;
            }
        }

        //判断文件夹路径是否存在，如果没有则新建文件夹
        File files = new File(uploadUrl);
        if (!files.exists()) {
            files.mkdirs();
        }

        //文件路径
        String url = uploadUrl + File.separator + fileName;

        //把文件写入到指定路径下
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(url, false))) {
            out.write(bytes);
        }

        // 数据库资源地址
        return serverUrl + fileName;
    }

    /**
     * 文件弹出下载
     * @return
     * @throws Exception
     */
    public static void downloadFile(String resource, HttpServletResponse response) throws Exception {

        if (!FileUtils.checkAllowDownload(resource))
        {
            throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
        }
        // 本地资源路径
        String localPath = RuoYiConfig.getProfile();
        // 数据库资源地址
        String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
        // 下载名称
        String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, downloadName);
        FileUtils.writeBytes(downloadPath, response.getOutputStream());
    }

    public static final int BUFSIZE = 1024 * 8;
    public static void mergeFiles(String outFile, List<String> files) {
        FileChannel outChannel = null;
        try {
            outChannel = new FileOutputStream(outFile).getChannel();
            for(String f : files){
                Charset charset=Charset.forName("utf-8");
                CharsetDecoder chdecoder=charset.newDecoder();
                CharsetEncoder chencoder=charset.newEncoder();
                FileChannel fc = new FileInputStream(f).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
                CharBuffer charBuffer=chdecoder.decode(bb);
                ByteBuffer nbuBuffer=chencoder.encode(charBuffer);
                while(fc.read(nbuBuffer) != -1){

                    bb.flip();
                    nbuBuffer.flip();
                    outChannel.write(nbuBuffer);
                    bb.clear();
                    nbuBuffer.clear();
                }
                fc.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {if (outChannel != null) {outChannel.close();}} catch (IOException ignore) {}
        }
    }

    public static void main(String[] args) {
        List<String> objects = new ArrayList<>();
        objects.add("d:\\1.txt");
        objects.add("d:\\2.txt");

        mergeFiles("d:\\1.txt",objects);
    }
}
