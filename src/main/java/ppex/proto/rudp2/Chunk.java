package ppex.proto.rudp2;

public class Chunk {

    private Chunk() {
        xmit = 0;
        dataLen = 0;
        cmd = 0;
        tot = 0;
        wnd = 0;
        ts = 0;
        sn = 0;
        una = 0;
        ts_resnd = 0;
        rto = 0;
        fastack = 0;
    }

    public static Chunk newChunk(byte[] data) {
        Chunk chunk = new Chunk();
        chunk.data = data;
        return chunk;
    }

    public byte[] data;
    public int dataLen;
    public byte cmd;
    public long msgid;
    public int tot;
    public int wnd;
    public long ts;
    public long sn;
    public long una;
    public long ts_resnd;
    public int rto;
    public int fastack;
    public int xmit;

}
