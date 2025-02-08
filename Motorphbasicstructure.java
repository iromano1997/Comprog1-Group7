/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package motorphbasicstructure;
import java.util.Scanner;
/**
 *
 * @author jonad
 */
public class Motorphbasicstructure {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
     String x;

Scanner scan = new Scanner(System.in);
System.out.println("Welcome to MotorPH Employee Portal");
System.out.print("Enter Employee ID: ");
x= scan.nextLine();

System.out.println("Showing details for "+ x);
System.out.println("Name: Garcia, Manuel III");
System.out.println("Birthday: 10/11/1983");

System.out.print("Enter Hours Worked for the week: ");
        String y = scan.nextLine();

System.out.println("Showing details for "+ y + " hours");
System.out.println("Gross Weekly Salary: P21,428.40");
System.out.println("Net Weekly Salary: P14,854.54");


// TODO code application logic here
    }
}
