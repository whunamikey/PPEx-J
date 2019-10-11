package ppex.proto.entity.through;

public class RECVINFO {
    public RECVINFO(int type,String recvinfos){
        this.type = type;
        this.recvinfos = recvinfos;
    }
    public int type;
    public String recvinfos;

    @Override
    public String toString() {
        return "RECVINFO{" +
                "type=" + type +
                ", recvinfos='" + recvinfos + '\'' +
                '}';
    }
}
