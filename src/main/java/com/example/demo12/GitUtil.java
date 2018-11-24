package com.example.demo12;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
/**
 * http://yonge812.iteye.com/blog/1622163
 * @author niull
 *
 */
public class GitUtil {
	
	private final static String GIT = ".git";

    /**
     * 将文件列表提交到git仓库中
     * @param gitRoot git仓库目录
     * @param files 需要提交的文件列表
     * @return 返回本次提交的版本号
     * @throws IOException 
     */
    /*public static String commitToGitRepository(String gitRoot, List<String> files) throws Exception {
        if (StringUtils.isNotBlank(gitRoot) && files != null && files.size() > 0) {

            File rootDir = new File(gitRoot);

            //初始化git仓库
            if (new File(gitRoot + File.separator + GIT).exists() == false) {
                Git.init().setDirectory(rootDir).call();
            }

            //打开git仓库
            Git git = Git.open(rootDir);
            //判断是否有被修改过的文件
            List<DiffEntry> diffEntries = git.diff()
                .setPathFilter(PathFilterGroup.createFromStrings(files))
                .setShowNameAndStatusOnly(true).call();
            if (diffEntries == null || diffEntries.size() == 0) {
                throw new Exception("提交的文件内容都没有被修改，不能提交");
            }
            //被修改过的文件
            List<String> updateFiles=new ArrayList<String>();
            ChangeType changeType;
            for(DiffEntry entry : diffEntries){
                changeType = entry.getChangeType();
                switch (changeType) {
                    case ADD:
                        updateFiles.add(entry.getNewPath());
                        break;
                    case COPY:
                        updateFiles.add(entry.getNewPath());
                        break;
                    case DELETE:
                        updateFiles.add(entry.getOldPath());
                        break;
                    case MODIFY:
                        updateFiles.add(entry.getOldPath());
                        break;
                    case RENAME:
                        updateFiles.add(entry.getNewPath());
                        break;
                    }
            }
            //将文件提交到git仓库中，并返回本次提交的版本号
            AddCommand addCmd = git.add();
            for (String file : updateFiles) {
                addCmd.addFilepattern(file);
            }
            addCmd.call();

            CommitCommand commitCmd = git.commit();
            for (String file : updateFiles) {
                commitCmd.setOnly(file);
            }
            RevCommit revCommit = commitCmd.setCommitter(Constants.USERNAME, Constants.EMAIL)
                .setMessage("publish").call();
            return revCommit.getName();
        }
        return null;
    }*/

    /**
     * 将git仓库内容回滚到指定版本的上一个版本
     * @param gitRoot 仓库目录
     * @param revision 指定的版本号
     * @return true,回滚成功,否则flase
     * @throws IOException
     */
    public static boolean rollBackPreRevision(String gitRoot, String revision) throws IOException {

        Git git = Git.open(new File(gitRoot));

        Repository repository = git.getRepository();

        RevWalk walk = new RevWalk(repository);
        ObjectId objId = repository.resolve(revision);
        RevCommit revCommit = walk.parseCommit(objId);
        String preVision = revCommit.getParent(0).getName();
        try {
			git.reset().setMode(ResetType.HARD).setRef(preVision).call();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        walk.close();
        repository.close();
        return true;
    }

    /**
     * 查询本次提交的日志
     * @param gitRoot git仓库
     * @param revision  版本号
     * @return 
     * @throws Exception
     */
    public static List<DiffEntry> getLog(String gitRoot, String revision) throws Exception {
        Git git = Git.open(new File(gitRoot));
        Repository repository = git.getRepository();

        ObjectId objId = repository.resolve(revision);
        Iterable<RevCommit> allCommitsLater = git.log().add(objId).call();
        Iterator<RevCommit> iter = allCommitsLater.iterator();
        RevCommit commit = iter.next();
        TreeWalk tw = new TreeWalk(repository);
        tw.addTree(commit.getTree());

        commit = iter.next();
        if (commit != null)
            tw.addTree(commit.getTree());
        else
            return null;

        tw.setRecursive(true);
        RenameDetector rd = new RenameDetector(repository);
        rd.addAll(DiffEntry.scan(tw));

        return rd.compute();
    }
    
    public static void main(String[] args) {
    	String gitRoot = "D:\\Workspaces\\developeWorkSpace\\demo12";
    	String revision = "79007205451fe4ae724ee2c6be44d34496592d49";
		try {
			List<DiffEntry> log = GitUtil.getLog(gitRoot, revision);
			System.out.println(log.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//验证成功，不要轻易使用
		/*try {
			boolean b = GitUtil.rollBackPreRevision(gitRoot, revision);
			System.out.println(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}


