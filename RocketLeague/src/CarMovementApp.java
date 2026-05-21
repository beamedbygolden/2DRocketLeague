import de.gurkenlabs.input4j.*;
public class CarMovementApp {
	public static void main(String[] args) throws Exception {

        try (var devices = InputDevices.init()) {

            System.out.println(devices.getAll());
        }
    }

}
