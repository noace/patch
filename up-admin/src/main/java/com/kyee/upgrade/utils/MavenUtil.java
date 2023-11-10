package com.kyee.upgrade.utils;

import com.kyee.upgrade.common.domain.MavenBuildResult;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MavenUtil {
    public synchronized static MavenBuildResult execute(String mavenHome,String pomPath,String command, Long patchId, String processId){
        MavenBuildResult mavenBuildResult = new MavenBuildResult();
        mavenBuildResult.setResultSuccess(false);
        List<String> logArray = new ArrayList<String>();

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( pomPath ) );
        request.setGoals( Collections.singletonList( command ) );

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(mavenHome));

        invoker.setOutputHandler(new InvocationOutputHandler() {
            @Override
            public void consumeLine(String s) throws IOException {
                if (!s.startsWith("[WARNING]") && !s.startsWith("[INFO]")) {
                    LogUtil.recordBuildLog(s, patchId, processId);
                }
                logArray.add(s);
            }
        });

        try{
            if(invoker.execute(request).getExitCode() == 0){
                mavenBuildResult.setResultSuccess(true);
                mavenBuildResult.setLogArray(logArray);
            }else{
                mavenBuildResult.setResultSuccess(false);
                mavenBuildResult.setLogArray(logArray);
            }
        }catch (MavenInvocationException e){
            e.printStackTrace();
            mavenBuildResult.setResultSuccess(false);
            mavenBuildResult.setLogArray(logArray);
        }

        return mavenBuildResult;
    }

    public static void main(String[] args) {
        String mavenHome = "D:/work/software/apache-maven-3.3.9";
        String pomPath = "D:/work/gitTest/his_dwh_server/pom.xml";
        String command = "clean package";
        // execute(mavenHome,pomPath,command);
    }
}
