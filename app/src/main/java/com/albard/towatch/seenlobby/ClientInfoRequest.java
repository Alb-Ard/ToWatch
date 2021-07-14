package com.albard.towatch.seenlobby;

public class ClientInfoRequest {
    public static final int UPDATE = 0;
    public static final int REMOVE = 1;
    public static final int REQUEST = 2;

    public int id;
    public String name;
    public int mode;
}
