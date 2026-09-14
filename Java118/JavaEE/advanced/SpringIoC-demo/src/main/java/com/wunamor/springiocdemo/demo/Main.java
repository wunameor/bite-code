package com.wunamor.springiocdemo.demo;

public class Main {
    public static void main(String[] args) {
        Tire tire = new Tire(20);
        Frame frame = new Frame(tire);
        Car car = new Car(frame);
        car.run();
    }
}
