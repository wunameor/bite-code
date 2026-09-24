package com.wunamor.springaopdemo.proxy.statics;

public class RealHouseSubject implements HouseSubject {
    @Override
    public void rentHouse() {
        System.out.println("real house subject rent");
    }

    @Override
    public void recycleHouse() {
        System.out.println("real house subject recycle");
    }
}
