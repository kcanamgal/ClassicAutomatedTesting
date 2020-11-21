package org.com.ClassicAutomatedTesting;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;

import java.util.Objects;

public class Main {

    public static final String SCOPE = "Project\\src\\main\\resources\\scope.txt";
    public static final String EXCLUSION = "Project\\src\\main\\resources\\exclusion.txt";

    /**
     *
     * @param args instruction is like java -jar testSelection.jar -c/-m <project_target> <change_info>
     *             <project_target>: A string representing the "target" directory of the project to be tested.
     *             <change_info>: A string representing the txt file recording the change information of the project.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 以下至空行都是是读取命令行参数部分
        if (!(args[0].startsWith("-"))){
            return;
        }
        char command = args[0].charAt(1);
        String projectTarget = args[1];
        if (projectTarget.endsWith("\\")){
            projectTarget = projectTarget.substring(0, projectTarget.length() - 1);
        }
        if (projectTarget.endsWith("target")){
            projectTarget = projectTarget.substring(0, projectTarget.length() - "target".length());
        }
        MavenProject project = new MavenProject(projectTarget);
        DependencyAnalysis analysis = null;
        switch (command){
            case 'c': analysis = new ClassHierarchyAnalysis(project); break;
            case 'm': analysis = new MethodHierarchyAnalysis(project);
        }
        Objects.requireNonNull(analysis);
        System.out.println("Building CallGraph");
        // 生成分析域和调用图
        AnalysisScope scope = project.createProjectAnalysisScope(SCOPE, EXCLUSION);
        CallGraph cg = CallGraphGenerator.createZeroCFACallGraph(project, scope);
        // 输出依赖图
        analysis.writeDependencyGraphDot(cg);

        System.out.println("Analyzing change_info");
        // 变更信息获取
        String changeInfo = args[2];
        project.setChangeInfo(changeInfo);
        analysis.testCaseSelect();;
        System.out.println("Over");
    }

}
