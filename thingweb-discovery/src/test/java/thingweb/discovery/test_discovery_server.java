package thingweb.discovery;

import de.thingweb.discovery.repository.RepositoryClient;

public class test_discovery_server {

	
	public static void main(String args[]) {
		
		RepositoryClient tdr = new RepositoryClient("localhost", 3030);
		
		try {
			tdr.tdSearch("Lamp");
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
