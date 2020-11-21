package org.com.ClassicAutomatedTesting;

import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.CancelException;

/**
 * 用于生成调用图
 */
public final class CallGraphGenerator {

    private CallGraphGenerator(){ }

    /**
     * 调用图构建，该方法以CHA算法生成调用图
     * @param p
     * @param scope
     * @return
     * @throws ClassHierarchyException
     * @throws CancelException
     */
    public static CallGraph createCHACallGraph(MavenProject p, AnalysisScope scope) throws ClassHierarchyException, CancelException {
        ClassHierarchy classHierarchy = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, classHierarchy);
        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
        options.setReflectionOptions(AnalysisOptions.ReflectionOptions.NONE);
        CHACallGraph cg = new CHACallGraph(classHierarchy);
        cg.init(entrypoints);
        System.out.println(CallGraphStats.getCGStats(cg));
        return cg;
    }

    /**
     * 调用图构建，该方法以0-CFA算法生成调用图
     * @param p
     * @param scope
     * @return
     * @throws ClassHierarchyException
     * @throws CancelException
     */
    public static CallGraph createZeroCFACallGraph(MavenProject p, AnalysisScope scope) throws ClassHierarchyException, CancelException {
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        AllApplicationEntrypoints entrypoints = new AllApplicationEntrypoints(scope, cha);
        AnalysisOptions option = new AnalysisOptions(scope, entrypoints);
        SSAPropagationCallGraphBuilder builder = Util.makeZeroCFABuilder(Language.JAVA,
                option, new AnalysisCacheImpl(), cha, scope);
        return builder.makeCallGraph(option);
    }
}
