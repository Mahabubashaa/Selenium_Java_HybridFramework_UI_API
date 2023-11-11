package utility;


import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

public class PasswordEncrypter {

	public static String encode(String text){
		String encodedPassword = "";
		byte[] passwordencoded = Base64.encodeBase64(text.getBytes());
		encodedPassword = new String(passwordencoded);
		return encodedPassword;
	}
	
	public static String decode(String password){
		String decodedPassword = "";
		byte[] password_decoded = Base64.decodeBase64(new String(password));
		decodedPassword = new String(password_decoded);
		return decodedPassword;
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {

		System.out.println("Enter Your Password \n");
		String password = new Scanner(System.in).next();
		
		System.out.println("Encrypted password is " + encode(password));
		System.out.println("Decrypted password is " + decode(password));

	}

	
}
