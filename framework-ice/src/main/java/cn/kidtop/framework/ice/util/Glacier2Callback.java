package cn.kidtop.framework.ice.util;

import Ice.ObjectPrx;

public interface Glacier2Callback {
    public void callback(ObjectPrx proxy);
}
