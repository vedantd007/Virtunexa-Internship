import java.util.Scanner;

public class BinaryToDecimalConverter {
    
    
    public static int binaryToDecimal(String binaryString) {
        int decimal = 0;
        int length = binaryString.length();

        for (int i = 0; i < length; i++) {
            
            char currChar = binaryString.charAt(i);

            int digit = Character.getNumericValue(currChar);

            int power = length-i-1;
            decimal += digit * Math.pow(2, power);

        }
        return decimal;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a binary number: ");
        String binaryString = scanner.nextLine();

        int decimal = binaryToDecimal(binaryString);
        System.out.println("The decimal equivalent of " + binaryString + " is " + decimal);
        
        scanner.close();
    }

}