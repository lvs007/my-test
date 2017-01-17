package cn.mucang.simple.nativecache.javaexpect;

/**
 * Created by mc-050 on 2017/1/13 15:27.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class Test {

    public static void main(String[] args) {
        String ip = "120.26.208.189";
        int port = 22;
        String user = "liangzhiyan";
        String password = "2saLtWpqwdN56D2N";
//        Shell ssh = new Shell(ip, port, user, password);
//        String cmd[] = {"pwd"};
//        System.out.println(ssh.executeCommands(cmd));
//        System.out.println("resultxxx = " + ssh.getResponse());
//        ssh.disconnect();
        test(ip, port, user, password);
    }

    private static void test(String ip, int port, String user, String password) {
        SshShell sshShell = SshShell.newSshShell(ip, port, user, password);
        CommandExcute commandExcute = sshShell.openSsh();
        commandExcute.getResponse();
        commandExcute.executeCommands("cd ..");
        System.out.println("result=" + commandExcute.getResponse());
        commandExcute.executeCommands("ll");
        System.out.println("result=" + commandExcute.getResponse());
        sshShell.disconnect();
    }

}
