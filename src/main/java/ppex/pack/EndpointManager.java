package ppex.pack;

import java.util.LinkedList;
import java.util.List;

public class EndpointManager {
    private List<Endpoint> endpoints;

    public EndpointManager() {
        endpoints = new LinkedList<>();
    }

    public boolean addEndpoint(Endpoint endpoint){
        if (endpoints.contains(endpoint)){
            return true;
        }else{
            endpoints.add(endpoint);
            return true;
        }
    }

    public boolean removeEndpoint(Endpoint endpoint){
        return endpoints.remove(endpoint);
    }

    public int getEndpointSize(){
        return endpoints.size();
    }

    public void printAllEndpoints(){
        endpoints.stream().forEach(ep ->{
            System.out.print(ep.ep_toString(ep) + "->");
        });
    }

}
