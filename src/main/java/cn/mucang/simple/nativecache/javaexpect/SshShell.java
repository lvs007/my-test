package cn.mucang.simple.nativecache.javaexpect;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import expect4j.Expect4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

/**
 * Created by mc-050 on 2017/1/13 16:59.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class SshShell {

    private static final Logger LOG = LoggerFactory.getLogger(SshShell.class);

    //ssh服务器的ip地址
    private String ip;
    //ssh服务器的登入端口
    private int port;
    //ssh服务器的登入用户名
    private String user;
    //ssh服务器的登入密码
    private String password;

    private Session session;
    private ChannelShell channel;

    private Expect4j expect = null;

    private CommandExcute commandExcute;

    private SshShell(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public static SshShell newSshShell(String ip, int port, String user, String password) {
        return new SshShell(ip, port, user, password);
    }

    public CommandExcute openSsh() {
        try {
            LOG.debug(String.format("Start logging to %s@%s:%s", user, ip, port));
            JSch jsch = new JSch();
            session = jsch.getSession(user, ip, port);
            session.setPassword(password);
            Hashtable<String, String> config = new Hashtable<String, String>();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            LocalUserInfo ui = new LocalUserInfo();
            session.setUserInfo(ui);
            session.connect();
            channel = (ChannelShell) session.openChannel("shell");
            expect = new Expect4j(channel.getInputStream(), channel.getOutputStream());
            commandExcute = new CommandExcute(expect, user);
            channel.connect();
            LOG.debug(String.format("Logging to %s@%s:%s successfully!", user, ip, port));
        } catch (Exception ex) {
            LOG.error("Connect to " + ip + ":" + port + "failed,please check your username and password!", ex);
        }
        return this.commandExcute;
    }

    /**
     * 关闭SSH远程连接
     */
    public void disconnect() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    //登入SSH时的控制信息
    //设置不提示输入密码、不显示登入信息等
    private class LocalUserInfo implements UserInfo {
        String passwd;

        public String getPassword() {
            return passwd;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public void showMessage(String message) {

        }
    }
}
