package J2025_12_03;


import java.util.Timer;
import java.util.TimerTask;

public class Demo2 {
    public static void main(String[] args) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello timer 4");
            }
        }, 4000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello timer 3");
            }
        }, 3000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello timer 2");
            }
        }, 2000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello timer 1");
            }
        }, 1000);
    }
}
