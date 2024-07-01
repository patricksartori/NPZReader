package ch.patricksartori.npzreader;

import ch.patricksartori.npyobject.NPYObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NPYReader {

    public static NPYObject readNpy(String npyFilePath) throws IOException {
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
        NPYObject<?> object;
        switch (dtype) {
            case "<f8":
                double[] doubleArray = new double[numElements];
                List<Double> doubleList = new ArrayList<>();

                for (int i = 0; i < numElements; i++) {
                    doubleList.add(buffer.getDouble());
                }
                //System.out.println("Data: " + Arrays.toString(doubleArray));
                object = new NPYObject<Double>(dtype, shape, doubleList);
                break;
            case "<i4":
                int[] intArray = new int[numElements];
                List<Integer> intList = new ArrayList<>();
                for (int i = 0; i < numElements; i++) {
                    //intArray[i] = buffer.getInt();
                    intList.add(buffer.getInt());
                }
                //System.out.println("Data: " + Arrays.toString(intArray));
                object = new NPYObject<Integer>(dtype, shape, intList);

                break;
            case "<i8":
                long[] longArray = new long[numElements];
                List<Long> longList = new ArrayList<>();

                for (int i = 0; i < numElements; i++) {
                    longList.add(buffer.getLong());
                }
                //System.out.println("Data: " + Arrays.toString(longArray));
                object = new NPYObject<Long>(dtype, shape, longList);
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
                //System.out.println("Data: " + Arrays.toString(stringArray));
                object = new NPYObject<String>(dtype, shape, List.of(stringArray));
                break;
            case "<U6":
                stringLength = 6; // Number of characters in each Unicode string
                stringArray = new String[numElements];
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
                //System.out.println("Data: " + Arrays.toString(stringArray));
                object = new NPYObject<String>(dtype, shape, List.of(stringArray));

                break;
            // Add more cases as needed for other dtypes

            default:
                throw new UnsupportedOperationException("Data type not supported: " + dtype);
        }
        return object;
    }

    public static double[][] reshape(double[] flatArray, int[] shape) {
        if (shape.length != 2) {
            throw new UnsupportedOperationException("Only 2D arrays are supported for reshaping");
        }
        int rows = shape[0];
        int cols = shape[1];
        double[][] reshapedArray = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(flatArray, i * cols, reshapedArray[i], 0, cols);
        }
        return reshapedArray;
    }
}
