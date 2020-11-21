package org.com.ClassicAutumatedTesting;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;

import java.util.HashSet;
import java.util.Set;

/**
 * 方法层级测试项目的依赖图分析
 */
public class MethodHierarchyAnalysis extends DependencyAnalysis{

    private Set<String> methodScanned;

    public MethodHierarchyAnalysis(MavenProject p){
        super(p, DotFile.Hierarchy.METHOD);
    }

    @Override
    protected void writeDotFile(DotFile file) {
        for (DependencyInfo dep: dependencyInfos){
            // 方法层级需要输出的是方法签名
            file.addDependencies(dep.getCaller().getMethod().getSignature(),
                    dep.getCallee().getMethod().getSignature());
        }
    }


    @Override
    public void beforeSelect() {
        methodScanned = new HashSet<>(25);
    }

    @Override
    protected Set<String> testCaseSelect(String changeInfos) {
        Set<String> selectedCase = new HashSet<>(20);
//        Arrays.stream(changeInfos).forEach(System.out::println);
        // 方法级别选择要看方法
        String method = changeInfos.split(" ")[1];
        for (DependencyInfo node: dependencyInfos){
            //被调用者，确认是否是这个类被调用
            CGNode callee = node.getCallee();
//            System.out.println(callee.getMethod().getSignature());
            if (callee.getMethod().getSignature().equals(method)){
                //调用该方法的方法
                IMethod caller = node.getCaller().getMethod();
                String methodSign = caller.getDeclaringClass().getName().toString() + " " + caller.getSignature();
                if (caller.getAnnotations().stream().anyMatch(
                        p -> p.getType().getName().getClassName().toString().equals("Test"))){
                    /*
                     * 说明该方法是一个测试方法
                     * 方法级别的测试选择只用选择调用修改过的方法的相关测试用例
                     */
                    selectedCase.add(methodSign);
                }
                else {
                    if (!(methodScanned.contains(methodSign))){
                        methodScanned.add(methodSign);
                        selectedCase.addAll(testCaseSelect(methodSign));
                    }

                }
            }
        }
        System.out.println(selectedCase.size());

        return selectedCase;
    }
}
