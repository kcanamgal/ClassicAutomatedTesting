package org.com.ClassicAutomatedTesting;

import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.util.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.com.ClassicAutomatedTesting.DotFile.*;


/**
 * 依赖图的.dot文件生成以及变更信息分析+测试用例选择
 */
public abstract class DependencyAnalysis {

    private static final String CODE_DEPENDENCY_GRAPH_DIR = "dot";

    private static final String SELECTED_CASE_DIR = "." + File.separator;

    protected Hierarchy hierarchy;

    protected MavenProject p;

    protected List<DependencyInfo> dependencyInfos;

    protected DependencyAnalysis() { }

    protected DependencyAnalysis(MavenProject p, Hierarchy hierarchy){
        this.p = p;
        this.hierarchy = hierarchy;
        dependencyInfos = new ArrayList<>();
    }

    /**
     * 生成代码调用图
     * @param cg
     * @return
     */
    public final void writeDependencyGraphDot(CallGraph cg) throws IOException {
        writeDependencyGraphDot(cg, null);
    }

    public final void writeDependencyGraphDot(CallGraph cg, String output) throws IOException {
        if (output == null){
            output = hierarchy.getName() + "-" + p.getName() + "-cfa.dot";
        }
        writeDotFile(cg, output);
    }

    /**
     * 生成dot文件
     * p.s. 类名格式 e.g. Lnet/mooctest/CMDTest1
     *      方法签名格式 e.g. net.mooctest.CMDTest1.<init>()V、
     *      net.mooctest.CMD.addBooleanOption(CLjava/lang/String;)Lnet/mooctest/CMD$Option;
     * @param cg 调用图
     * @param output 输出文件名
     */
    public void writeDotFile(final CallGraph cg, String output) throws IOException {
        DotFile dot = new DotFile(hierarchy);

        for (CGNode node: cg){
            // 一般地，本项目中所有和业务逻辑相关的方法都是ShrikeBTMethod对象
            if (node.getMethod() instanceof ShrikeBTMethod){

                if (node.getMethod().getDeclaringClass().getClassLoader().toString().equals("Application")){
                    /*
                     * CallGraph的有向边表示的是调用关系，然而希望生成的是被调用关系
                     */

                    //找前继节点（应该是该方法的调用者）
                    Iterator<CGNode> it = cg.getPredNodes(node);
                    while (it.hasNext()){
                        CGNode pred = it.next();
                        if (!(pred.getMethod().getDeclaringClass().getClassLoader().toString().equals("Application")))
                            continue;
                        dependencyInfos.add(new DependencyInfo(node, pred));
                    }

                    // 找后继节点（应该是该方法调用的方法）
                    it = cg.getSuccNodes(node);
                    while (it.hasNext()){
                        CGNode succ = it.next();
                        if (!(succ.getMethod().getDeclaringClass().getClassLoader().toString().equals("Application")))
                            continue;
                        dependencyInfos.add(new DependencyInfo(succ, node));
                    }
                }
            }
        }
        // 填充文件
        writeDotFile(dot);
        // 输出文件，先确认一下后缀
        if (!(output.endsWith(".dot")))
            output += ".dot";
        dot.output(CODE_DEPENDENCY_GRAPH_DIR + File.separator + output);

    }

    /**
     * 填充dot文件，不用在这个方法中输出文件
     * @param file
     */
    protected abstract void writeDotFile(DotFile file);

    /**
     * 测试用例选择，这个必须在依赖图的dot文件生成后才能调用
     */
    public final void testCaseSelect() throws IOException {
        beforeSelect();
        Set<String> testMethods = new HashSet<>(20);
        for (String change: p.getChangeInfo()){
            testMethods.addAll(testCaseSelect(change));
        }

        StringBuilder builder = new StringBuilder();
        for (String clazz: testMethods){
            builder.append(clazz).append("\n");
        }
        FileUtil.writeFile(new File(SELECTED_CASE_DIR.concat("selection-").concat(hierarchy.getName()).concat(".txt")), builder.toString());
    }

    public void beforeSelect(){

    }

    /**
     * 变更信息分析+测试用例选择，这个必须在依赖图的dot文件生成后才能调用
     * 在testCaseSelect中被调用以进行测试用例选择
     * 返回的Set中String的格式应为“类标签 方法标签”（中间一个空格即可）
     * @param changeInfos
     * @return
     */
    protected abstract Set<String> testCaseSelect(String changeInfos);



    protected static class DependencyInfo {
        private CGNode caller;
        private CGNode callee;
        private DependencyInfo(CGNode callee, CGNode caller){
            this.caller = caller;
            this.callee = callee;
        }
        protected CGNode getCaller(){
            return caller;
        }
        public CGNode getCallee() {
            return callee;
        }
    }

}
