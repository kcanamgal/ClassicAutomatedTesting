package org.com.ClassicAutumatedTesting;

import com.ibm.wala.util.io.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 用于表示需要生成的.dot文件
 */
public class DotFile {

    private String start = "digraph";
    private Set<Dependency> dependencies;

    public enum Hierarchy {
        CLASS("class", "cmd_class"),
        METHOD("method", "cmd_method");
        private String content;
        private String name;
        Hierarchy(String name, String content){
            this.name = name;
            this.content = content;
        }

        public String getName() {
            return name;
        }
    }

    public DotFile(Hierarchy cmdType){
        dependencies = new HashSet<>(15);
        start += (" " + cmdType.content);
    }

    /**
     * 添加依赖
     * @param caller
     * @param callee
     * @return
     */
    public boolean addDependencies(String caller, String callee){
        for (Dependency dependency: dependencies){
            if (callee.equals(dependency.callee) && caller.equals(dependency.caller)){
                return false;
            }
        }
        return dependencies.add(new Dependency(caller, callee));
    }

    /**
     * 输出为.dot文件
     * @param path
     * @throws IOException
     */
    public void output(String path) throws IOException {
        if (!(path.endsWith(".dot")))
            throw new IllegalArgumentException("Error path: " + path + " . Should end with \".dot\"");

        StringBuilder builder = new StringBuilder();
        builder.append(start).append(" {\n");
        for (Dependency d: dependencies){
            builder.append("\t").append(d.toString()).append("\n");
        }
        builder.append("}");
        FileUtil.writeFile(new File(path), builder.toString());
    }

    /**
     * 依赖对，仅用于表示“ xxxx -> yyyy”的一对依赖关系
     */
    private static class Dependency {
        private final String caller;
        private final String callee;
        Dependency(String caller, String callee){
            this.caller = caller;
            this.callee = callee;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("\"").append(callee).append("\" -> \"").append(caller).append("\";");
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dependency that = (Dependency) o;
            return caller.equals(that.caller) &&
                    callee.equals(that.callee);
        }

    }
}
