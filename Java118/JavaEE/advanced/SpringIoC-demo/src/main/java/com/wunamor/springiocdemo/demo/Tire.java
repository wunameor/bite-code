package com.wunamor.springiocdemo.demo;


public class Tire {
    private int size;
    public Tire(int size) {
        this.size = size;
        System.out.println("tire init...");
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "Tire{" +
                "size=" + size +
                '}';
    }
}
