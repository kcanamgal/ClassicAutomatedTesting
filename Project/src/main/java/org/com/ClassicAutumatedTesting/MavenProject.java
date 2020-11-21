package org.com.ClassicAutumatedTesting;

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

    private static final String CLASS = "target\\classes\\net\\mooctest";

    private static final String TEST_CLASS = "target\\test-classes\\net\\mooctest";

    private final String path;

    private final String name;

    private List<String> changeInfo;

    public MavenProject(String path){
        if (path.endsWith("\\")){
            path = path.substring(0, path.length() - 1);
        }
//        name = path.substring(path.lastIndexOf("\\") + 1).split("-")[1];
        name = path.substring(path.lastIndexOf("\\") + 1);
        path += "\\";
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
        String testClass = path + TEST_CLASS;
        String classpath =  path + CLASS;
        File target = new File(classpath);
        AnalysisScope analysisScope = AnalysisScopeReader.readJavaScope(scope, new File(exclusion), MavenProject.class.getClassLoader());
        File[] classes = Objects.requireNonNull(target.listFiles());
        if (target.isDirectory()){
            for (File clazz: classes){
                System.out.println(clazz.getName());
                analysisScope.addClassFileToScope(ClassLoaderReference.Application, clazz);
            }
        }
        File testTar = new File(testClass);
        File[] tests = Objects.requireNonNull(testTar.listFiles());
        if (testTar.isDirectory()){
            for (File test: tests){
                analysisScope.addClassFileToScope(ClassLoaderReference.Application, test);
            }
        }
        String[] stdlibs = WalaProperties.getJ2SEJarFiles();
        for (String lib: stdlibs){
            analysisScope.addToScope(ClassLoaderReference.Primordial, new JarFile(lib));
        }
        return analysisScope;
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
