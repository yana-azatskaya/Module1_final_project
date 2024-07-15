import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final ArrayList<Character> ALPHABET = new ArrayList<>(List.of('a', 'b', 'c', 'd', 'е', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'Е', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '?', '!', ',', '.', '/', '№'));
    private static final ArrayList<String> commonWord = new ArrayList(List.of("and", "she", "he", "it", "yes", "no", "so", "how", "is", "what", "where", "because", "when", "in", "on", "to"));


    public static void main(String[] args) {
        callMainMenu();
    }

    public static void callMainMenu() {
        try {
            int menuPoint = mainMenu();

            switch (menuPoint) {
                case 1 -> encryptFile();
                case 2 -> decryptFile();
                case 3 -> bruteForce();
                case 4 -> programClose();
                default -> throw new InvalidMenuPoint("Your input is incorrect, please, enter a number from 1 to 4");

            }
        } catch (InvalidMenuPoint e) {
            System.err.println(e.getMessage());
            callMainMenu();
        }
    }

    public static int mainMenu() throws InvalidMenuPoint {
        System.out.println(""" 
                Please choose one element from the menu:
                1 - encrypt the file;
                2 - decrypt the file;
                3 - crack the encryption key;
                4 - close the program""");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (ifInt(input)) {
            return Integer.parseInt(input);
        } else throw new InvalidMenuPoint("Your input is not a number, please, enter a number from 1 to 4");

    }

    public static int attentionMenu() throws InvalidMenuPoint {

        System.out.println(("""
                Please choose one element from the menu:
                1 - yes, proceed;
                2 - no, close the program;
                """));
        Scanner scanner = new Scanner(System.in);
        String stringMenuInput = scanner.nextLine();
        int menuInput;

        if (ifInt(stringMenuInput)) {
            menuInput = Integer.parseInt(stringMenuInput);
            if (menuInput != 1 & menuInput != 2) {
                throw new InvalidMenuPoint("Please enter a number from 1 to 2");
            }
        } else {
            throw new InvalidMenuPoint("Your input is not a number, please enter a number from 1 to 2");
        }
        return menuInput;
    }

    public static boolean ifInt(String menuPoint) {
        try {
            Integer.parseInt(menuPoint);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int receiveKey() throws KeyIsIncorrect {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a key from 1 to 10");
        int key;
        String inputKey = scanner.nextLine();
        if (ifInt(inputKey)) {
            key = Integer.parseInt(inputKey);
            if (key < 1 || key > 10) {
                throw new KeyIsIncorrect("Your key is incorrect, please enter a key from 1 to 10");
            }
        } else throw new KeyIsIncorrect("Your key is not a number, please enter a key from 1 to 10");
        return key;
    }

    public static StringBuilder coder(int key, Path input) {
        StringBuilder content = new StringBuilder();
        try {
            List<String> rows = new ArrayList<>(Files.readAllLines(input));

            int newIndex;
            int rowCount = 0;
            for (String row : rows) {
                char[] rowAsChar = row.toCharArray();
                rowCount++;
                for (char c : rowAsChar) {
                    if (ALPHABET.contains(c)) {
                        newIndex = ALPHABET.indexOf(c) + key;
                        if (newIndex >= ALPHABET.size()) {
                            newIndex = newIndex - (ALPHABET.size() - 1);
                            content.append(ALPHABET.get(newIndex - 1));
                        } else if (newIndex < 0) {
                            newIndex = ALPHABET.size() + newIndex;
                            content.append(ALPHABET.get(newIndex));
                        } else {
                            content.append(ALPHABET.get(newIndex));
                        }
                    } else content.append(c);


                }
                if (rowCount != rows.size()) {
                    content.append(System.lineSeparator());
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return content;
    }

    public static void encryptFile() {

        try {
            int key = receiveKey();
            Path filePath = receiveFilePath("input");
            Path outputPath = receiveFilePath("output");
            writerToFile(outputPath, coder(key, filePath));
        } catch (KeyIsIncorrect e) {
            System.err.println(e.getMessage());
            encryptFile();
        }
    }

    public static void decryptFile() {
        Path decryptPath = receiveFilePath("decryption");
        Path resultFile = receiveFilePath("result");
        try {
            int key = receiveKey() * (-1);
            writerToFile(resultFile, coder(key, decryptPath));
        } catch (KeyIsIncorrect e) {
            System.err.println(e.getMessage());
            decryptFile();
        }
    }

    public static void bruteForce() {
        Path fileForBruteForcing = receiveFilePath("for brute forcing");
        Path result = receiveFilePath("result");
        boolean success = false;
        StringBuilder finalResult = new StringBuilder();
        for (int i = -10; i <= -1; i++) {
            finalResult = coder(i, fileForBruteForcing);
            if (writerToArray(finalResult)) {
                writerToFile(result, finalResult);
                success = true;
                break;
            }
        }
        if (!success) {
            System.out.println("We couldn't find the right key");
        }
    }

    public static void clearFile(Path path) {
        try
        {Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING);}
         catch (IOException e) {
             System.err.println(e.getMessage());
         }
    }

    public static void writerToFile(Path path, StringBuilder content) {
        System.out.println("Attention! If the target file contains any data, it will be removed completely!");
        try {
            if (attentionMenu() == 1) {
                clearFile(path);
                Files.writeString(path, content, StandardOpenOption.APPEND);
                System.out.println("Program operation has been completed successfully, your data has been recorded");
            } else programClose();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (InvalidMenuPoint e) {
            System.err.println(e.getMessage());
            writerToFile(path, content);
        }
    }

    public static boolean writerToArray(StringBuilder rowOutput) {
        String[] words = rowOutput.toString().split(" ");
        boolean isContainWord = false;
        for (String word : words) {
            if (commonWord.contains(word)) {
                isContainWord = true;
                break;
            }
        }
        return isContainWord;
    }

    public static Path receiveFilePath(String word) {

        Scanner scanner = new Scanner(System.in);
        String filePath;
        boolean fileExists = false;
        Path finalFilePath = null;
        while (!fileExists) {
            System.out.printf("Please enter a path to the file for %s" + "\n", word);
            filePath = scanner.nextLine();
            finalFilePath = Path.of(filePath);

            if (Files.exists(finalFilePath) && !filePath.isEmpty()) {
                fileExists = true;
            } else System.out.println("Your input is incorrect, please, try again");
        }
        return finalFilePath;
    }

    public static void programClose() {
        System.out.println("Good bye!");
    }
}
