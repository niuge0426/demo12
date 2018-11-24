package com.example.demo12;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;
 
import java.io.File;
import java.io.IOException;
 
/**
 * Created by kxw on 2015/9/18.
 * {<a href='http://wiki.eclipse.org/JGit/User_Guide'>@link</a>}
 */

/*
	Git对象（Git Objects）：就是git的对象。它们在git中用SHA-1来表示。在JGit中用AnyObjectId和ObjectId表示。而它又包含了四种类型：
	二进制大对象（blob）：文件数据
	树（tree）：指向其它的tree和blob
	提交（commit）：指向某一棵tree
	标签（tag）：把一个commit标记为一个标签
	引用（Ref）：对某一个git对象的引用。
	仓库（Repository）：顾名思义，就是用于存储所有git对象和Ref的仓库。
	RevWalk：该类用于从commit的关系图（graph）中遍历commit。晦涩难懂？看到范例就清楚了。
	RevCommit：表示一个git的commit
	RevTag：表示一个git的tag
	RevTree：表示一个git的tree
	TreeWalk：类似RevWalk，但是用于遍历一棵tree
*/
public class TestJgit {
 
    @Test
    public void test() throws IOException, GitAPIException {
        //在用户的账号配置了ssh，即可提交
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        String projectURL = System.getProperty("user.dir");
        Repository repository = builder.setGitDir(new File(projectURL.substring(0, projectURL.lastIndexOf("\\"))+"\\.git"))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
        Git git = new Git(repository);
        AddCommand add = git.add();
        add.addFilepattern(".").call();//git add .
        CommitCommand commit = git.commit();
        /**-Dusername=%teamcity.build.username%**/
        commit.setCommitter("Kingson_Wu", "Kingson_Wu@163.com");
        commit.setAuthor("Kingson_Wu","Kingson_Wu@163.com");
        commit.setAll(true);
        //commit.setCommitter(new PersonIdent(repository));
        RevCommit revCommit = commit.setMessage("use jgit").call();//git commit -m "use jgit"
        String commitId = revCommit.getId().name();
        System.out.println(commitId);
        PushCommand push = git.push();
        push.call();//git push
    }
 
 
    @Test
    public void testURL(){
        String url = this.getClass().getClassLoader().getResource("").getPath();
        System.out.println(url);
        String projectURL = System.getProperty("user.dir");
        //System.out.println(projectURL.lastIndexOf("\\"));
        System.out.println(projectURL.substring(0, projectURL.lastIndexOf("\\"))+"\\.git");
    }
}

