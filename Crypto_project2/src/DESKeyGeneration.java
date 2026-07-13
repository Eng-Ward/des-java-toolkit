import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


//key for trying: 133457799BBCDFF1
public class DESKeyGeneration {
    static int[] permutedChoice = {57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19,
            11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21,
            13, 5, 28, 20, 12, 4};
    static int[] permutedChoice2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };
    static int[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9,  1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    static int[] FP = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41,  9, 49, 17, 57, 25
    };

    static int[] expansionTable = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    static int[] straightPermutationTable = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };

    public class SBoxes {

        static int[][][] S = {

                { // S1
                        {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                        {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                        {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                        {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
                },

                { // S2
                        {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                        {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                        {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                        {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
                },

                { // S3
                        {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                        {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                        {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                        {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
                },

                { // S4
                        {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                        {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                        {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                        {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
                },

                { // S5
                        {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                        {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                        {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                        {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
                },

                { // S6
                        {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                        {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                        {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                        {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
                },

                { // S7
                        {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                        {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                        {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                        {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
                },

                { // S8
                        {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                        {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                        {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                        {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
                }
        };
    }

    static int[] rounds = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    static ArrayList<String> keys = new ArrayList<>();
    static String key;

    public void key56Bit(String hexKey) {
        keys.clear();
        String binaryKey = new BigInteger(hexKey, 16).toString(2);
        binaryKey = String.format("%64s", binaryKey).replace(' ', '0');
        StringBuilder permutedKeyBits = new StringBuilder();

        for (int i = 0; i < permutedChoice.length; i++) {
            int pos = permutedChoice[i];
            permutedKeyBits.append(binaryKey.charAt(pos - 1));
        }
        this.key = permutedKeyBits.toString();

        String leftHalf = this.key.substring(0, this.key.length() / 2);
        String rightHalf = this.key.substring(this.key.length() / 2, this.key.length());

        for (int i = 0; i < rounds.length; i++) {
            if (rounds[i] == 1) {
                leftHalf = leftHalf.substring(1) + leftHalf.charAt(0);
                rightHalf = rightHalf.substring(1) + rightHalf.charAt(0);
            } else if (rounds[i] == 2) {
                leftHalf = leftHalf.substring(2) + leftHalf.charAt(0) + leftHalf.charAt(1);
                rightHalf = rightHalf.substring(2) + rightHalf.charAt(0) + rightHalf.charAt(1);
            }

            String combinedHalves56 = leftHalf + rightHalf;
            StringBuilder subKey48Bits = new StringBuilder();

            for (int j = 0; j < permutedChoice2.length; j++) {
                int pos = permutedChoice2[j];
                subKey48Bits.append(combinedHalves56.charAt(pos - 1));
            }
            keys.add(subKey48Bits.toString());
        }
    }

    public File encryptFiles(String path) throws IOException {
        byte[] rawData = Files.readAllBytes(Path.of(path));
        byte[] paddedData = addPadding(rawData);

        byte[] encryptedData = new byte[paddedData.length];

        for (int i = 0; i < paddedData.length; i += 8) {
            byte[] block = new byte[8];
            int blockLength = Math.min(8, paddedData.length - i);
            System.arraycopy(paddedData, i, block, 0, blockLength);

            byte[] encryptedBlock = encryptBlock(block);
            System.arraycopy(encryptedBlock, 0, encryptedData, i, 8);
        }

        String encryptedFilePath = path + ".des";
        Files.write(Path.of(encryptedFilePath), encryptedData);
        return new File(encryptedFilePath);
    }

    public File decryptFiles(String path) throws IOException {
        byte[] encryptedData = Files.readAllBytes(Path.of(path));
        byte[] decryptedData = new byte[encryptedData.length];

        for (int i = 0; i < encryptedData.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(encryptedData, i, block, 0, 8);

            byte[] decryptedBlock = decryptBlock(block);
            System.arraycopy(decryptedBlock, 0, decryptedData, i, 8);
        }

        decryptedData = removePadding(decryptedData);

        String originalFilePath = path.endsWith(".des") ? path.substring(0, path.length() - 4) : path + ".dec";
        Files.write(Path.of(originalFilePath), decryptedData);
        return new File(originalFilePath);
    }

    private byte[] encryptBlock(byte[] eightByteBlock) {
        String blockBits = bytesToBits(eightByteBlock);
        String afterInitialPermutation = initialPermutation(blockBits);

        String leftBlock = afterInitialPermutation.substring(0, 32);
        String rightBlock = afterInitialPermutation.substring(32, 64);

        for (int roundIndex = 0; roundIndex < 16; roundIndex++) {
            String roundKey = keys.get(roundIndex);
            String newLeftBlock = rightBlock;
            String functionOutput = roundFunction(rightBlock, roundKey);
            String newRightBlock = xorBinaryStrings(leftBlock, functionOutput);
            leftBlock = newLeftBlock;
            rightBlock = newRightBlock;
        }

        String combinedBeforeFinalPermutation = rightBlock + leftBlock;
        String cipherBits = finalPermutation(combinedBeforeFinalPermutation);

        return bitsToBytes(cipherBits);
    }

    private byte[] decryptBlock(byte[] eightByteBlock) {
        String blockBits = bytesToBits(eightByteBlock);
        String afterInitialPermutation = initialPermutation(blockBits);

        String leftBlock = afterInitialPermutation.substring(0, 32);
        String rightBlock = afterInitialPermutation.substring(32, 64);

        for (int roundIndex = 15; roundIndex >= 0; roundIndex--) {
            String roundKey = keys.get(roundIndex);
            String newLeftBlock = rightBlock;
            String functionOutput = roundFunction(rightBlock, roundKey);
            String newRightBlock = xorBinaryStrings(leftBlock, functionOutput);
            leftBlock = newLeftBlock;
            rightBlock = newRightBlock;
        }

        String combinedBeforeFinalPermutation = rightBlock + leftBlock;
        String plainBits = finalPermutation(combinedBeforeFinalPermutation);

        return bitsToBytes(plainBits);
    }

    private String roundFunction(String rightBlock32, String roundKey48) {
        String expandedBlock48 = applyExpansionPermutation(rightBlock32);
        String xoredBits48 = xorBinaryStrings(expandedBlock48, roundKey48);
        String substituted32 = applySBoxes(xoredBits48);
        String permuted32 = applyStraightPermutation(substituted32);
        return permuted32;
    }

    private String applyExpansionPermutation(String rightBlock32) {
        StringBuilder expanded = new StringBuilder();
        for (int i = 0; i < expansionTable.length; i++) {
            int bitPosition = expansionTable[i];
            expanded.append(rightBlock32.charAt(bitPosition - 1));
        }
        return expanded.toString();
    }

    private String xorBinaryStrings(String firstBits, String secondBits) {
        StringBuilder xorResult = new StringBuilder();
        for (int i = 0; i < firstBits.length(); i++) {
            xorResult.append(firstBits.charAt(i) == secondBits.charAt(i) ? '0' : '1');
        }
        return xorResult.toString();
    }

    private String applySBoxes(String xoredBits48) {
        int[][][] sBoxTables = new SBoxes().S;
        StringBuilder substitutionResult = new StringBuilder();

        for (int sBoxIndex = 0; sBoxIndex <= 7; sBoxIndex++) {
            int startBit = sBoxIndex * 5;

            int rowIndex = (xoredBits48.charAt(startBit) - '0') * 2
                    + (xoredBits48.charAt(startBit + 4) - '0');

            int columnIndex = (xoredBits48.charAt(startBit + 0) - '0') * 8
                    + (xoredBits48.charAt(startBit + 1) - '0') * 4
                    + (xoredBits48.charAt(startBit + 2) - '0') * 2
                    + (xoredBits48.charAt(startBit + 3) - '0');

            int sBoxValue = sBoxTables[sBoxIndex][rowIndex][columnIndex];
            substitutionResult.append(String.format("%4s", Integer.toBinaryString(sBoxValue)).replace(' ', '0'));
        }

        return substitutionResult.toString();
    }

    private String applyStraightPermutation(String sBoxOutput32) {
        StringBuilder permuted = new StringBuilder();
        for (int i = 0; i < straightPermutationTable.length; i++) {
            int bitPosition = straightPermutationTable[i];
            permuted.append(sBoxOutput32.charAt(bitPosition - 1));
        }
        return permuted.toString();
    }

    public String encryptText(String plainText) {
        byte[] textBytes = plainText.getBytes();
        byte[] paddedBytes = addPadding(textBytes);
        StringBuilder hexCiphertext = new StringBuilder();

        for (int i = 0; i < paddedBytes.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(paddedBytes, i, block, 0, 8);
            byte[] encryptedBlock = encryptBlock(block);
            for (byte b : encryptedBlock) {
                hexCiphertext.append(String.format("%02X", b));
            }
        }

        return hexCiphertext.toString();
    }

    public String encryptHex(String hexInput) {
        byte[] inputBytes = hexStringToBytes(hexInput);
        byte[] paddedBytes = addPadding(inputBytes);
        StringBuilder hexCiphertext = new StringBuilder();

        for (int i = 0; i < paddedBytes.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(paddedBytes, i, block, 0, 8);
            byte[] encryptedBlock = encryptBlock(block);
            for (byte b : encryptedBlock) {
                hexCiphertext.append(String.format("%02X", b));
            }
        }

        return hexCiphertext.toString();
    }

    public String decryptText(String hexCiphertext) {
        byte[] cipherBytes = hexStringToBytes(hexCiphertext);
        byte[] decryptedBytes = new byte[cipherBytes.length];

        for (int i = 0; i < cipherBytes.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(cipherBytes, i, block, 0, 8);
            byte[] decryptedBlock = decryptBlock(block);
            System.arraycopy(decryptedBlock, 0, decryptedBytes, i, 8);
        }

        decryptedBytes = removePadding(decryptedBytes);
        return new String(decryptedBytes);
    }

        private String initialPermutation(String dataBits) {
        StringBuilder permuted = new StringBuilder();
        for (int i = 0; i < IP.length; i++) {
            int bitPosition = IP[i];
            permuted.append(dataBits.charAt(bitPosition - 1));
        }
        return permuted.toString();
    }

        private String finalPermutation(String dataBits) {
        StringBuilder permuted = new StringBuilder();
        for (int i = 0; i < FP.length; i++) {
            int bitPosition = FP[i];
            permuted.append(dataBits.charAt(bitPosition - 1));
        }
        return permuted.toString();
    }


    private String bytesToBits(byte[] data) {
        StringBuilder bits = new StringBuilder();
        for (byte b : data) {
            bits.append(String.format("%8s", Integer.toBinaryString(b & 0xFF))
                    .replace(' ', '0'));
        }
        return bits.toString();
    }

    private byte[] bitsToBytes(String bits) {
        byte[] result = new byte[bits.length() / 8];
        for (int i = 0; i < result.length; i++) {
            String byteString = bits.substring(i * 8, i * 8 + 8);
            result[i] = (byte) Integer.parseInt(byteString, 2);
        }
        return result;
    }

    private byte[] hexStringToBytes(String hexString) {
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        byte[] result = new byte[hexString.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }

    public static byte[] addPadding(byte[] data) {
        int blockSize = 8;
        int padding = blockSize - (data.length % blockSize);

        byte[] padded = new byte[data.length + padding];
        System.arraycopy(data, 0, padded, 0, data.length);

        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padding;
        }

        return padded;
    }

    public static byte[] removePadding(byte[] data) {
        int padding = data[data.length - 1];
        int newLength = data.length - padding;

        byte[] result = new byte[newLength];
        System.arraycopy(data, 0, result, 0, newLength);

        return result;
    }
}
