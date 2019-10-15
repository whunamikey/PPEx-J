package ppex.server.myturn;

import ppex.proto.entity.through.connect.ConnectType;

import java.util.List;

public class ConnectResult {
    private List<ConnectType> results;

    public List<ConnectType> getResults() {
        return results;
    }

    public void setResults(List<ConnectType> results) {
        this.results = results;
    }
}
