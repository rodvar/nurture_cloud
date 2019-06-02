package com.nurturecloud;

import java.util.Scanner;

public class App {

    private static final String QUIT = "Q";

    public static void main(String[] args) {
        Scanner command = new Scanner(System.in);
        boolean running = true;
        while(running) {

            System.out.print("Welcome to Nurture Cloud Suburb Search. To Exit, enter q (quit) as a suburb name\n\n");

            System.out.print("Please enter a suburb name: ");
            String suburbName = command.nextLine();

            if (suburbName.equalsIgnoreCase(QUIT))
                running = false;
            else {
                if (invalid(suburbName, false)) {
                    System.out.print("\nERROR: Invalid Suburb Name. \n\n");
                    continue;
                }

                System.out.print("Please enter the postcode: ");
                String postcode = command.nextLine();
                if (invalid(postcode, true)) {
                    System.out.print("\nERROR: Invalid PostCode Number. \n\n");
                    continue;
                }

                System.out.println(String.format("Nothing foudsffnd for %s, %s!!\n", suburbName, postcode));
            }
        }
        command.close();
    }

    private static boolean invalid(String string, boolean isNumber) {
        return string == null || string.isEmpty() || (isNumber ? isText(string) : isNumber(string));
    }

    private static boolean isNumber(String string) {
        return string.matches("[0-9]+");
    }

    private static boolean isText(String string) {
        return string.matches("[a-zA-Z]+");
    }
}
