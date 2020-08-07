package com.xinlan.mysipdemo.sip;

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

    public void destory(){
        try {
            endPoint.libDestroy();
            endPoint.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}//end class
