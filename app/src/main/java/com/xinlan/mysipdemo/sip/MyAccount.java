package com.xinlan.mysipdemo.sip;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountInfo;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnRegStartedParam;
import org.pjsip.pjsua2.OnRegStateParam;

/**
 * SIP accout
 */
public class MyAccount extends Account {
    public AccountConfig accoutConfig;

    MyAccount(AccountConfig config) {
        super();
        accoutConfig = config;
    }

    @Override
    public void onRegStarted(OnRegStartedParam prm) {
        System.out.println("onRegStarted");
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        System.out.println("onRegState changed ");
        notifyRegState(prm.getCode() , prm.getReason() , prm.getExpiration());
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        System.out.println("======== Incoming call ======== ");
    }

    @Override
    public void onInstantMessage(OnInstantMessageParam prm) {
        System.out.println("======== Incoming pager ======== ");
        System.out.println("From     : " + prm.getFromUri());
        System.out.println("To       : " + prm.getToUri());
        System.out.println("Contact  : " + prm.getContactUri());
        System.out.println("Mimetype : " + prm.getContentType());
        System.out.println("Body     : " + prm.getMsgBody());
    }

    /**
     *  注册状态发生改变
     * @param code
     * @param reason
     * @param expiration
     */
    private void notifyRegState(int code, String reason, long expiration){
        if(code /100 == 2){
            try {
                AccountInfo info = getInfo();
                System.out.println(info.getUri() + "登录注册成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}//end class
