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

    public static void printPackages(String patternString) {
        // Avoid duplications of package names
        Set<String> packageNames = new HashSet<String>();

        int packageNumber = patternString.split("\\.").length;
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);

        // TODO: Support library paths for Windows and Linux
        String path = System.getProperty("java.home") + File.separator + ".." + File.separator + "Classes" + File.separator;

        File dir = new File(path);
        File[] files = dir.listFiles();
        for (File file : files) {
            String filepath = file.getAbsolutePath();
            if (!filepath.endsWith(".jar") && !filepath.endsWith(".zip"))
                continue;

            ZipFile zipFile;
            try {
                zipFile = new ZipFile(filepath);
            } catch (IOException e) {
                continue;
            }

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry == null)
                    continue;

                String entryName = entry.getName();
                if (!entryName.endsWith(".class") || entryName.indexOf("$") > 0)
                    continue;

                String packageName = entryName.replace('/', '.').replaceAll("\\.class", "");
                Matcher matcher = pattern.matcher(packageName);
                if (matcher.find() && packageName.split("\\.").length == packageNumber + 1)
                    packageNames.add(packageName);
            }
        }

        for (String packageName : packageNames) {
            System.out.println(packageName);
        }
    }
}
