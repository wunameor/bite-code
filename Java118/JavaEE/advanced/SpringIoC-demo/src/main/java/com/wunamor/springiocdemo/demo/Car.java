package com.wunamor.springiocdemo.demo;

public class Car {
    private Frame frame;
    public Car(Frame frame) {
        this.frame = frame;
        System.out.println("car init...");
    }

    public void run() {
        System.out.println("car run...");
    }
}
