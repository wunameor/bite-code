package Preparation;

import java.util.HashMap;

class Func2<K extends Comparable<K>, V> {
}




class Father {

}

class Son extends Father {

}

class Message<T> {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

class Func {
    public static void func(Message<? super Son> message) {
        Son data = (Son) message.getData();
        System.out.println(data);
    }
}

public class Test2 {

    public static void main(String[] args) {
        Message<Son> message = new Message<>();
        message.setData(new Son());
        Func.func(message);
    }
}

