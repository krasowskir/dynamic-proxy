package org.richard.home.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {

private static final Logger log = LoggerFactory.getLogger(MyInvocationHandler.class);

    public MyInvocationHandler() {
        log.info("MyInvocationHandler erzeugt!");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("MyInvocationHandler wurde aufgerufen!");
        if (method.getName().equals("zeigeAn")){
            if ((boolean) args[0]){
                return "zeigeAn wurde aufgerufen und switch war true";
            }
            return "zeigeAn auf dem proxy wurde aufgerufen! Switch war false!";
        }
        return null;
    }
}
