import java.util.Scanner;
import java.util.Random;

public class Task1 {
    public static void main(String[]args){
        Scanner sc=new Scanner(System.in);
        Random rd= new Random();
        int Score = 0;
        int Totalquestions = 10;
        System.out.println("Welcome to Quiz");

        for(int i = 0; i<= Totalquestions; i++){
           int a = rd.nextInt(15);
           int b = rd.nextInt(15);
           char operator = "+-*".charAt(rd.nextInt(3));

           int correct = switch (operator){
            case '+' -> a+b; 
            case '-' -> a-b;
            case '*' -> a*b;
            default -> 0;
           };
           System.out.printf("Example %d : %d %c %d = ? " , i + 1 , a , operator, b);
           int answer = sc.nextInt();

           if(answer == correct){
            System.out.println("Your answer is CORRECT");
            Score++;
           }
           else{
            System.out.println("Your answer is INCORRECT " + correct);
           }
        }
        System.out.printf("Quiz Finished! Your Score is %d/%d%n " , Score , Totalquestions);
        sc.close();
    }
    
}
