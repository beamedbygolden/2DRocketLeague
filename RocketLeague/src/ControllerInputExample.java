import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Component;

public class ControllerInputExample {
    public static void main(String[] args) {
        // Point Java directly to your current project root where the DLLs live
        System.setProperty("net.java.games.input.librarypath", System.getProperty("user.dir"));

        // Retrieve all available controllers (gamepads, joysticks, etc.)
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        Controller pad = null;
        
        for (int i = 0; i < controllers.length; i++) {
            if (controllers[i].getType() == Controller.Type.GAMEPAD || controllers[i].getType() == Controller.Type.STICK) {
                pad = controllers[i];
                System.out.println("Success! Controller found: " + pad.getName());
                break;
            }
        }

        if (pad == null) {
            System.out.println("No controller detected! Make sure it's plugged in/connected via Bluetooth before clicking run.");
            return;
        }

        // Loop to continuously pull inputs
        while (true) {
            pad.poll(); // Update controller state
            Component[] components = pad.getComponents();
            
            for (int i = 0; i < components.length; i++) {
                Component component = components[i];
                float data = component.getPollData();

                if (component.isAnalog()) {
                    // Ignore tiny stick drift values (< 0.15) and unpressed default trigger values (-1.0)
                    if (Math.abs(data) > 0.15f && data != -1.0f) {
                        System.out.println(component.getName() + " Axis Value: " + data);
                    }
                } else {
                    // Digital buttons (0.0 is released, 1.0 is pressed)
                    if (data == 1.0f) {
                        System.out.println(component.getName() + " was pressed.");
                    }
                }
            }

            try {
                Thread.sleep(50); // Pause briefly to prevent console spam
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
