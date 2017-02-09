package cn.liang.nativecache.javaexpect;

import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.EofMatch;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import expect4j.matches.TimeoutMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mc-050 on 2017/1/13 17:09.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class CommandExcute {

    private static final Logger LOG = LoggerFactory.getLogger(CommandExcute.class);
    public static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;
    private static final long defaultTimeOut = 1000;
    public static final String BACKSLASH_R = "\r|\n|\r\n";
    private StringBuffer buffer = new StringBuffer();

    //正则匹配，用于处理服务器返回的结果
    private String[] linuxPromptRegEx = new String[]{};

    private Expect4j expect;

    private String userName;

    public CommandExcute(Expect4j expect, String userName) {
        this.userName = userName;
        linuxPromptRegEx = new String[]{userName};
        this.expect = expect;
    }

    /**
     * 执行配置命令
     *
     * @param commands 要执行的命令，为字符数组
     * @return 执行是否成功
     */
    public boolean executeCommands(String... commands) {
        //如果expect返回为0，说明登入没有成功
        if (expect == null) {
            return false;
        }

        for (String command : commands) {
            LOG.debug("[executeCommands]Run command is : {}", command);
        }

        List<Match> matchList = new ArrayList<>();
        if (linuxPromptRegEx != null && linuxPromptRegEx.length > 0) {
            addFilterPattern(matchList);
            addDefaultPattern(matchList);
        }
        try {
            boolean isSuccess = true;
            for (String strCmd : commands) {
                isSuccess &= isSuccess(matchList, strCmd);
            }
            return isSuccess;
        } catch (Exception ex) {
            LOG.error("[executeCommands]执行命令出错！", ex);
            return false;
        }
    }

    /**
     * 获取服务器返回的信息
     *
     * @return 服务端的执行结果
     */
    public List<String> getResponse() {
        String result = buffer.toString();
        buffer.delete(0, buffer.length());
        return parseResult(result);
    }

    private List<String> parseResult(String result) {
        String[] array = result.split(BACKSLASH_R);
        if (array.length < 2) {
            return null;
        }
        return Arrays.asList(Arrays.copyOfRange(array, 1, array.length - 2));
    }

    private void addFilterPattern(List<Match> matchList) {
        Closure closure = new Closure() {
            public void run(ExpectState expectState) throws Exception {
                // buffer is string，buffer for appending，output of executed
                // command
                buffer.append(expectState.getBuffer());
                expectState.exp_continue();

            }
        };
        for (String regexElement : linuxPromptRegEx) {// list of regx like, :>, />
            try {
                RegExpMatch mat = new RegExpMatch(regexElement, closure);
                matchList.add(mat);
            } catch (Exception e) {
                LOG.error("[addFilterPattern]添加过滤的模板出错。", e);
            }
        }
    }

    private void addDefaultPattern(List<Match> matchList) {
        Closure closure = new Closure() {
            @Override
            public void run(ExpectState state) throws Exception {
            }
        };
        matchList.add(new EofMatch(closure));
        matchList.add(new TimeoutMatch(defaultTimeOut, closure));
    }

    //检查执行是否成功
    private boolean isSuccess(List<Match> matchList, String strCommandPattern) {
        try {
            expect.send(strCommandPattern);
            expect.send("\r");
            return checkResult(expect.expect(matchList));
        } catch (Exception ex) {
            return false;
        }
    }

    //检查执行返回的状态
    private boolean checkResult(int intRetVal) {
        if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
            return true;
        }
        return false;
    }

    public void setLinuxPromptRegEx(String[] linuxPromptRegEx) {
        this.linuxPromptRegEx = linuxPromptRegEx;
    }
}
