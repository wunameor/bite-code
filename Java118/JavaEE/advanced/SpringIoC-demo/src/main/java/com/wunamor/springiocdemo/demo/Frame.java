package com.wunamor.springiocdemo.demo;

public class Frame {
    private Tire tire;

    public Frame(Tire tire) {
        this.tire = tire;
        System.out.println("frame init...");
    }
}
