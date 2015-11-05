import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import java.io.IOException;

class LeapListener extends Listener 
{
	boolean correcto = false;
	public void onInit(Controller controller)
	{
		System.out.println("Initialized.");
	}
	
	public void onConnect(Controller controller)
	{
		System.out.println("Connected to motion sensor.");
		//controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		//controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		//controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
		System.out.println("Hola, sigue las instrucciones para colocar tu mano en la posición correcta.");
	}
	
	public void onDisconnect(Controller controller)
	{
		System.out.println("Motion sensor disconnected.");
	}
	
	public void onExit(Controller controller)
	{
		System.out.println("Exited.");
	}
	
	public void onFrame(Controller controller)
	{
		Frame frame = controller.frame(0);
		
		for (Hand hand: frame.hands())
		{
			String handType = hand.isLeft() ? "Left hand" : "Right hand";
			//System.out.println(handType + " , id: " + hand.id() + " , palm position: " + hand.palmPosition(). );
			
			Vector normal = hand.palmNormal();
			Vector direction = hand.direction(); // dirección desde la palma a los dedos
			
			if (!correcto)
			{
				if (hand.palmPosition().getX() < -10)
				{
					System.out.println("Mueve tu mano hacia la derecha horizontalmente.");
				}
				else if(hand.palmPosition().getX() > 40)
				{
					System.out.println("Mueve tu mano hacia la izquierda horizontalmente.");
				}
				else if(hand.palmPosition().getY() < 110)
				{
					System.out.println("Mueve tu mano hacia arriba verticalmente.");
				}
				else if(hand.palmPosition().getY() > 170)
				{
					System.out.println("Mueve tu mano hacia abajo verticalmente.");
				}
				else if(hand.palmPosition().getZ() > 30)
				{
					System.out.println("Mueve tu mano hacia delante horizontalmente.");
				}
				else if(hand.palmPosition().getZ() < -20)
				{
					System.out.println("Mueve tu mano hacia atrás horizontalmente.");
				}else
				{
					System.out.println("Estás en la posición CORRECTA, ahora realiza un círculo con tu mano");
				}
			}
			//System.out.println("Pitch: " + Math.toDegrees(direction.pitch()) + ", Roll: " + Math.toDegrees(normal.roll()) + ", Yaw: " + Math.toDegrees(direction.yaw()));		
		} 
		
		GestureList gestures = frame.gestures();
		for (int i = 0; i < gestures.count(); i++)
		{
			Gesture gesture = gestures.get(i);
			
			switch (gesture.type())
			{
				case TYPE_CIRCLE:
					CircleGesture circle = new CircleGesture(gesture);
					
					String clockwiseness;
					if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4 )
					{
						clockwiseness = "clockwise";
					}
					else
					{
						clockwiseness = "counter-clockwise";
					}
					
					double sweptAngle = 0;
					if (circle.state() != State.STATE_START)
					{
						CircleGesture previous = new CircleGesture(controller.frame(1).gesture(circle.id()));
						sweptAngle = (circle.progress() - previous.progress()) * 2 * Math.PI;
					}
					
					System.out.println("Has realizado un círculo!"); 
					correcto = true;
					break;
				
				case TYPE_SWIPE:
					SwipeGesture swipe = new SwipeGesture(gesture);
					System.out.println("Swipe id: " + swipe.id()
								     + ", state: " + swipe.state()
								     + ", swipe position: " + swipe.position()
								     + ", direction: " + swipe.direction()
								     + ", speed: " + swipe.speed());
					break;
					
				case TYPE_SCREEN_TAP:
					ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
					System.out.println("ScreenTap id: " + screenTap.id()
								     + ", state: " + screenTap.state()
								     + ", position: " + screenTap.position()
								     + ", direction: " + screenTap.direction());
					break;
					
				case TYPE_KEY_TAP:
					KeyTapGesture keyTap = new KeyTapGesture(gesture);
					System.out.println("KeyTap id: " + keyTap.id()
								     + ", state: " + keyTap.state()
								     + ", position: " + keyTap.position()
								     + ", direction: " + keyTap.direction());
					break;
					
				default:
					System.out.println("Unknow gesture.");
					break;
			}
		}
	}
}

public class LeapController 
{
	public static void main(String[] args) 
	{
		LeapListener listener = new LeapListener();
		Controller controller = new Controller();
		
		controller.addListener(listener);
		
		System.out.println("Press enter to quit");
		
		try
		{
			System.in.read();
		} 
		catch	(IOException e)
		{
			e.printStackTrace();
		}
		controller.removeListener(listener);

	}

}
