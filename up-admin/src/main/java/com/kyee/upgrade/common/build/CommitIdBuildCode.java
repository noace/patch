package com.kyee.upgrade.common.build;

import com.kyee.upgrade.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * 编译模块文件
 */
@Component
public class CommitIdBuildCode {

    private static final Logger log = LoggerFactory.getLogger(CommitIdBuildCode.class);

    /**
     * CommitId打包方式，copy build file
     * @param path
     * @param patchId
     * @param processId
     * @return
     * @throws Exception
     */
    public String commitIdReplaceFilePath(String path, Long patchId, String processId) throws Exception{
        //hisv3
        if(path.toLowerCase().endsWith(".sql")){
            path = path.replace(  "his/db/", "db/");
            path = path.replace(  "his/sql/", "sql/");

            // 暂时这里添加lis
            path = path.replace(  "lis/db/", "db/");
            path = path.replace(  "lis/sql/", "sql/");
        }else if(path.toLowerCase().endsWith(".java")){
            path = path.replace("src/main/java", "WEB-INF/classes");
            path = path.replace("src", "WEB-INF/classes");
        }else{
            if(path.contains("WebContent")){
                path = path.replace("WebContent","");
            }else if(path.contains("src/main/webapp/")){
                path = path.replace("src/main/webapp/","");
            }else if(path.contains("src/main/resources/")){
                path = path.replace("src/main/resources/","WEB-INF/classes/");
            }else if(path.contains("tar.xz")){
                //默认不处理
            }else if(path.contains("src/main/java")){
                path = path.replace("src/main/java", "WEB-INF/classes");
            }else if(path.contains("README")){

            } else{
                LogUtil.recordBuildLog("代码列表路径错误，或者路径有没有考虑到的情况【"+path+"】", patchId, processId);
                throw new Exception("代码列表路径错误，或者路径有没有考虑到的情况");
            }
        }
        return path;
    }

    /**
     * CommitId打包方式，copy build file
     * @param path
     * @param patchId
     * @param processId
     * @return
     * @throws Exception
     */
    public String sourceReplaceFilePath(String path, Long patchId, String processId) throws Exception{
        //hisv3
        if(path.toLowerCase().endsWith(".sql")){
            path = path.replace(  "his/db/", "db/");
            path = path.replace(  "his/sql/", "sql/");

            // 暂时这里添加lis
            path = path.replace(  "lis/db/", "db/");
            path = path.replace(  "lis/sql/", "sql/");
        }else if(path.toLowerCase().endsWith(".java")){
            path = path.replace("src/main/java", "target/classes");
            path = path.replace("src", "target/classes");
        }else{
            if(path.contains("WebContent")){
                path = path.replace("WebContent","");
            }else if(path.contains("src/main/webapp/")){
                path = path.replace("src/main/webapp/","");
            }else if(path.contains("src/main/resources/")){
                path = path.replace("src/main/resources/","target/classes/");
            }else if(path.contains("tar.xz")){
                //默认不处理
            }else if(path.contains("src/main/java")){
                path = path.replace("src/main/java", "target/classes");
            }else if(path.contains("README")) {

            }else{
                LogUtil.recordBuildLog("代码列表路径错误，或者路径有没有考虑到的情况【"+path+"】", patchId, processId);
                throw new Exception("代码列表路径错误，或者路径有没有考虑到的情况");
            }
        }
        return path;
    }

