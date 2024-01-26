package org.richard.home.proxy;

public class AnotherService {
    public String zeigeAn(boolean mySwitch) {
        return mySwitch ? "zeigeAn was called with switch = true" : "zeigeAn was called with switch = false";
    }
}
