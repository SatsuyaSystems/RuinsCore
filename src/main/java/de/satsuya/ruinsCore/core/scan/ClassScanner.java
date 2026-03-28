package de.satsuya.ruinsCore.core.scan;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassScanner {

    private ClassScanner() {
    }

    public static Set<Class<?>> scanPackage(ClassLoader classLoader, String packageName) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');

        Enumeration<URL> resources = classLoader.getResources(packagePath);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if ("file".equals(resource.getProtocol())) {
                scanDirectory(classes, packageName, resource);
                continue;
            }

            if ("jar".equals(resource.getProtocol())) {
                scanJar(classes, packageName, packagePath, resource);
            }
        }

        return classes;
    }

    private static void scanDirectory(Set<Class<?>> classes, String packageName, URL resource) {
        try {
            File directory = new File(resource.toURI());
            File[] files = directory.listFiles();
            if (files == null) {
                return;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(classes, packageName + "." + file.getName(), file.toURI().toURL());
                    continue;
                }

                if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    addClass(classes, className);
                }
            }
        } catch (URISyntaxException | IOException ignored) {
            // Scanning failure is handled by caller logs.
        }
    }

    private static void scanJar(Set<Class<?>> classes, String packageName, String packagePath, URL resource) {
        try {
            JarURLConnection connection = (JarURLConnection) resource.openConnection();
            try (JarFile jarFile = connection.getJarFile()) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (!name.startsWith(packagePath) || !name.endsWith(".class") || name.contains("$")) {
                        continue;
                    }

                    String className = name.replace('/', '.').substring(0, name.length() - 6);
                    if (className.startsWith(packageName)) {
                        addClass(classes, className);
                    }
                }
            }
        } catch (IOException ignored) {
            // Scanning failure is handled by caller logs.
        }
    }

    private static void addClass(Set<Class<?>> classes, String className) {
        try {
            classes.add(Class.forName(className));
        } catch (ClassNotFoundException ignored) {
            // Failed classes are ignored to keep loading resilient.
        }
    }
}

