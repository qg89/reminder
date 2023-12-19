package com.q.reminder.reminder.task.git;

import cn.hutool.core.io.FileUtil;
import com.q.reminder.reminder.entity.GitCommitLog;
import com.q.reminder.reminder.entity.GitConf;
import com.q.reminder.reminder.service.GitCommitLogService;
import com.q.reminder.reminder.service.GitConfService;
import com.q.reminder.reminder.util.JGitUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.git.GitLogTasks
 * @Description : l
 * @date :  2023.12.19 17:44
 */
@AllArgsConstructor
@Component
public class GitLogTasks implements BasicProcessor {
    private final GitConfService gitConfService;
    private final GitCommitLogService gitCommitLogService;
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        String instanceParams = context.getInstanceParams();
        String jobParams = context.getJobParams();
        ProcessResult processResult = new ProcessResult(true);
        OmsLogger omsLogger = context.getOmsLogger();
        List<GitConf> list = gitConfService.list();
        List<GitCommitLog> data = new ArrayList<>();
        for (GitConf gitConf : list) {
            String keyPath = gitConf.getKeyPath();
            String branchMain = gitConf.getBranchMain();
            String localPath = gitConf.getLocalPath();
            String remoteRepoPath = gitConf.getRemoteRepoPath();
            FileUtil.clean(localPath);
            omsLogger.info("del localPath done! {}", localPath);
            JGitUtils.gitClone(remoteRepoPath, localPath, keyPath);
            omsLogger.info("clone done! {}", localPath);
            Integer id = gitConf.getId();
            List<GitCommitLog> gitCommitLogs = JGitUtils.commitResolver(localPath, branchMain);
            for (GitCommitLog gitCommitLog : gitCommitLogs) {
                gitCommitLog.setConfId(id);
                data.add(gitCommitLog);
            }
            omsLogger.info("git config {}", gitConf);
        }
        gitCommitLogService.saveOrUpdateBatch(data);
        return processResult;
    }
}
