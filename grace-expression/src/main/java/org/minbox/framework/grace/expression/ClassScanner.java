package org.minbox.framework.grace.expression;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描器
 * <p>
 * 根据指定的跟目录进行扫描全部的类
 * 支持使用递归扫描子包内的类、支持从Jar包中递归扫描类
 *
 * @author 恒宇少年
 */
public class ClassScanner {
    private String basePackage;
    private boolean recursive;
    private Predicate<String> packagePredicate;
    private Predicate<Class> classPredicate;

    public ClassScanner(String basePackage, boolean recursive) {
        this.basePackage = basePackage;
        this.recursive = recursive;
    }

    public ClassScanner(String basePackage, boolean recursive, Predicate<String> packagePredicate, Predicate<Class> classPredicate) {
        this.basePackage = basePackage;
        this.recursive = recursive;
        this.packagePredicate = packagePredicate;
        this.classPredicate = classPredicate;
    }

    public Set<Class<?>> doScanning() throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new LinkedHashSet();
        String packageName = basePackage;
        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        }
        String basePackageFilePath = packageName.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(basePackageFilePath);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            if (Protocol.file.toString().equals(protocol)) {
                String filePath = URLDecoder.decode(resource.getFile(), "UTF-8");
                doScanPackageClassesByFile(classes, packageName, filePath);
            } else if (Protocol.jar.toString().equals(protocol)) {
                doScanPackageClassesByJar(packageName, resource, classes);
            }
        }

        return classes;
    }

    private void doScanPackageClassesByJar(String basePackage, URL url, Set<Class<?>> classes) throws IOException, ClassNotFoundException {
        String packageName = basePackage;
        String basePackageFilePath = packageName.replace('.', '/');
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (!name.startsWith(basePackageFilePath) || entry.isDirectory()) {
                continue;
            }
            if (!recursive && name.lastIndexOf('/') != basePackageFilePath.length()) {
                continue;
            }
            if (packagePredicate != null) {
                String jarPackageName = name.substring(0, name.lastIndexOf('/')).replace("/", ".");
                if (!packagePredicate.test(jarPackageName)) {
                    continue;
                }
            }
            String className = name.replace('/', '.');
            className = className.substring(0, className.length() - 6);
            Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(className);
            if (classPredicate == null || classPredicate.test(loadClass)) {
                classes.add(loadClass);
            }
        }
    }

    private void doScanPackageClassesByFile(Set<Class<?>> classes, String packageName, String packagePath) throws ClassNotFoundException {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(file -> {
            String filename = file.getName();
            if (file.isDirectory()) {
                if (!recursive) {
                    return false;
                }
                if (packagePredicate != null) {
                    return packagePredicate.test(packageName + "." + filename);
                }
                return true;
            }
            return filename.endsWith(".class");
        });
        if (null == dirFiles) {
            return;
        }
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                doScanPackageClassesByFile(classes, packageName + "." + file.getName(), file.getAbsolutePath());
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
                if (classPredicate == null || classPredicate.test(loadClass)) {
                    classes.add(loadClass);
                }
            }
        }
    }

    enum Protocol {
        file, jar
    }
}
