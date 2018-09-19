package com.lee.toollibrary;

class A {
    public A() {
        System.out.printf("构造方法");
    }
    {
        System.out.printf("普通代码块");
    }
    static {
        System.out.printf("静态代码块");
    }
}
class  B extends A{
    public B() {
        System.out.printf("B构造方法");
    }
    {
        System.out.printf("B普通代码块");
    }
    static {
        System.out.printf("B静态代码块");
    }

    public static void main(String[] args) {
            new B();
    }
}