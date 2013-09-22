import java.util.*;
import java.util.regex.*;
import java.util.zip.*;
import java.io.*;

public class Reflection {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: java Reflection <Command> <Command arguments>");
        }
        if (args[0].equals("packages")) {
            printAllPackages();
        } else {
            throw new IllegalArgumentException("Avaible commands: packages");
        }
    }

    public static void printAllPackages() {
        Set<String> packageFQNs = new HashSet<String>();
        Pattern pattern = Pattern.compile("^[^$]*$");

        // TODO: Support library paths for Windows and Linux
        String path = System.getProperty("java.home") + File.separator + ".." + File.separator + "Classes" + File.separator;

        File dir = new File(path);
        for (File file : dir.listFiles()) {
            ZipFile zip;
            try {
                zip = new ZipFile(file.getAbsolutePath());
            } catch (IOException e) {
                continue;
            }

            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = entries.nextElement();
                if (entry == null)
                    continue;
                String packageFQN = entry.getName().replace('/', '.').replaceAll("\\.class", "");
                if (!pattern.matcher(packageFQN).find())
                    continue;
                packageFQNs.add(packageFQN);
            }
        }

        for (String packageFQN : packageFQNs)
            System.out.println(packageFQN);
    }
}
