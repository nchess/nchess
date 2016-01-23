import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		String fname = "sample-map.json";
		System.out.println("Vaguely validating file: " + fname);
		
		System.out.printf("File path: %s\n", Main.class.getResource(fname));
		
		VagueReaderValidator validator = new VagueReaderValidator();
		System.out.print("Result: ");
		if(validator.validate(Main.class.getResourceAsStream(fname)))
			System.out.println("valid!");
		else
			System.out.println("invalid!");
	}
	
	public static void Wait() {
		System.out.println("Press enter... ");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}