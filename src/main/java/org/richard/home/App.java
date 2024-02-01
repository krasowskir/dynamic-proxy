package org.richard.home;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.MethodInterceptor;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.richard.home.proxy.AnotherService;
import org.richard.home.proxy.MeinInterface;
import org.richard.home.proxy.MeinInterfaceImpl;
import org.richard.home.proxy.MyInvocationHandler;
import org.richard.home.web.PlayerServlet;
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

        App app = new App();
//        app.callCglibProxy();
        app.callServiceWithoutInterface();
        try {
            log.info("server started...");
            app.startServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void callCglibProxy(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MeinInterface.class);
        enhancer.setCallback((FixedValue) () -> "proxy was called beforehands");
        MeinInterface service = (MeinInterface) enhancer.create();
        String result = service.zeigeAn(true);
        log.info("result was: '{}'", result);
    }

    public void callServiceWithoutInterface(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AnotherService.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if ((!(boolean) args[0])){
                return "proxy was called instead";
            } else {
                return proxy.invokeSuper(obj, args);
            }
        });
        AnotherService service = (AnotherService)enhancer.create();
        String result = service.zeigeAn(false);
        log.info("result was: '{}'", result);
    }

    public void startServer() throws Exception {
        Server server = new Server();
        Connector connector = new ServerConnector(server);
        server.addConnector(connector);

        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/api");
        servletContextHandler.addServlet(PlayerServlet.class, "/player/*");
        server.setHandler(servletContextHandler);
        server.start();
    }

}
