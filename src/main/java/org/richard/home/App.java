package org.richard.home;

import org.richard.home.proxy.MeinInterface;
import org.richard.home.proxy.MyInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {

        Class<?>[] interfaces = new Class[]{MeinInterface.class};
        ClassLoader classLoader = MeinInterface.class.getClassLoader();

        InvocationHandler invocationHandlerRef = new MyInvocationHandler();

        MeinInterface myProxy = (MeinInterface) Proxy.newProxyInstance(classLoader, interfaces, invocationHandlerRef);
        log.info("invoking the proy: {}", myProxy.zeigeAn(false));
        assert myProxy.zeigeAn(false).equals("zeigeAn auf dem proxy wurde aufgerufen! Switch war false!");
        System.out.println("Hello dynamic jdk proxy!");
    }
}
