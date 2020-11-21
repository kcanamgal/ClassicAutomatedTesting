package org.com.ClassicAutomatedTesting;

import org.com.ClassicAutumatedTesting.Main;
import org.junit.Test;

public class MainTest {

    public static final String CMD = "data\\ClassicAutomatedTesting\\0-CMD";
    public static final String ALU = "data\\ClassicAutomatedTesting\\1-ALU";
    public static final String DATA_LOG = "data\\ClassicAutomatedTesting\\2-DataLog";
    public static final String BINARY_HEAP = "data\\ClassicAutomatedTesting\\3-BinaryHeap";
    public static final String NEXT_DAY = "data\\ClassicAutomatedTesting\\4-NextDay";
    public static final String MORE_TRIANGLE = "data\\ClassicAutomatedTesting\\5-MoreTriangle";
    public static final String CHANGE_INFO = "data\\change_info.txt";


    public static String makeInstruction(char command, String project_target, String change_info){
        StringBuilder instruction = new StringBuilder();
        instruction.append("-").append(command).append(" ").append(project_target).append(" ").append(change_info);
        return instruction.toString();
    }
//
//    @Test
//    public void Test0() throws Exception {
//        String instruction = makeInstruction('m', CMD, CHANGE_INFO);
//        Main.main(instruction.split(" "));
//    }
//
//    @Test
//    public void Test01() throws Exception {
//        String instruction = makeInstruction('c', CMD, CHANGE_INFO);
//        Main.main(instruction.split(" "));
//    }


    @Test
    public void Test1() throws Exception {
        String instruction = makeInstruction('m', ALU, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test2() throws Exception {
        String instruction = makeInstruction('m', DATA_LOG, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test3() throws Exception {
        String instruction = makeInstruction('m', BINARY_HEAP, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test4() throws Exception {
        String instruction = makeInstruction('m', NEXT_DAY, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test5() throws Exception {
        String instruction = makeInstruction('m', MORE_TRIANGLE, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test6() throws Exception {
        String instruction = makeInstruction('c', ALU, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test7() throws Exception {
        String instruction = makeInstruction('c', DATA_LOG, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test8() throws Exception {
        String instruction = makeInstruction('c', BINARY_HEAP, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test9() throws Exception {
        String instruction = makeInstruction('c', NEXT_DAY, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }

    @Test
    public void Test10() throws Exception {
        String instruction = makeInstruction('c', MORE_TRIANGLE, CHANGE_INFO);
        Main.main(instruction.split(" "));
    }


}
