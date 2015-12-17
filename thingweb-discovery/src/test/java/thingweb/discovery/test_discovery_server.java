package thingweb.discovery;

public class test_discovery_server {

	
	public static void main(String args[]) {
		
		TDRepository tdr = new TDRepository("localhost", 3030);
		
		try {
			tdr.tdSearch("Lamp");
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
