package com.q.reminder.reminder.util;

import com.q.reminder.reminder.util.entity.GitCount;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.BlobUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.JGitUtils
 * @Description :
 * @date :  2022.12.30 11:47
 */
@Log4j2
public class JGitUtils {

    public static void main(String[] args) throws Exception {
        String localPath = "d:\\Users\\saiko\\Desktop\\rem";
//        Git git = gitClone("qig", "qigang0925", "https://codeup.aliyun.com/617a04646746bc7c6cc8ce00/redmine/reminder.git", "master", localPath);
        gitPull("qig", "qigang0925", localPath, "master");
        List<GitCount> master = commitResolver(localPath, "master");
        System.out.println(master);

    }

    /**
     * 克隆项目
     *
     * @param username   git用户名
     * @param password   git密码
     * @param remotePath git远程库路径
     * @param branch     git分支
     * @param localPath  下载已有仓库到本地路径
     */
    public static Git gitClone(String username, String password, String remotePath, String branch, String localPath) throws GitAPIException {
        //设置远程服务器上的用户名和密码
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);

        //克隆代码库命令
        CloneCommand cloneCommand = Git.cloneRepository();
        //设置远程URI
        return cloneCommand.setURI(remotePath)
                // 设置分支
                .setBranch(branch)
                // 设置下载存放路径
                .setDirectory(new File(localPath))
                // 设置权限验证
                .setCredentialsProvider(cp)
                .call();
    }


    /**
     * 获取本地所有分支名
     *
     * @author duandi
     */
    public static List<String> getLocalBranchNames(String localPath) throws IOException {
        List<String> result = new LinkedList<>();
        Git git = Git.open(new File(localPath));
        Map<String, Ref> map = git.getRepository().getAllRefs();
        Set<String> keys = map.keySet();
        for (String str : keys) {
            if (str.contains("refs/heads")) {
                String el = str.substring(str.lastIndexOf("/") + 1);
                result.add(el);
            }
        }
        return result;
    }

    /**
     * 切换分支
     * 首先判断本地是否已有此分支
     *
     * @param localPath 主仓
     * @author duandi
     */
    public static String switchBranch(String localPath, String branch) {
        try {
            Git git = Git.open(new File(localPath));
            String newBranch = branch.substring(branch.lastIndexOf("/") + 1);
            CheckoutCommand checkoutCommand = git.checkout();
            List<String> list = getLocalBranchNames(localPath);
            //如果本地分支
            if (!list.contains(newBranch)) {
                git.branchCreate().setName(branch).call();
            }
            checkoutCommand.setName(newBranch);
            checkoutCommand.call();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 切换子仓的分支
     *
     * @param localPath 主仓
     * @param sub       子仓
     * @param branch    分支名
     * @author duandi
     */
    public static String switchSubhBranch(String localPath, String sub, String branch) {
        try {
            Git git = Git.open(new File(localPath + "\\.git\\modules" + sub));
            String newBranch = branch.substring(branch.lastIndexOf("/") + 1);
            CheckoutCommand checkoutCommand = git.checkout();
            List<String> list = getLocalBranchNames(localPath + "\\.git\\modules" + sub);
            //如果本地分支
            if (!list.contains(newBranch)) {
                checkoutCommand.setStartPoint(branch);
                checkoutCommand.setCreateBranch(true);
                checkoutCommand.setForce(true);
            }
            checkoutCommand.setName(newBranch);
            checkoutCommand.call();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * push到远程仓库
     *
     * @author duandi
     */
    public static String pushRepository(String username, String password, String localPath) {
        try {
            Git git = Git.open(new File(localPath));
            PushCommand pushCommand = git.push();
            CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(
                    username, password);
            pushCommand.setCredentialsProvider(credentialsProvider);
            pushCommand.setForce(true).setPushAll();
            Iterable<PushResult> iterable = pushCommand.call();
            for (PushResult pushResult : iterable) {
                log.info(pushResult.toString());
            }
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 远程提交子仓
     *
     * @author duandi
     */
    public static String pushSubRepository(String username, String password, String localPath, String sub) {
        try {
            String newPath = localPath + "\\.git\\modules";
            Git git = Git.open(new File(newPath + sub));
            PushCommand pushCommand = git.push();
            CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(
                    username, password);
            pushCommand.setCredentialsProvider(credentialsProvider);
            pushCommand.setForce(true).setPushAll();
            Iterable<PushResult> iterable = pushCommand.call();
            for (PushResult pushResult : iterable) {
                log.info(pushResult.toString());
            }
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 拉取远程仓库内容至本地
     *
     * @param username  git用户名
     * @param password  git密码
     * @param localPath 下载已有仓库到本地路径
     * @param branch    git分支
     * @author duandi
     */
    public static void gitPull(String username, String password, String localPath, String branch) throws IOException, GitAPIException {
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new
                UsernamePasswordCredentialsProvider(username, password);
        //git仓库地址
        Git git = new Git(new FileRepository(localPath + "/.git"));
        git.pull().setRemoteBranchName(branch).
                setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        log.info("pull完成");
    }

    /**
     * 拿到当前本地分支名
     *
     * @param localPath 主仓
     */
    public static String getCurrentBranch(String localPath) throws IOException {
        Git git = Git.open(new File(localPath));
        return git.getRepository().getBranch();
    }

    /**
     * 拿到当前远程分支名
     *
     * @param localPath 主仓
     */
    public static String getCurrentRemoteBranch(String localPath) throws IOException {
        Git git = Git.open(new File(localPath));
        StoredConfig storedConfig = git.getRepository().getConfig();
        String currentRemote = storedConfig.getString("branch", getCurrentBranch(localPath), "remote");
        return currentRemote;
    }

    /**
     * @author xiaobing
     * 获取所有远程分支
     */
    public static List<String> gitAllBranch(String localPath) throws IOException {
        List<String> result = new LinkedList<String>();
        // 获取名称
        Git git = Git.open(new File(localPath));
        StoredConfig storedConfig = git.getRepository().getConfig();
        String currentRemote = storedConfig.getString("branch", getCurrentBranch(localPath), "remote");
        // 根据名称获取所有分支名称
        Map<String, Ref> map = git.getRepository().getAllRefs();
        Set<String> keys = map.keySet();
        String index = "refs/remotes/" + currentRemote;
        for (String str : keys) {
            if (str.indexOf(index) > -1) {
                String el = str.substring(str.lastIndexOf("/") + 1);
                result.add(str);
            }
        }
        return result;
    }

    /**
     * @param commitList commit对象，只能存在两个
     * @param localPath  下载已有仓库到本地路径
     * @author duandi
     * 进行两版本之间文件对比
     */
    public static Map<String, Object> gitdiff(List<RevCommit> commitList, String localPath) throws IOException, GitAPIException {
        Map<String, Object> map = new HashMap<>();
        Git git = new Git(new FileRepository(localPath + "/.git"));
        Repository repository = git.getRepository();
        AbstractTreeIterator newTree = prepareTreeParser(commitList.get(0), repository);
        AbstractTreeIterator oldTree = prepareTreeParser(commitList.get(1), repository);
        List<DiffEntry> diff = git.diff().setOldTree(oldTree).setNewTree(newTree).setShowNameAndStatusOnly(true).call();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        //设置比较器为忽略空白字符对比（Ignores all whitespace）
        df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        df.setRepository(git.getRepository());
        //每一个diffEntry都是第个文件版本之间的变动差异
        int addSize = 0;
        int subSize = 0;
        for (DiffEntry diffEntry : diff) {
            //打印文件差异具体内容
            df.format(diffEntry);
            String diffText = out.toString(StandardCharsets.UTF_8);

            // 获取文件差异位置，从而统计差异的行数，如增加行数，减少行数
            FileHeader fileHeader = df.toFileHeader(diffEntry);
            List<HunkHeader> hunks = (List<HunkHeader>) fileHeader.getHunks();

            for (HunkHeader hunkHeader : hunks) {
                EditList editList = hunkHeader.toEditList();
                for (Edit edit : editList) {
                    subSize += edit.getEndA() - edit.getBeginA();
                    addSize += edit.getEndB() - edit.getBeginB();

                }
            }
            out.reset();
        }
        map.put("addSize", addSize);
        map.put("subSize", subSize);
        return map;
    }


    private static AbstractTreeIterator prepareTreeParser(RevCommit commit, Repository repository) {
        try (RevWalk walk = new RevWalk(repository)) {
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
            try (ObjectReader oldReader = repository.newObjectReader()) {
                oldTreeParser.reset(oldReader, tree.getId());
            }

            walk.dispose();

            return oldTreeParser;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }


    /**
     * @author xiaobing
     * 获取指定版本行数
     * @Date: 2018/9/26
     */
    public static int getAllFileLines(RevCommit commit, String localPath) throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(localPath + "/.git"));
        Repository repository = git.getRepository();
        TreeWalk treeWalk = new TreeWalk(repository);
        int size = 0;
        try {
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            MutableObjectId id = new MutableObjectId();
            while (treeWalk.next()) {
                treeWalk.getObjectId(id, 0);
                int lines = countAddLine(BlobUtils.getContent(repository, id.toObjectId()));
                size += lines;
            }
        } catch (IOException e) {
            log.error("error:" + e);
        }
        return size;
    }

    /**
     * 统计非空白行数
     *
     * @param content
     * @return
     */
    public static int countAddLine(String content) {
        char[] chars = content.toCharArray();
        int sum = 0;
        boolean notSpace = false;
        for (char ch : chars) {
            if (ch == '\n' && notSpace) {
                sum++;
                notSpace = false;
            } else if (ch > ' ') {
                notSpace = true;
            }
        }
        //最后一行没有换行时，如果有非空白字符，则+1
        if (notSpace) {
            sum++;
        }
        return sum;
    }

    /**
     * 根据项目路径统计git日志
     *
     * @param localPath
     * @param branchMain
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static List<GitCount> commitResolver(String localPath, String branchMain) throws IOException, GitAPIException {
        // 定义数据及变量
        List<GitCount> listGitCount = new ArrayList<>();
        List<Map<String, Object>> list = commitList(localPath, branchMain);
        for (Map maps : list) {
            String branch = (String) maps.get("branch");
            branch = branch.substring(branch.lastIndexOf("/") + 1, branch.length());
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) maps.get("list");
            for (Map map : mapList) {
                RevCommit revCommit = (RevCommit) map.get("commitHash");
                List<RevCommit> listParent = (List<RevCommit>) map.get("commitParentHash");

                // 版本比较获取增删行数
                Map<String, Object> map1 = gitMap(revCommit, listParent, localPath);
                int addLine = (int) map1.get("addLine");
                int removeLine = (int) map1.get("removeLine");

                // 获取当前commit信息
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(revCommit.getAuthorIdent().getWhen());
                String authName = revCommit.getAuthorIdent().getName();
                String commitMessage = revCommit.getFullMessage();
                String usrCde = revCommit.getAuthorIdent().getEmailAddress();

                //System.out.println(usrCde + "-----" + authName + "----" + date + "----" + commitMessage + "新增：" + addLine + "删除：" + removeLine);

                // 获取设置值放入对象中
                GitCount gitCount = new GitCount();
                gitCount.setEmailAddress(usrCde);
                gitCount.setAuthName(authName);
                gitCount.setCommitDate(date);
                gitCount.setCommitMessage(commitMessage);
                gitCount.setAddLine(addLine);
                gitCount.setRemoveLine(removeLine);
                gitCount.setBranch(branch);
                if (commitMessage.contains("Merge")) {
                    gitCount.setIsMerge("Y");
                } else {
                    gitCount.setIsMerge("N");
                }
                listGitCount.add(gitCount);
            }
        }

        return listGitCount;
    }


    /**
     * 提取一个项目所有commit，不包含重复commit记录
     *
     * @param localPath
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static List<Map<String, Object>> commitList(String localPath, String branchMain) throws IOException, GitAPIException {
        // 定义集合
        Set<Map<String, Object>> setMap = new LinkedHashSet<>();
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<Map<String, Object>> holdSet = new ArrayList<>();
        List<Map<String, Object>> holdSet2 = new ArrayList<>();
        // 获取所有远程分支
        List<String> list = gitAllBranch(localPath);
        list.remove(branchMain);
        list.add(0, branchMain);
        // 循环遍历分支
        for (int i = 0; i < list.size(); i++) {
            String branch = list.get(i);
            //fetch
            //GitUtil.fetchBranch(localPath);
            // 切换分支
            // GitUtil.switchBranch(localPath,branch);
            // pull代码
            //GitUtil.gitPull(username,password,localPath,branch);
            // 获取git记录
            Git git = new Git(new FileRepository(localPath + "/.git"));
            Repository repository = git.getRepository();
            RevWalk revWalk = new RevWalk(repository);
            ObjectId commitId = repository.resolve(branch);
            revWalk.markStart(revWalk.parseCommit(commitId));

            // 类型转化为commit对象进行循环提取数据
            for (RevCommit revCommit : revWalk) {
                Map<String, Object> map = new HashMap<>();
                List<RevCommit> listParent = new ArrayList<>();
                // 当前版本哈希值
                map.put("commitHash", revCommit);
                // 父版本哈希值
                // 当前版本meger过来父版本哈希值有几个
                int parentNum = revCommit.getParentCount();
                for (int j = 0; j < parentNum; j++) {
                    // 当前版本父类哈希
                    RevCommit revCommitParent = revCommit.getParent(j);
                    listParent.add(revCommitParent);
                }
                map.put("commitParentHash", listParent);
                //map.put("branch",branch);
                setMap.add(map);
            }

            // 根据分支提取不重复commit对象
            Map<String, Object> map2 = new HashMap<>();
            if (holdSet.size() == 0) {
                List<Map<String, Object>> newList1 = new ArrayList<>(setMap);
                List<Map<String, Object>> newList2 = new ArrayList<>(setMap);
                holdSet = newList1;
                holdSet2 = newList2;
                map2.put("branch", branch);
                map2.put("list", holdSet);
                mapList.add(map2);
            } else {
                List<Map<String, Object>> newList1 = new ArrayList<>(setMap);
                holdSet = newList1;
                if (holdSet2.size() != holdSet.size()) {
                    if (holdSet2.size() > holdSet.size()) {
                        // 移除相同数据
                        holdSet2.removeAll(holdSet);
                        map2.put("branch", branch);
                        map2.put("list", holdSet2);
                        mapList.add(map2);
                    } else if (holdSet.size() > holdSet2.size()) {
                        // 移除相同数据
                        holdSet.removeAll(holdSet2);
                        map2.put("branch", branch);
                        map2.put("list", holdSet);
                        mapList.add(map2);
                    }

                }
                List<Map<String, Object>> newList2 = new ArrayList<>(setMap);
                holdSet2 = newList2;
            }

        }
        return mapList;
    }

    /**
     * 传入父子commit对象返回新增，删除行数
     *
     * @param revCommit
     * @param listParent
     * @param localPath
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static Map<String, Object> gitMap(RevCommit revCommit, List<RevCommit> listParent, String localPath) throws IOException, GitAPIException {
        // 定义数据及变量
        List<RevCommit> commitList = new ArrayList<>();
        Map<String, Object> mapLine = new HashMap<>();
        int addLine = 0;
        int removeLine = 0;
        if (listParent.size() == 0) {
            addLine = getAllFileLines(revCommit, localPath);
        } else {
            for (RevCommit revCommitParent : listParent) {
                // 集合中只保存两个版本值，进行比较，统计出新增和删除行数
                if (commitList.size() == 2) {
                    commitList.clear();
                }
                // 往集合中添加提交版本
                commitList.add(revCommit);
                commitList.add(revCommitParent);
                // 控制版本，两个相邻版本进行文件对比
                Map<String, Object> map = gitdiff(commitList, localPath);
                addLine = addLine + (int) map.get("addSize");
                removeLine = removeLine + (int) map.get("subSize");
            }
        }
        mapLine.put("addLine", addLine);
        mapLine.put("removeLine", removeLine);
        return mapLine;
    }
}
