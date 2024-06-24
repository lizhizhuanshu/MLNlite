package com.immomo.luanative.hotreload;

public interface Transporter {
    class Data{
        public int type;
        public byte[] message;
    }
    void send(int type,byte[] message);
    boolean take(Data out)throws InterruptedException;
}
