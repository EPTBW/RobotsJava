package utils;

import api.IRobotPlugin;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    public static IRobotPlugin loadPlugin(File jarFile) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");
                    Class<?> loadedClass = classLoader.loadClass(className);

                    if (IRobotPlugin.class.isAssignableFrom(loadedClass) && !loadedClass.isInterface()) {
                        return (IRobotPlugin) loadedClass.getDeclaredConstructor().newInstance();
                    }
                }


            }
        } throw new IllegalArgumentException("В файле " + jarFile.getName() + " не найден класс реализующий IRobotPlugin");
    }

}
