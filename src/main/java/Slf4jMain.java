import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jMain {

    static Logger log = LoggerFactory.getLogger(Slf4jMain.class);


    public static void main(String[] args) {

        log.info("test Log");
        log.error("My error", new RuntimeException("foooo"));

    }
}
