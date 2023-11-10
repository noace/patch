package com.kyee.upgrade.common.review;

import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.utils.CommandUtil;
import com.kyee.upgrade.utils.LogUtil;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description：代码审查工具类
 * @author：DangHangHang
 * @date：2023-07-04
 */
@Component
public class CodeReviewToolkit {

    private static final Logger log = LoggerFactory.getLogger(CodeReviewToolkit.class);

    @Value("${ruoyi.codeReviewToolkit}")
    private String codeReviewToolkit;

    /**
     * @description：打包编译前代码审查
     * @author：DangHangHang
     * @date：2023-07-04
     */
    public void codeReview(UpPatch patch, String sourcePath, Long patchId, String processId) throws Exception {

        String realCodeList = patch.getRealCodeList();
        if (StringUtils.isEmpty(realCodeList)) {
            throw new BusinessException("代码路径为空，无法进行代码审查！");
        }

        String codePath = realCodeList.replaceAll("[\r\n]", "");
        String[] pathList = codePath.split(",");

        List<String> ruleReportList = new ArrayList<>();
        LogUtil.recordBuildLog("--------------输出编码规约报告开始--------------", patchId, processId);
        try {
            for (String path : pathList) {
                if (!path.endsWith(".java")) {
                    continue;
                }
                String filePath = sourcePath + File.separator + path;
                log.info("需要编码规约的代码路径：" + filePath);
                String script;
                if (path.startsWith("ADD_")) {
                    String addPath = sourcePath + File.separator + path.substring(path.indexOf("ADD_") + 4);
                    if (!new File(addPath).exists()) {
                        throw new Exception("代码路径为空！" + addPath);
                    }
                    script = "java -cp " + codeReviewToolkit + File.separator + "p3c-pmd.jar"
                            + " net.sourceforge.pmd.PMD -d " + addPath
                            + " -R " + codeReviewToolkit + "/ali-strict.xml" + " -f text";
                } else {
                    if (!new File(filePath).exists()) {
                        throw new Exception("代码路径为空！" + filePath);
                    }
                    script = "java -cp " + codeReviewToolkit + File.separator + "p3c-pmd.jar"
                            + " net.sourceforge.pmd.PMD -d " + filePath
                            + " -R " + codeReviewToolkit + "/ali-rules.xml" + " -f text";
                }
                log.info("编码规约脚本：" + script);

                String[] result = {"/bin/sh", "-c", script};
//                String[] result = {"cmd", "/c", script};

                List<String> ruleMsg = CommandUtil.runWithResults(result);
                ruleReportList.addAll(ruleMsg);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }


        if (StringUtils.isNotEmpty(ruleReportList)) {
            for (String report : ruleReportList) {
                LogUtil.recordBuildLog(report, patchId, processId);
                log.info("编码规约报告：" + report);
            }
            LogUtil.recordBuildLog("--------------输出编码规约报告结束--------------", patchId, processId);
//            throw new Exception("请根据编码规约报告修复代码，方可继续打包！");
        } else {
            log.info("编码规约报告为空！");
            LogUtil.recordBuildLog("--------------输出编码规约报告结束--------------", patchId, processId);
        }
    }
}
