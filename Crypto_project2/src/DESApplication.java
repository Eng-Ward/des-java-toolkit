import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Scanner;

public class DESApplication {

    private static final Scanner scanner = new Scanner(System.in);
    private static DESKeyGeneration des = new DESKeyGeneration();


    private static String currentKeyHex = null;

    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleKeyExpansion();
                case "2" -> handleEncryptText();
                case "3" -> handleDecryptText();
                case "4" -> handleEncryptFile();
                case "5" -> handleDecryptFile();
                case "6" -> handleSendEmail();
                case "7" -> handleGenerateKey();
                case "8" -> {
                    System.out.println("\nGoodbye!");
                    running = false;
                }
                default -> System.out.println("[!] Invalid option. Please enter a number between 1 and 8.");
            }
        }
    }

    private static void printBanner() {
        System.out.println("=".repeat(55));
        System.out.println("   Enhanced DES Secure Communication System");
        System.out.println("=".repeat(55));
    }

    private static void printMenu() {
        System.out.println("\n" + "-".repeat(55));

        if (currentKeyHex != null)
            System.out.println("  Active Key : " + currentKeyHex.toUpperCase());
        else
            System.out.println("  Active Key : (none – please generate or enter a key)");

        System.out.println("-".repeat(55));
        System.out.println("  1. Key Expansion");
        System.out.println("  2. Encrypt Text");
        System.out.println("  3. Decrypt Text");
        System.out.println("  4. Encrypt File");
        System.out.println("  5. Decrypt File");
        System.out.println("  6. Send Ciphertext via Email");
        System.out.println("  7. Generate Random DES Key");
        System.out.println("  8. Exit");
        System.out.println("-".repeat(55));
        System.out.print("Select an option: ");
    }


    private static boolean promptAndLoadKey() {

        System.out.println("\n  Key source:");
        System.out.println("    1. Use active key (" +
                (currentKeyHex != null ? currentKeyHex.toUpperCase() : "none") + ")");
        System.out.println("    2. Enter key manually");
        System.out.println("    3. Load key from file");
        System.out.print("  Choice: ");

        String choice = scanner.nextLine().trim();
        String rawKey = null;

        switch (choice) {

            case "1" -> {
                if (currentKeyHex == null) {
                    System.out.println("[!] No active key. Please generate or enter one first.");
                    return false;
                }
                rawKey = currentKeyHex;
            }

            case "2" -> {
                System.out.print(
                        "  Enter key (hex 16 chars, e.g. 133457799BBCDFF1, or plain 8-char text): ");
                rawKey = scanner.nextLine().trim();
            }

            case "3" -> {
                System.out.print("  Enter key file path: ");
                String path = scanner.nextLine().trim();

                try {
                    rawKey = Files.readString(Path.of(path)).trim();
                } catch (IOException e) {
                    System.out.println("[!] Cannot read key file: " + e.getMessage());
                    return false;
                }
            }

            default -> {
                System.out.println("[!] Invalid choice.");
                return false;
            }
        }

        String hexKey = toHexKey(rawKey);

        if (hexKey == null)
            return false;

        currentKeyHex = hexKey;
        des.key56Bit(hexKey);

        System.out.println("  [OK] Key loaded: " + hexKey.toUpperCase());
        return true;
    }


    private static String toHexKey(String raw) {

        if (raw == null || raw.isEmpty()) {
            System.out.println("[!] Key must not be empty.");
            return null;
        }

        if (raw.matches("[0-9A-Fa-f]{16}")) {
            return raw.toUpperCase();
        }

        if (raw.length() <= 8) {

            byte[] keyBytes = new byte[8];
            byte[] textBytes = raw.getBytes();

            System.arraycopy(textBytes, 0, keyBytes, 0, textBytes.length);

            StringBuilder hex = new StringBuilder();

            for (byte b : keyBytes) {
                hex.append(String.format("%02X", b));
            }

            return hex.toString();
        }

        System.out.println(
                "[!] Invalid key: must be 16 hex characters OR up to 8 plain-text characters.");
        return null;
    }


    private static String promptData(String label) {

        System.out.println("\n  " + label + " source:");
        System.out.println("    1. Enter manually");
        System.out.println("    2. Load from file");
        System.out.print("  Choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {

            case "1" -> {
                System.out.print("  Enter " + label + ": ");
                return scanner.nextLine().trim();
            }

            case "2" -> {
                System.out.print("  Enter file path: ");
                String path = scanner.nextLine().trim();

                try {
                    return Files.readString(Path.of(path)).trim();
                } catch (IOException e) {
                    System.out.println("[!] Cannot read file: " + e.getMessage());
                    return null;
                }
            }

            default -> {
                System.out.println("[!] Invalid choice.");
                return null;
            }
        }
    }


    private static boolean isHex(String s) {
        return s != null
                && !s.isEmpty()
                && s.length() % 2 == 0
                && s.matches("[0-9A-Fa-f]+");
    }


    private static void handleKeyExpansion() {

        System.out.println("\n====== Key Expansion (Key Scheduling) ======");

        if (!promptAndLoadKey())
            return;

        System.out.println("\n  Key (hex) : " + currentKeyHex.toUpperCase());
        System.out.println("  " + "-".repeat(70));

        System.out.printf(
                "  %-8s  %-50s  %s%n",
                "Round",
                "Sub-key (Binary)",
                "Sub-key (Hex)");

        System.out.println("  " + "-".repeat(70));

        for (int i = 0; i < DESKeyGeneration.keys.size(); i++) {

            String binarySubKey = DESKeyGeneration.keys.get(i);

            long subKeyLong =
                    Long.parseUnsignedLong(binarySubKey, 2);

            String hexSubKey =
                    String.format("%012X", subKeyLong);

            System.out.printf(
                    "  K%-7d  %s  %s%n",
                    i + 1,
                    binarySubKey,
                    hexSubKey);
        }

        System.out.println("  " + "-".repeat(70));
        System.out.println(
                "  Total sub-keys generated: "
                        + DESKeyGeneration.keys.size());
    }

    private static void handleEncryptText() {

        System.out.println("\n====== Encrypt Text ======");

        if (!promptAndLoadKey())
            return;

        System.out.println("\n  Input format:");
        System.out.println("    1. Plain text");
        System.out.println("    2. Hexadecimal");
        System.out.print("  Choice: ");

        String fmt = scanner.nextLine().trim();

        String data = promptData("plaintext");

        if (data == null)
            return;

        String ciphertext;

        try {

            if ("2".equals(fmt)) {

                if (!isHex(data)) {
                    System.out.println("[!] Input is not valid hexadecimal.");
                    return;
                }

                ciphertext = des.encryptHex(data);

            } else {

                ciphertext = des.encryptText(data);
            }

        } catch (Exception e) {

            System.out.println("[!] Encryption error: " + e.getMessage());
            return;
        }

        System.out.println("\n  Ciphertext (hex): " + ciphertext);

        System.out.print("\n  Save ciphertext to file? (y/n): ");

        if ("y".equalsIgnoreCase(scanner.nextLine().trim())) {

            System.out.print("  Output file path: ");
            String outPath = scanner.nextLine().trim();

            try {

                Files.writeString(Path.of(outPath), ciphertext);

                System.out.println(
                        "  [OK] Ciphertext saved to: "
                                + outPath);

            } catch (IOException e) {

                System.out.println(
                        "[!] Could not save file: "
                                + e.getMessage());
            }
        }
    }


    private static void handleDecryptText() {

        System.out.println("\n====== Decrypt Text ======");

        if (!promptAndLoadKey())
            return;

        String data =
                promptData("ciphertext (hex)");

        if (data == null)
            return;

        if (!isHex(data)) {

            System.out.println(
                    "[!] Ciphertext must be a valid hex string.");
            return;
        }

        String plaintext;

        try {

            plaintext = des.decryptText(data);

        } catch (Exception e) {

            System.out.println(
                    "[!] Decryption error: "
                            + e.getMessage());
            return;
        }

        System.out.println(
                "\n  Recovered plaintext: "
                        + plaintext);

        System.out.print(
                "\n  Save plaintext to file? (y/n): ");

        if ("y".equalsIgnoreCase(scanner.nextLine().trim())) {

            System.out.print("  Output file path: ");
            String outPath = scanner.nextLine().trim();

            try {

                Files.writeString(
                        Path.of(outPath),
                        plaintext);

                System.out.println(
                        "  [OK] Plaintext saved to: "
                                + outPath);

            } catch (IOException e) {

                System.out.println(
                        "[!] Could not save file: "
                                + e.getMessage());
            }
        }
    }


    private static void handleEncryptFile() {

        System.out.println("\n====== Encrypt File ======");
        System.out.println(
                "  Supported types: .txt, .docx, .jpg (and any other file)");

        if (!promptAndLoadKey())
            return;

        System.out.print(
                "\n  Enter path to the file to encrypt: ");

        String filePath =
                scanner.nextLine().trim();

        File inputFile =
                new File(filePath);

        if (!inputFile.exists()
                || !inputFile.isFile()) {

            System.out.println(
                    "[!] File not found: "
                            + filePath);
            return;
        }

        String name =
                inputFile.getName().toLowerCase();

        if (!name.endsWith(".txt")
                && !name.endsWith(".docx")
                && !name.endsWith(".jpg")
                && !name.endsWith(".jpeg")
                && !name.endsWith(".png")
                && !name.endsWith(".pdf")) {

            System.out.print(
                    "  File type not in the standard list. Continue anyway? (y/n): ");

            if (!"y".equalsIgnoreCase(
                    scanner.nextLine().trim()))
                return;
        }

        try {

            File encryptedFile =
                    des.encryptFiles(filePath);

            System.out.println(
                    "\n  [OK] File encrypted successfully.");

            System.out.println(
                    "  Output : "
                            + encryptedFile.getAbsolutePath());

        } catch (IOException e) {

            System.out.println(
                    "[!] Encryption failed: "
                            + e.getMessage());
        }
    }

    private static void handleDecryptFile() {

        System.out.println("\n====== Decrypt File ======");

        if (!promptAndLoadKey())
            return;

        System.out.print(
                "\n  Enter path to the encrypted .des file: ");

        String filePath =
                scanner.nextLine().trim();

        File inputFile =
                new File(filePath);

        if (!inputFile.exists()
                || !inputFile.isFile()) {

            System.out.println(
                    "[!] File not found: "
                            + filePath);
            return;
        }

        if (!filePath.endsWith(".des")) {

            System.out.println(
                    "[!] Warning: file does not have a .des extension. It may not be a DES-encrypted file.");

            System.out.print(
                    "  Continue anyway? (y/n): ");

            if (!"y".equalsIgnoreCase(
                    scanner.nextLine().trim()))
                return;
        }

        try {

            File decryptedFile =
                    des.decryptFiles(filePath);

            System.out.println(
                    "\n  [OK] File decrypted successfully.");

            System.out.println(
                    "  Output : "
                            + decryptedFile.getAbsolutePath());

        } catch (IOException e) {

            System.out.println(
                    "[!] Decryption failed: "
                            + e.getMessage());
        }
    }


    private static void handleSendEmail() {

        System.out.println(
                "\n====== Send Ciphertext via Email ======");

        System.out.print(
                "  Sender Gmail address   : ");
        String senderEmail =
                scanner.nextLine().trim();

        System.out.print(
                "  Sender Gmail password  : ");
        String senderPassword =
                scanner.nextLine().trim();

        System.out.print(
                "  Recipient email address: ");
        String recipientEmail =
                scanner.nextLine().trim();

        if (senderEmail.isEmpty()
                || senderPassword.isEmpty()
                || recipientEmail.isEmpty()) {

            System.out.println(
                    "[!] All fields are required.");
            return;
        }

        EmailSender emailSender =
                new EmailSender(
                        senderEmail,
                        senderPassword);

        System.out.println("\n  What to send?");
        System.out.println(
                "    1. Ciphertext text message only");
        System.out.println(
                "    2. Ciphertext message + encrypted file attachment");

        System.out.print("  Choice: ");
        String choice =
                scanner.nextLine().trim();

        System.out.print("  Email subject: ");
        String subject =
                scanner.nextLine().trim();

        System.out.print(
                "  Ciphertext (hex) to include in email body: ");

        String ciphertext =
                scanner.nextLine().trim();

        if ("2".equals(choice)) {

            System.out.print(
                    "  Path to encrypted .des file to attach: ");

            String attachPath =
                    scanner.nextLine().trim();

            if (!new File(attachPath).exists()) {

                System.out.println(
                        "[!] Attachment file not found.");
                return;
            }

            emailSender.sendCiphertextEmailWithAttachment(
                    recipientEmail,
                    subject,
                    "Encrypted message:\n" + ciphertext,
                    attachPath);

        } else {

            emailSender.sendCiphertextEmail(
                    recipientEmail,
                    subject,
                    "Encrypted message:\n" + ciphertext);
        }
    }

    private static void handleGenerateKey() {

        System.out.println("\n====== Generate Random DES Key ======");

        byte[] keyBytes = new byte[8];
        new SecureRandom().nextBytes(keyBytes);

        StringBuilder hexKey = new StringBuilder();

        for (byte b : keyBytes) {
            hexKey.append(String.format("%02X", b));
        }

        currentKeyHex = hexKey.toString();

        des.key56Bit(currentKeyHex);

        System.out.println(
                "\n  Generated DES key (hex): "
                        + currentKeyHex);

        System.out.println(
                "  Key schedule loaded and ready to use.");

        System.out.println(
                "\n  TIP: Save this key securely before encrypting data.");
    }
}