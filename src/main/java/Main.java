import ch.patricksartori.npyobject.NPYObject;
import ch.patricksartori.npzreader.NPYReader;
import ch.patricksartori.npzreader.NPZReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String npzFilePath = "C:\\Users\\patri\\OneDrive\\Desktop\\Tesi Bachelor\\project_material\\Data\\Dmitdb_S100_R100_T01_sig.npz";
        String outputDir = "output\\directory";
        List<NPYObject> objects = new ArrayList<>();
        try {
            NPZReader.extractNPZ(npzFilePath, outputDir);

            // Read each NPY file in the output directory
            Files.walk(Paths.get(outputDir))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".npy"))
                    .forEach(path -> {
                        try {
                            objects.add(NPYReader.readNpy(path.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(objects);
    }
}
