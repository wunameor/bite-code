package com.wunamor.springaopdemo.proxy.statics;

public class ProxyHouseSubject implements HouseSubject {
    private HouseSubject houseSubject;

    public ProxyHouseSubject(HouseSubject houseSubject) {
        this.houseSubject = houseSubject;
    }

    @Override
    public void rentHouse() {
        System.out.println("代理开启");
        houseSubject.rentHouse();
        System.out.println("代理结束");
    }

    @Override
    public void recycleHouse() {
        System.out.println("代理开启");
        houseSubject.recycleHouse();
        System.out.println("代理结束");
    }
}
