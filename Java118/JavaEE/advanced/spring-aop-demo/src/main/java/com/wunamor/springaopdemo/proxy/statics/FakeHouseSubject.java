package com.wunamor.springaopdemo.proxy.statics;

public class FakeHouseSubject implements HouseSubject {
    @Override
    public void rentHouse() {
        System.out.println("fake house subject rent");
    }

    @Override
    public void recycleHouse() {
        System.out.println("fake house subject recycle");
    }
}
