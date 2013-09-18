import java.util.*;
import java.util.regex.*;
import java.util.zip.*;
import java.io.*;

public class Reflection {
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java Reflection <Command> <Command arguments>");
        }
        if (args[0].equals("packages")) {
            printPackages(args[1]);
        } else {
            throw new IllegalArgumentException("Avaible commands: packages");
        }
    }

    public static void printPackages(String keyword) {
        Set<String> packageFQNs = new HashSet<String>();

        StringBuffer regex = new StringBuffer();
        regex.append("^");
        regex.append(Pattern.quote(keyword));
        regex.append("[^$]*$");
        Pattern pattern = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);

        int numberOfKeywordPackage = keyword.split("\\.").length;

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
                String[] packageNames = packageFQN.split("\\.");
                if (packageNames.length <= numberOfKeywordPackage)
                    continue;
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < numberOfKeywordPackage + 1; i++) {
                    buffer.append(packageNames[i]);
                    if (i < numberOfKeywordPackage)
                        buffer.append(".");
                }
                packageFQNs.add(buffer.toString());
            }
        }

        for (String packageFQN : packageFQNs)
            System.out.println(packageFQN);
    }
}
