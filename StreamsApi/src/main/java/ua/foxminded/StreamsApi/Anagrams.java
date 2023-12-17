//package ua.foxminded.anagrams;
//
//import java.util.*;
//
//public class Anagrams {
//	public static void main(String[] args) {
//
//		try (Scanner scanner = new Scanner(System.in)) {
//			System.out.println("Please write a word");
//			String stringInput = scanner.nextLine();
//			Anagrams anagrams = new Anagrams();
//			System.out.println("Original String : " + stringInput);
//			System.out.println("Reversed String : " + anagrams.reverseStringSentence(stringInput));
//		}
//	}
//
//	public String reverseStringSentence(String input) {
//		if (input == null || input.equals("")) {
//			throw new IllegalArgumentException("argument cannot be null");
//		}
//		String[] outputString = input.split(" ");
//
//		for (int i = 0, j = outputString.length; i < j; i++) {
//			outputString[i] = reverseWord(outputString[i]);
//		}
//		return String.join(" ", outputString);
//	}
//
//	private String reverseWord(String stringInput) {
//		char[] chars = stringInput.toCharArray();
//		int left = 0;
//		int right = stringInput.length() - 1;
//		while (left < right) {
//			if (isAlphabet(chars[left]) && isAlphabet(chars[right])) {
//				char temperateChar = chars[left];
//				chars[left] = chars[right];
//				chars[right] = temperateChar;
//				left++;
//				right--;
//			} else if (!isAlphabet(chars[left])) {
//				left++;
//			} else {
//				right--;
//			}
//		}
//		return new String(chars);
//	}
//
//	private boolean isAlphabet(char ch) {
//		return ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'));
//	}
//
//}
package ua.foxminded.StreamsApi;

import java.time.Duration;
import java.time.LocalTime;


public class Anagrams {
	public static void main(String[] args) {

		
		String time1 = "12:19:25.111";
		String time2 = "12:20:56.211";

		long seconds = Duration.between(LocalTime.parse(time1), LocalTime.parse(time2)).toMillis();
		System.out.println(seconds);
		System.out.println(seconds/1000/60 + ":" +     
		 ( seconds/1000-((seconds/1000/60)*60)) + "." +
				(seconds)    );
		
	}
}
