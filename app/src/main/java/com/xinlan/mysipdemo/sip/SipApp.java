package com.xinlan.mysipdemo.sip;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.LogConfig;
import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pj_log_decoration;
import org.pjsip.pjsua2.pjsip_transport_type_e;

/**
 *  Sip App
 */
public class SipApp {
    //载入native sip库
    static {
        try {
            System.loadLibrary("openh264");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("UnsatisfiedLinkError: " + e.getMessage());
            System.out.println("This could be safely ignored if you " +
                    "don't need video.");
        }
        System.loadLibrary("pjsua2");
        System.out.println("Library loaded");
    }

    public static final String SIP_PROTOCOL = "sip:";
    public static final String SIP_SERVER = "115.239.133.189:5068";
    public static final String SIP_TRANSPORT_TCP = ";transport=tcp";

    public static final int SIP_PORT = 5068;
    private final int LOG_LEVEL = 4;

    private volatile static SipApp instance;
    private SipApp(){
    }

    public static SipApp getInstance(){
        if(instance == null){
            synchronized (SipApp.class){
                if(instance == null){
                    instance = new SipApp();
                }
            }
        }
        return instance;
    }

    /**
     * 自定义日志
     */
    public static class MyLogWriter extends LogWriter {
        @Override
        public void write(LogEntry entry) {
            System.out.println(entry.getMsg());
        }
    }

    private Endpoint endPoint = new Endpoint();
    private EpConfig epConfig = new EpConfig();
    private TransportConfig sipTpConfig = new TransportConfig();

    private MyAccount mMyAccount;

    private MyLogWriter logWriter = new MyLogWriter();

    public void initSip(){
        try {
            endPoint.libCreate();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        sipTpConfig.setPort(SIP_PORT);

        //日志配置
        epConfig.getLogConfig().setLevel(LOG_LEVEL);
        epConfig.getLogConfig().setConsoleLevel(LOG_LEVEL);

        LogConfig logCfg = epConfig.getLogConfig();
        logWriter = new MyLogWriter();
        logCfg.setWriter(logWriter);
        logCfg.setDecor(logCfg.getDecor() & ~(pj_log_decoration.PJ_LOG_HAS_CR | pj_log_decoration.PJ_LOG_HAS_NEWLINE));

        //UserAgent配置
        UaConfig userAgentConfig = epConfig.getUaConfig();
        userAgentConfig.setUserAgent("Pjsua2 for xinalnTest " + endPoint.libVersion().getFull());
        //userAgentConfig.setThreadCnt(0);
        //userAgentConfig.setMainThreadOnly(true);

        /* Init endpoint */
        try {
            endPoint.libInit(epConfig);
        } catch (Exception e) {
            return;
        }

        /* Create transports. */
        try {
            endPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP,
                    sipTpConfig);
            endPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP,
                    sipTpConfig);
            sipTpConfig.setPort(SIP_PORT + 1);
            endPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TLS,
                    sipTpConfig);
        } catch (Exception e) {
            System.out.println(e);
        }
        sipTpConfig.setPort(SIP_PORT);

        /* Start. */
        try {
            endPoint.libStart();
        } catch (Exception e) {
            return;
        }
    }

    /**
     *  发起注册 register
     * @param account
     * @param pwd
     */
    public void sipLogin(String account , String pwd , String ipAddress){
        AccountConfig accountConfig = new AccountConfig();
        String accId = SIP_PROTOCOL+account+"@"+ipAddress+":"+SIP_PORT;
        String registar = SIP_PROTOCOL + SIP_SERVER +SIP_TRANSPORT_TCP;
        String username = account;

        System.out.println("login info: ");
        System.out.println("id               :  " + accId);
        System.out.println("registar     :  " + registar);
        System.out.println("username :  " + username);
        System.out.println("pwd            :  " + pwd);

        accountConfig.setIdUri(accId);
        accountConfig.getRegConfig().setRegistrarUri(registar);
        AuthCredInfoVector creds = accountConfig.getSipConfig().getAuthCreds();creds.clear();
        if (username.length() != 0) {
            creds.add(new AuthCredInfo("Digest", "*", username, 0, pwd));
        }
        accountConfig.getNatConfig().setIceEnabled(true); // Enable ICE

        try{
            if(mMyAccount == null){
                mMyAccount = new MyAccount(accountConfig);
                mMyAccount.create(accountConfig);
            }else{
                mMyAccount.modify(accountConfig);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void destory(){
        try {
            endPoint.libDestroy();
            endPoint.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}//end class
