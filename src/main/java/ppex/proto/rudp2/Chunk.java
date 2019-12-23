package ppex.proto.rudp2;

public class Chunk {

    private Chunk(){}

    public static Chunk newChunk(byte[] data){
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
    public long ackMask;
    public int ackMaskSize;

}
