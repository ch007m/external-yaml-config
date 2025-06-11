import io.halkyon.ClientCommands;
import io.halkyon.SharedApplication;

//@QuarkusMain(name="client")
public class ClientApplication {
    public static void main(String[] args) {
        SharedApplication.main(ClientCommands.class, args);
    }
}