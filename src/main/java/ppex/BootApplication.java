package ppex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ppex.server.socket.Server;

@SpringBootApplication
public class BootApplication implements ApplicationRunner {

    @Autowired
    Server server;

    public static void main(String[] args) throws Exception{
        SpringApplication.run(BootApplication.class,args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        server.startServer();
    }
}
