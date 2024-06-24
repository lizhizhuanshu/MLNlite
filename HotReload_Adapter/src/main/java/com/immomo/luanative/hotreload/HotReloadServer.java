/**
  * Created by MomoLuaNative.
  * Copyright (c) 2019, Momo Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
package com.immomo.luanative.hotreload;


import android.util.Log;

import com.immomo.luanative.codec.PBCommandConvert;
import com.immomo.luanative.codec.PBCommandFactory;
import com.immomo.luanative.codec.protobuf.PBCreateCommand;
import com.immomo.luanative.codec.protobuf.PBEntryFileCommand;
import com.immomo.luanative.codec.protobuf.PBIPAddressCommand;
import com.immomo.luanative.codec.protobuf.PBMessageFactory;
import com.immomo.luanative.codec.protobuf.PBMoveCommand;
import com.immomo.luanative.codec.protobuf.PBReloadCommand;
import com.immomo.luanative.codec.protobuf.PBRemoveCommand;
import com.immomo.luanative.codec.protobuf.PBRenameCommand;
import com.immomo.luanative.codec.protobuf.PBUpdateCommand;

import java.util.concurrent.atomic.AtomicReference;


public class HotReloadServer implements IHotReloadServer {

    //
    //    ---------- 单例
    //
    private static final HotReloadServer ourInstance = new HotReloadServer();

    public static HotReloadServer getInstance() {
        return ourInstance;
    }

    private HotReloadServer() {

    }

    private volatile String entryFilePath;
    private volatile String relativeEntryFilePath;
    private volatile String params;

    //
    //    ---------- 开放接口
    //
    public void setupUSB(int usbPort) {
        throw new RuntimeException("Stub!");
    }

    private iHotReloadListener listener;
    public void setListener(iHotReloadListener l) {
        this.listener = l;
    }



    private void loop() throws InterruptedException {
        Transporter transporter = this.transporter;
        if(transporter == null) return;
        Transporter.Data data = new Transporter.Data();
        while (transporter.take(data)) {
            try {
                handleMessage(PBMessageFactory.getInstance(data.type, data.message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private enum State{
        IDLE,
        STARTING,
        RUNNING,
        STOPPING
    }


    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);
    private Thread worker;
    public void start() {
        if(state.compareAndSet(State.IDLE,State.STARTING)){
            worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        state.set(State.RUNNING);
                        if (listener != null)
                            listener.onConnected(NET_CONNECTION, "127.0.0.1", 8176);
                        loop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        state.set(State.IDLE);
                        if(listener != null)
                            listener.disconnecte(NET_CONNECTION,"127.0.0.1",8176,"");
                    }
                }
            });
            worker.start();
        }
    }

    public void stop() {
        if(state.compareAndSet(State.RUNNING,State.STOPPING)){
            Log.d("HotReloadServer","call stop");
            if(worker != null){
                worker.interrupt();
                worker = null;
            }
        }
    }

    public void log(String log) {
        writeMsg(PBCommandFactory.getLogCommand(log));
    }

    public void error(String error) {
        writeMsg(PBCommandFactory.getErrorCommand(error));
    }

    public String getEntryFilePath() {
        return entryFilePath;
    }

    public String getParams() {
        return params;
    }

    public void startNetClient(String ip, int port) {
        if(listener != null && transporter != null){
            listener.onConnected(NET_CONNECTION,ip,port);
        }
    }

    public void setSerial(String serial) {
        PBCommandFactory.Serial = serial;
    }

    public String getSerial() {
        return PBCommandFactory.Serial;
    }



    private void updateEntryFile(String entryFilePath, String relativeEntryFilePath, String params) {
        this.entryFilePath = entryFilePath;
        this.relativeEntryFilePath = relativeEntryFilePath;
        this.params = params;
    }

    private <T> void handleMessage(T msg) {
        if (listener == null) return;

        if (msg instanceof PBUpdateCommand.pbupdatecommand cmd) {
            // 文件更新
            listener.onFileUpdate(cmd.getFilePath(), cmd.getRelativeFilePath(), cmd.getFileData().newInput());
        } else if (msg instanceof PBEntryFileCommand.pbentryfilecommand cmd) {
            // 入口文件
            updateEntryFile(cmd.getEntryFilePath(), cmd.getRelativeEntryFilePath(), cmd.getParams());
        } else if (msg instanceof PBCreateCommand.pbcreatecommand cmd) {
            // 创建文件
            listener.onFileCreate(cmd.getFilePath(), cmd.getRelativeFilePath(), cmd.getFileData().newInput());
        } else if (msg instanceof PBRemoveCommand.pbremovecommand cmd) {
            // 删除文件
            listener.onFileDelete(cmd.getFilePath(), cmd.getRelativeFilePath());
        } else if (msg instanceof PBRenameCommand.pbrenamecommand cmd) {
            // 重命名文件 或 文件夹
            listener.onFileRename(cmd.getOldFilePath(), cmd.getOldRelativeFilePath(), cmd.getNewFilePath(), cmd.getNewRelativeFilePath());
        } else if (msg instanceof PBMoveCommand.pbmovecommand cmd) {
            // 移动文件 或 文件夹
            listener.onFileMove(cmd.getOldFilePath(), cmd.getOldRelativeFilePath(), cmd.getNewFilePath(), cmd.getNewRelativeFilePath());
        } else if (msg instanceof PBReloadCommand.pbreloadcommand cmd) {
            // 刷新
            listener.onReload(getEntryFilePath(), getRelativeEntryFilePath(), getParams() + "&hotReload_SerialNum=" + cmd.getSerialNum());
        } else if (msg instanceof PBIPAddressCommand.pbipaddresscommand cmd) {
            String ip = cmd.getMacIPAddress();
            listener.onIpChanged(ip);
        }
    }

    private String getRelativeEntryFilePath() {
        return relativeEntryFilePath;
    }



    private void writeMsg(Object msg){
        if(transporter != null){
            transporter.send(PBCommandConvert.getBodyType(msg),PBCommandConvert.convertFrom(msg));
        }
    }

    private volatile Transporter transporter;
    @Override
    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

}