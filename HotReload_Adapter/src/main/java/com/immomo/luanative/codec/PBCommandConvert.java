/**
  * Created by MomoLuaNative.
  * Copyright (c) 2019, Momo Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
package com.immomo.luanative.codec;

import com.immomo.luanative.codec.proto.PackageConst;
import com.immomo.luanative.codec.protobuf.PBBaseCommand;

import com.immomo.luanative.codec.protobuf.PBPingCommand;
import com.immomo.luanative.codec.protobuf.PBPongCommand;

/**
 * The type Pb command convert.
 */
public class PBCommandConvert {

    /**
     * Convert from byte [ ].
     *
     * @param obj the obj
     * @return the byte [ ]
     */
    public static byte[] convertFrom(Object obj) {
        if (obj instanceof com.google.protobuf.GeneratedMessageLite) {
            return ((com.google.protobuf.GeneratedMessageLite)obj).toByteArray();
        }
        return null;
    }

    /**
     * Gets body type.
     *
     * @param obj the obj
     * @return the body type
     */
    public static int getBodyType(Object obj) {
        PBBaseCommand.pbbasecommand baseCmd = null;
        Class<?> clazz = obj.getClass();
        try{
            baseCmd = (PBBaseCommand.pbbasecommand) clazz.getMethod("getBasecommand").invoke(obj);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (baseCmd != null) {
            return baseCmd.getInstruction();
        }
        return -1;
    }

    /**
     * Gets package type.
     *
     * @param obj the obj
     * @return the package type
     */
    public static byte getPackageType(Object obj) {
        if (obj instanceof PBPingCommand.pbpingcommand) {
            return PackageConst.MAGIC_PING;
        } else if (obj instanceof PBPongCommand.pbpongcommand) {
            return PackageConst.MAGIC_PONG;
        }
        return PackageConst.MAGIC_MESSAGE;
    }

}