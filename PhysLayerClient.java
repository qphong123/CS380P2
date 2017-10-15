import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.TreeMap;

public class PhysLayerClient{

	public static void main(String[] args) throws IOException{
		try (Socket socket = new Socket("18.221.102.182",38002)){
			System.out.println("Connected to server.");
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			double baseline = 0.0;
			for(int i = 0; i < 64; i++){
				int signal = in.read();
				baseline += signal;
			}
			baseline /= 64;
			System.out.printf("Baseline established from preamble: %.2f\n", baseline);
			TreeMap<String, String> fourBfiveB = new TreeMap<>();
			initTable(fourBfiveB);
			String[] halfBytes = new String[64];
			boolean lastSignal = false;	//false is LOW, true is HIGH
			for(int i = 0; i < 64; i++){
				String fiveBits = "";
				for(int j = 0; j < 5; j++){
					boolean thisSignal = in.read()>baseline;
					fiveBits += (lastSignal==thisSignal)? "0":"1";
					lastSignal = thisSignal;
				}
//				System.out.print(fiveBits +"\t");
				halfBytes[i] = fourBfiveB.get(fiveBits);
//				System.out.println(halfBytes[i]);
			}
			System.out.print("Received 32 bytes: ");
			byte[] bytes = new byte[32];
			for(int i = 0; i < 32; i++){
				String firstHalf = halfBytes[2*i];
				String secondHalf = halfBytes[2*i+1];
				System.out.printf("%X", Integer.parseInt(firstHalf, 2));
				System.out.printf("%X", Integer.parseInt(secondHalf, 2));
				String wholeByte = firstHalf + secondHalf;
				bytes[i] = (byte)Integer.parseInt(wholeByte, 2);
			}
			System.out.println();
			out.write(bytes);
			if(in.read()==1)
				System.out.println("Response good.");
		}
		System.out.println("Disconnected from server.");
	}

	public static void initTable(TreeMap<String,String> table){
		table.put("11110","0000");
		table.put("01001","0001");
		table.put("10100","0010");
		table.put("10101","0011");
		table.put("01010","0100");
		table.put("01011","0101");
		table.put("01110","0110");
		table.put("01111","0111");
		table.put("10010","1000");
		table.put("10011","1001");
		table.put("10110","1010");
		table.put("10111","1011");
		table.put("11010","1100");
		table.put("11011","1101");
		table.put("11100","1110");
		table.put("11101","1111");
	}

}