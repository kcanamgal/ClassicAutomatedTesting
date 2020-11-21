package org.com.ClassicAutomatedTesting;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;

/**
 * 表示待测Maven项目
 */
public class MavenProject {

    private static final String TARGET = "target";

    private static final String CLASS = "classes";

    private static final String TEST_CLASS = "test-classes";

    // target文件所在目录
    private final String path;

    private final String name;

    private List<String> changeInfo;

    public MavenProject(String path){
        if (path.endsWith("\\")){
            path = path.substring(0, path.length() - 1);
        }
//        name = path.substring(path.lastIndexOf("\\") + 1).split("-")[1];
        name = path.substring(path.lastIndexOf(File.separator) + 1);
        path += File.separator;
        this.path = path;
        changeInfo = new ArrayList<>(10);
    }

    /**
     * 简易版目标项目文件分析域构建，思路是直接补充完path，复杂实现版后续跟进
     * @param scope
     * @param exclusion
     * @return 分析域构建
     * @throws IOException
     * @throws InvalidClassFileException
     */
    public AnalysisScope createProjectAnalysisScope(String scope, String exclusion)
            throws IOException, InvalidClassFileException {
        String testClassPath = path + File.separator + TARGET + File.separator + TEST_CLASS;
        String classpath =  path + File.separator + TARGET + File.separator + CLASS;
        File target = new File(classpath);
        AnalysisScope analysisScope = AnalysisScopeReader.readJavaScope(scope, new File(exclusion), MavenProject.class.getClassLoader());
        buildProjectAnalysisScope(analysisScope, target);
        File testTar = new File(testClassPath);
        buildProjectAnalysisScope(analysisScope, testTar);
        String[] stdlibs = WalaProperties.getJ2SEJarFiles();
        for (String lib: stdlibs){
            analysisScope.addToScope(ClassLoaderReference.Primordial, new JarFile(lib));
        }
        return analysisScope;
    }

    private static void buildProjectAnalysisScope(AnalysisScope scope, File dir) throws InvalidClassFileException {
        if (!(dir.isDirectory()))
            return;

        if (dir.listFiles() == null)
            return;

        for (File clazz: dir.listFiles()){
            if (clazz.isDirectory()){
                buildProjectAnalysisScope(scope, clazz);
            }
            else {
                System.out.println("Find .class file" + clazz.getName());
                scope.addClassFileToScope(ClassLoaderReference.Application, clazz);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String[] getChangeInfo(){
        return changeInfo.toArray(new String[0]);
    }

    public void setChangeInfo(String infoPath) {
        changeInfo = new ArrayList<>(10);
        loadChangeInfo(infoPath);
    }

    /**
     * 读取change_info.txt文件的变更信息
     */
    private void loadChangeInfo(String infoPath){
        if (!(infoPath.startsWith(path))){
            infoPath = path + infoPath;
        }
        File file = new File(infoPath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null){
                changeInfo.add(line);
            }
            reader.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (reader != null){
                try {
                    reader.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

}
