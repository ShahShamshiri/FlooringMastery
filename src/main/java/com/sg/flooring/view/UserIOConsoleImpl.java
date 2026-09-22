package com.sg.flooring.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UserIOConsoleImpl implements UserIO {

    private final Scanner console = new Scanner(System.in);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public void print(String message) {System.out.println(message);}

    @Override
    public String readString(String prompt) {
        System.out.print(prompt);
        return console.nextLine();
    }

    @Override
    public int readInt(String prompt) {

        while (true) {
            String input = readString(prompt);

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                print("Please enter an integer");
            }
        }
    }

    @Override
    public int readInt(String prompt, int min, int max) {

        while (true) {
            int value = readInt(prompt);

            if (value >= min && value <= max) {
                return value;
            }

            print("Please enter a number between " + min + " and " + max + ".");
        }
    }

    @Override
    public BigDecimal readBigDecimal(String prompt) {

        while (true) {

            String input = readString(prompt);

            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                print("Enter a valid decimal number");
            }
        }
    }

    @Override
    public LocalDate readLocalDate(String prompt) {

        while (true) {

            String input = readString(prompt);

            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                print("Enter the date in MM/DD/YYYY format.");
            }
        }
    }

    @Override
    public boolean readYesNo(String prompt) {

        while (true) {

            String input = readString(prompt).trim();

            if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("Yes")) {
                return true;
            }

            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("No")) {
                return false;
            }

            print("Please enter Y or N.");
        }
    }
}