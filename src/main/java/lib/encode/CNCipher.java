package lib.encode;
import java.util.Scanner;

public class CNCipher {
	
	private static String convert2Ascii(String binary) {
		String str = "";
		for (int i = 0; i < binary.length()/7; i++) {
			int a = Integer.parseInt(binary.substring(7*i,(i+1)*7),2);
			str += (char)(a);
		}
		return str;
	}
	
	private static StringBuilder convert2Binary(String message) {
		/* Beginning of Source - Used from :
        http://stackoverflow.com/questions/917163/convert-a-string-like-testing123-to-binary-in-java
        */
        byte[] bytes = message.getBytes();
        StringBuilder binary = new StringBuilder();
        
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 7; i++) {       //7 bits instead of 8 as in source
                binary.append((val & 64) == 0 ? 0 : 1);     //2^(7-1)=64 instead of 128
                val <<= 1;
            }
            //binary.append(' ');  //used only in source, no need here
        }
        /* End of Source
         http://stackoverflow.com/questions/917163/convert-a-string-like-testing123-to-binary-in-java
        */
        return binary;
	}
	
	public static String decrypt(String unary) {
		String binary = "";
		String[] split = unary.split(" ");
		for (int i=0; i<split.length; i+=2) {
			String bit = split[i];
			int count = split[i+1].length();
			binary += multiply((bit.equals("0"))?'1':'0',count);
		}
		return convert2Ascii(binary);
	}

	public static String encrypt(String message) {
		StringBuilder binary = convert2Binary(message);
		
        //System.err.println("'" + message + "' to binary: " + binary);
        
        boolean one = false,
            previous = one;
        String unary = "";
        int successions = 0;    //successions will start incremented since it jumps to else
        
        //System.err.println(binary.toString().toCharArray());
        
        for (int i=0; i <binary.length(); i++) {
            char bit = binary.toString().toCharArray()[i];
            //System.err.print(i+": ");
            one = (bit=='1') ? true : false;
            if (i!=0 && previous!=one) {        //first time they have to be different lol.. skip that
                String first = ((previous) ? "0": "00");        //first series
                String second = multiply('0',successions);      //second series
                unary += first+" "+second+" ";
                successions = 1;
            }
            else 
                successions++;
            previous = one;
            //System.err.println(unary);
        }
        
        //Last series won't be added but succcessions will still hold the number of occcurences
        String first = ((previous) ? "0": "00");
        String second = multiply('0',successions);
        unary += first+" "+second;
        
        return unary;
	}
    
    private static String multiply(char letter, int times) {
        String result = "";
        while (times-->0) 
            result += ""+letter;
        return result;
    }
    
    public static void main(String args[]) {
		String MESSAGE = "";
		Scanner in;
		while (true) {
			in = new Scanner(System.in);
			MESSAGE = in.nextLine();
			if (MESSAGE.equals("exit")) {
				in.close();
				System.exit(0);
			}
			String unary = encrypt(MESSAGE);
			System.out.println(unary);
		}
    }
}
