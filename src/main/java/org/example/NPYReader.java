package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NPYReader {

    public static void readNpy(String npyFilePath) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(npyFilePath));

        // Parse header
        int headerLength = 10 + (data[8] & 0xFF) + (data[9] & 0xFF) * 256;
        String header = new String(Arrays.copyOfRange(data, 10, headerLength), "UTF-8");

        System.out.println("Header: " + header);

        // Extract data type and shape from header
        Pattern dtypePattern = Pattern.compile("'descr':\\s*'([^']*)'");
        Pattern shapePattern = Pattern.compile("'shape':\\s*\\(([^)]*)\\)");

        Matcher dtypeMatcher = dtypePattern.matcher(header);
        Matcher shapeMatcher = shapePattern.matcher(header);

        String dtype = "";
        if (dtypeMatcher.find()) {
            dtype = dtypeMatcher.group(1);
        }

        String[] shapeStr = {};
        if (shapeMatcher.find()) {
            shapeStr = shapeMatcher.group(1).split(",");
        }

        int[] shape = Arrays.stream(shapeStr).map(String::trim).filter(s -> !s.isEmpty()).mapToInt(Integer::parseInt).toArray();
        int numElements = shape.length == 0 ? 1 : Arrays.stream(shape).reduce(1, (a, b) -> a * b);

        ByteBuffer buffer = ByteBuffer.wrap(data, headerLength, data.length - headerLength).order(ByteOrder.LITTLE_ENDIAN);

        switch (dtype) {
            case "<f8":
                double[] doubleArray = new double[numElements];
                for (int i = 0; i < numElements; i++) {
                    doubleArray[i] = buffer.getDouble();
                }
                System.out.println("Data: " + Arrays.toString(doubleArray));
                break;
            case "<i4":
                int[] intArray = new int[numElements];
                for (int i = 0; i < numElements; i++) {
                    intArray[i] = buffer.getInt();
                }
                System.out.println("Data: " + Arrays.toString(intArray));
                break;
            case "<i8":
                long[] longArray = new long[numElements];
                for (int i = 0; i < numElements; i++) {
                    longArray[i] = buffer.getLong();
                }
                System.out.println("Data: " + Arrays.toString(longArray));
                break;
            case "<U7":
                int stringLength = 7; // Number of characters in each Unicode string
                String[] stringArray = new String[numElements];
                for (int i = 0; i < numElements; i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < stringLength; j++) {
                        int codePoint = buffer.getInt(); // Read 4 bytes as an int (UTF-32 code point)
                        if (codePoint != 0) { // Ignore null characters (padding)
                            sb.append(Character.toChars(codePoint));
                        }
                    }
                    stringArray[i] = sb.toString().trim();
                }
                System.out.println("Data: " + Arrays.toString(stringArray));
                break;
            // Add more cases as needed for other dtypes
            default:
                throw new UnsupportedOperationException("Data type not supported: " + dtype);
        }
    }

    public static void main(String[] args) {
        try {
            readNpy("C:\\Users\\patri\\OneDrive\\Desktop\\git\\NpzReader\\src\\resources\\data.npy");
            readNpy("C:\\Users\\patri\\OneDrive\\Desktop\\git\\NpzReader\\src\\resources\\chs.npy");
            readNpy("C:\\Users\\patri\\OneDrive\\Desktop\\git\\NpzReader\\src\\resources\\fs.npy");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
