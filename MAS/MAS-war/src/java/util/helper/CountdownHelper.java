/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helper;

import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import mas.common.session.SystemUserSessionLocal;
import mas.common.util.exception.NoSuchEmailException;
import mas.common.util.exception.NoSuchUsernameException;
import mas.common.util.helper.UserMsg;

/**
 *
 * @author winga_000
 */
public class CountdownHelper {

    Timer timer;
    @EJB
    private SystemUserSessionLocal systemUserSession;
    public CountdownHelper(SystemUserSessionLocal x){
        systemUserSession = x;
    }
    
    class ResetPasswordThread extends Thread {
        int time;
        String email;
        public ResetPasswordThread(int time, String email) {
            this.time = time;
            this.email = email;
        }
        public void run() {
            timer = new Timer();
            System.out.println("CountDown Start.");
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
                Logger.getLogger(CountdownHelper.class.getName()).log(Level.SEVERE, null, ex);
            }           
            try {
                systemUserSession.expireResetPassword(email);
            } catch (NoSuchEmailException ex) {
                Logger.getLogger(CountdownHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("CountDown end.");
        }
    }
    
    public void expireResetPasswoordCountDown(int time, String email) throws InterruptedException {
        ResetPasswordThread thread = new ResetPasswordThread(time, email);
        thread.start();
    }
//    class CountdownTask extends TimerTask {
//
//        int time;
//        String email;
//
//        public CountdownTask(int time, String email) {
//            this.time = time;
//            this.email = email;
//        }
//
//        @Override
//        public void run() {
//            time--;
//            System.out.println("Remaining time: " + time);
//            if (time < 0) {
//                systemUserSession.expireResetPassword(email);
//                timer.cancel();
//            }
//        }
//    }    

    class UnlockUserThread extends Thread {
        int time;
        String username;
        public UnlockUserThread(int time, String username) {
            this.time = time;
            this.username = username;
        }
        public void run() {
            timer = new Timer();
            System.out.println("CountDown Start.");
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
                Logger.getLogger(CountdownHelper.class.getName()).log(Level.SEVERE, null, ex);
            }           
            try {
                systemUserSession.unlockUser(username);
            } catch (NoSuchUsernameException ex) {
                System.err.println(UserMsg.NO_SUCH_EMAIL_ERROR);
                Logger.getLogger(CountdownHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("CountDown end.");
        }
    }
    
    public void unlockUserCountDown(int time, String username) throws InterruptedException {
        UnlockUserThread thread = new UnlockUserThread(time, username);
        thread.start();
    }
}
