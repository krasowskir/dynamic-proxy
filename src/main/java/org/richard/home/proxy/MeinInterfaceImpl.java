package org.richard.home.proxy;

public class MeinInterfaceImpl implements MeinInterface{
    @Override
    public String zeigeAn(boolean mySwitch) {
        return mySwitch ? "zeigeAn was called with switch = true" : "zeigeAn was called with switch = false";
    }
}
