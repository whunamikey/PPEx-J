package ppex.pack;

public class Endpoint {
    public Endpoint(String endpointIp, int port) {
        this.endpointIp = endpointIp;
        this.port = port;
    }

    private String endpointIp;
    private int port;
    public boolean ep_equal(Endpoint src,Endpoint dst){
        return src.endpointIp == dst.endpointIp
                && src.port == dst.port;
    }
    public String ep_toString(Endpoint ep){
        StringBuilder sb = new StringBuilder();
        return sb.append(ep.endpointIp).append(":").append(ep.port).toString();
    }

    public Endpoint ep_fromString(String str){
        String[] subStrs = str.split(":");
        if (subStrs.length < 2){
            return null;
        }else{
            return ep_fromPair(subStrs[0],Integer.valueOf(subStrs[1]));
        }
    }
    public Endpoint ep_fromPair(String ip,int port){
        return new Endpoint(ip,port);
    }

}
