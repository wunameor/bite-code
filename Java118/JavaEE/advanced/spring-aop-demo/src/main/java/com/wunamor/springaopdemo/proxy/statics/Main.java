package com.wunamor.springaopdemo.proxy.statics;

public class Main {
    public static void main(String[] args) {
        ProxyHouseSubject proxyHouseSubject = new ProxyHouseSubject(new FakeHouseSubject());

        proxyHouseSubject.rentHouse();
        proxyHouseSubject.recycleHouse();

        ProxyHouseSubject proxyHouseSubject2 = new ProxyHouseSubject(new RealHouseSubject());

        proxyHouseSubject2.rentHouse();
        proxyHouseSubject2.recycleHouse();

    }
}