    /**
     *  copy Java file
     * @param sourceUrl  D:\workspace3/his_v5
     * @param tempSourcePath  D:\\workspace3\\his_v5\\his-boot\\target\\upPatchPackage\\
     * @param tepStr  his/his-charge/his-charge-base/target/classes/com/kyee/his/mapper/MedicareMainRecordMapper.java
     * @param projectName  his
     * @param localsubPath  code\
     * @param patchId 9118
     * @param processId  80186f6835f34746b6588deea4523c54
     * @return
     * @throws Exception
     */
    public List<String> commitIdCopyJavaFile(String sourceUrl,String tempSourcePath, String tepStr, String sourcePath, String path, String projectName,String localsubPath,Long patchId, String processId, boolean jar) throws Exception {
        String currentDir = "";
        String fileName = tepStr.substring(tepStr.lastIndexOf("/")+1).replace(".java", "");
        String localPath =  tempSourcePath + localsubPath ;
        String sourceFilePath = sourceUrl + path.substring(path.indexOf("/"),path.lastIndexOf("/"));

        if (jar) {
            sourceFilePath = sourceUrl + File.separator + "his-boot" + File.separator + "target" + File.separator + sourcePath.substring(0, sourcePath.lastIndexOf("/"));
        }

        currentDir = sourcePath.substring(sourcePath.indexOf("WEB-INF/classes"),sourcePath.lastIndexOf("/"));
        File currentDirFile = new File(localPath + projectName + File.separator + currentDir);
        if(!currentDirFile.exists()){
            currentDirFile.mkdirs();
        }
        // 编译后的文件路径
        List<String> compilePaths = new ArrayList<>();
        File file = new File(sourceFilePath);
        if(file.exists()){
            for(File tempFile : file.listFiles()){
                if(tempFile.isFile()){
                    String tempFileName = tempFile.getName();
                    String start = "";
                    String endChar = "";
                    tempFileName = tempFileName.replaceAll("\\\\", "/");
                    if(tempFileName.indexOf("/") != -1){
                        start = "[/]";
                        endChar = "/";
                    }else{
                        start = "";
                    }
                    String compilePath = "";
                    if(tempFileName.endsWith(endChar + fileName + ".class")){
                        //类
                        String destFilePath = currentDirFile.getAbsolutePath() + File.separator + tempFileName;
                        File destFile = new File(destFilePath);
                        FileUtil.copyFile(tempFile, destFile, null);
                        // his\WEB-INF\classes\com\cpinfo\his\web\inpNurseStation\ExecuteAdviceAction.class

                        if (OSValidator.isWindows()) {
                            compilePath = destFilePath.split("upPatchPackage\\\\")[1];
                        } else {
                            compilePath = destFilePath.split("upPatchPackage/")[1];
                        }
                        compilePaths.add(compilePath);
                    }else if(tempFileName.matches(".*"+ start +fileName+"[$].*"+".class")){
                        //内部类
                        String destFilePath = currentDirFile.getAbsolutePath() + File.separator + tempFileName;
                        File destFile = new File(destFilePath);
                        FileUtil.copyFile(tempFile, destFile, null);
                        if (OSValidator.isWindows()) {
                            compilePath = destFilePath.split("upPatchPackage\\\\")[1];
                        } else {
                            compilePath = destFilePath.split("upPatchPackage/")[1];
                        }

                        compilePaths.add(compilePath);
                    }
                }
            }
        }else{
            log.error("资源文件不存在。"+tepStr);
            LogUtil.recordBuildLog("资源文件不存在【" + sourceFilePath + "】", patchId, processId);
            throw new Exception("资源文件不存在。"+tepStr);
        }
        return compilePaths;
    }

    /**
     * copy other code file
     * @param sourceUrl
     * @param tempSourcePath
     * @param tepStr
     * @param projectName
     * @param localsubPath
     * @param patchId
     * @param processId
     * @return
     * @throws Exception
     */
    public String copyCodeFile(String sourceUrl, String tempSourcePath, String tepStr, String sourcePath, String path, String projectName,String localsubPath,Long patchId, String processId, boolean jar) throws Exception {

        String targetPath = "";
        String currentDir = path.substring(path.indexOf("/"),path.lastIndexOf("/"));
        String fileName = path.substring(path.lastIndexOf("/")+1);
        String localPath =  tempSourcePath + localsubPath;
        String sourceRootPath = sourceUrl + File.separator + "target" + File.separator + projectName;
        if (jar) {
            sourceRootPath = sourceUrl + File.separator + "his-boot" + File.separator + "target" + File.separator;
        }

        if (sourcePath.contains("WEB-INF/classes/")) {
            currentDir = sourcePath.substring(sourcePath.indexOf("WEB-INF/classes"),sourcePath.lastIndexOf("/"));
            targetPath = localPath + projectName + File.separator + currentDir;
        } else {
            targetPath = localPath + projectName + currentDir;
        }

        File currentDirFile = new File(targetPath);
        if(!currentDirFile.exists()){
            currentDirFile.mkdirs();
        }

        String sourceFilePath = "";
        if (jar) {
            sourceFilePath = sourceRootPath + sourcePath;
        } else {
            sourceFilePath = sourceUrl + path.substring(path.indexOf("/"));
        }

        File sourceFile = new File(sourceFilePath);
        if(sourceFile.exists()){
            String destFilePath = currentDirFile.getAbsolutePath() + File.separator + fileName;
            File destFile = new File(destFilePath);
            FileUtil.copyFile(sourceFile, destFile, null);

            String compilePath = "";
            if (OSValidator.isWindows()) {
                compilePath = destFilePath.split("upPatchPackage\\\\")[1];
            } else {
                compilePath = destFilePath.split("upPatchPackage/")[1];
            }
            return compilePath;

        }else{
            LogUtil.recordBuildLog("资源文件不存在【" + sourceFilePath + "】", patchId, processId);
            log.error("资源文件不存在。"+tepStr);
            throw new Exception("资源文件不存在。"+tepStr);
        }
    }
}
