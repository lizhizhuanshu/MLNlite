/**
  * Created by MomoLuaNative.
  * Copyright (c) 2019, Momo Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
package com.immomo.luanative.hotreload.io;

import java.util.concurrent.Callable;

public interface iReader {
    public void read(byte[] data);
    public void onMessage(iMessageListener listener);
}