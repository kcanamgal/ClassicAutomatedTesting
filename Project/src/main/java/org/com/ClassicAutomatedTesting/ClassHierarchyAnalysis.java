package org.com.ClassicAutomatedTesting;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;

import java.util.HashSet;
import java.util.Set;

/**
 * 类层级测试项目的依赖图分析
 */
public class ClassHierarchyAnalysis extends DependencyAnalysis {

    private Set<String> methodScanned;

    public ClassHierarchyAnalysis(MavenProject p){
        super(p, DotFile.Hierarchy.CLASS);
        methodScanned = new HashSet<>(25);
    }

    @Override
    protected void writeDotFile(DotFile file) {
        for (DependencyInfo dep: dependencyInfos){
            //类层级输出类名
            file.addDependencies(dep.getCaller().getMethod().getDeclaringClass().getName().toString(),
                    dep.getCallee().getMethod().getDeclaringClass().getName().toString());
        }
    }

    @Override
    public void beforeSelect() {
        methodScanned = new HashSet<>(25);
    }

    @Override
    protected Set<String> testCaseSelect(String changeInfos) {
        Set<String> selectedCase = new HashSet<>(10);
        // 类级别选择只看类
        String clazz = changeInfos.split(" ")[0];
        for (DependencyInfo node: dependencyInfos){
            //被调用者，确认是否是这个类被调用
            CGNode callee = node.getCallee();
            if (callee.getMethod().getDeclaringClass().getName().toString().equals(clazz)){
                //调用该方法的方法所在类
                IMethod caller = node.getCaller().getMethod();
                String methodSign = caller.getDeclaringClass().getName().toString() + " " + caller.getSignature();
                if (caller.getAnnotations().stream().anyMatch(
                        p -> p.getType().getName().getClassName().toString().equals("Test"))){
                    /*
                     * 说明该方法是一个测试方法，则该类一定是测试类
                     * 类级别选择是和被修改类相关的测试类下的所有测试用例都会被选中
                     */
                    for (IMethod method: caller.getDeclaringClass().getAllMethods()){
                        if (method.getAnnotations().stream().anyMatch(
                                p -> p.getType().getName().getClassName().toString().equals("Test"))) {
                            selectedCase.add(methodSign);
                        }
                    }
                }
                else {
                    if (!(methodSign.equals(changeInfos) || methodScanned.contains(methodSign))){
                        methodScanned.add(methodSign);
                        selectedCase.addAll(testCaseSelect(methodSign));
                    }
                }
            }
        }

        return selectedCase;
    }
}
