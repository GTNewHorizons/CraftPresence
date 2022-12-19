/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * File Utilities for interpreting Files and Class Objects
 *
 * @author CDAGaming
 */
public class FileUtils {
    /**
     * A GSON Json Builder Instance
     */
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    /**
     * The list of the currently detected class names
     */
    private static final ClassInfoList CLASS_LIST = new ClassInfoList();
    /**
     * The list of the currently detected class names
     */
    private static final Map<String, Class<?>> CLASS_MAP = Maps.newHashMap();
    /**
     * The list of the currently detected modded class names
     */
    private static final List<String> MODDED_CLASS_NAMES = Lists.newArrayList();

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The File to access
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     * @throws Exception If Unable to read the File
     */
    public static <T> T getJsonData(File data, Class<T> classObj, Modifiers... args) throws Exception {
        return getJsonData(fileToString(data, "UTF-8"), classObj, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The json string to access
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     */
    public static <T> T getJsonData(String data, Class<T> classObj, Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        return builder.create().fromJson(data, classObj);
    }

    /**
     * Interpret compatible objects into Json Elements
     *
     * @param obj  The object data to access
     * @param args The Command Arguments to parse
     * @return the resulting json string
     */
    public static String toJsonData(Object obj, Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        return builder.create().toJson(obj);
    }

    /**
     * Writes Raw Json Data Objects to the specified file
     *
     * @param json     The json data to access
     * @param file     The resulting file to output to
     * @param encoding The encoding to parse the output as
     * @param args     The Command Arguments to parse
     */
    public static void writeJsonData(Object json, File file, String encoding, Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        Writer writer = null;
        OutputStream outputStream = null;

        try {
            outputStream = Files.newOutputStream(file.toPath());
            writer = new OutputStreamWriter(outputStream, Charset.forName(encoding));
            builder.create().toJson(json, writer);
        } catch (Exception ex) {
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }
        }

        try {
            if (writer != null) {
                writer.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.data.close"));
            ex.printStackTrace();
        }
    }

    /**
     * Parses inputted raw json into a valid JsonObject
     *
     * @param json The raw Input Json String
     * @return A Parsed and Valid JsonObject
     */
    public static JsonObject parseJson(String json) {
        if (!StringUtils.isNullOrEmpty(json)) {
            final JsonParser dataParser = new JsonParser();
            return dataParser.parse(json).getAsJsonObject();
        } else {
            return new JsonObject();
        }
    }

    /**
     * Downloads a File from a Url, then stores it at the target location
     *
     * @param urlString The Download Link
     * @param file      The destination and filename to store the download as
     */
    public static void downloadFile(final String urlString, final File file) {
        try {
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.download.init", file.getName(), file.getAbsolutePath(), urlString));
            final URL url = new URL(urlString);
            if (file.exists()) {
                final boolean fileDeleted = file.delete();
                if (!fileDeleted) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.delete.file", file.getName()));
                }
            }

            final InputStream stream = UrlUtils.getURLStream(url);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, file);
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.download.loaded", file.getName(), file.getAbsolutePath(), urlString));
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.download", file.getName(), urlString, file.getAbsolutePath()));
            ex.printStackTrace();
        }
    }

    /**
     * Attempts to load the specified file as a DLL
     *
     * @param file The file to attempt to load
     */
    public static void loadFileAsDLL(final File file) {
        try {
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.dll.init", file.getName()));
            boolean isPermsSet = file.setReadable(true) && file.setWritable(true);
            if (isPermsSet) {
                System.load(file.getAbsolutePath());
            }
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.dll.loaded", file.getName()));
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.dll", file.getName()));
            ex.printStackTrace();
        }
    }

    /**
     * Attempts to convert a File's data into a readable String
     *
     * @param file     The file to access
     * @param encoding The encoding to parse the file as
     * @return The file's data as a String
     * @throws Exception If Unable to read the file
     */
    public static String fileToString(File file, String encoding) throws Exception {
        return org.apache.commons.io.FileUtils.readFileToString(file, Charset.forName(encoding));
    }

    /**
     * Gets the File Extension of a File (Ex: txt)
     *
     * @param file The file to access
     * @return The file's extension String
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }

    /**
     * Retrieve the Amount of Active Mods in the {@link ModUtils#modsDir}
     *
     * @return The Mods that are active in the directory
     */
    public static int getModCount() {
        // Mod is within ClassLoader if in a Dev Environment
        // and is thus automatically counted if this is the case
        int modCount = ModUtils.IS_DEV ? 1 : 0;
        final File[] mods = new File(ModUtils.modsDir).listFiles();

        if (mods != null) {
            for (File modFile : mods) {
                if (getFileExtension(modFile).equals(".jar")) {
                    modCount++;
                }
            }
        }
        return modCount;
    }

    /**
     * Retrieve a List of Classes that extend or implement anything in the search list
     *
     * @param searchList          The Super Type Classes to look for within the source packages specified
     * @param includeExtraClasses Whether to include any extra sub classes
     * @param sourcePackages      The root package directories to search within
     * @return The List of found class names from the search
     */
    public static List<Class<?>> getClassNamesMatchingSuperType(final List<Class<?>> searchList, final boolean includeExtraClasses, final String... sourcePackages) {
        final List<Class<?>> matchingClasses = Lists.newArrayList();
        final List<String> sourceData = Lists.newArrayList(sourcePackages);

        if (!sourceData.isEmpty() && includeExtraClasses) {
            sourceData.addAll(getModClassNames());
        }

        Pair<Boolean, List<Class<?>>> subClassData = new Pair<>(false, Lists.newArrayList());
        for (Class<?> loadedInstance : getClasses(sourceData)) {
            for (Class<?> searchClass : searchList) {
                subClassData = isSubclassOf(loadedInstance, searchClass, subClassData.getSecond());

                if (subClassData.getFirst()) {
                    // If superclass data was found, add the scanned classes
                    // as well as the original class
                    if (!matchingClasses.contains(loadedInstance)) {
                        matchingClasses.add(loadedInstance);
                    }

                    for (Class<?> subClassInfo : subClassData.getSecond()) {
                        if (!matchingClasses.contains(subClassInfo)) {
                            matchingClasses.add(subClassInfo);
                        }
                    }

                    break;
                } else {
                    // If no superclass data found, reset for next data
                    subClassData = new Pair<>(false, Lists.newArrayList());
                }
            }
        }

        return matchingClasses;
    }

    /**
     * Retrieves sub/super class data for the specified data
     *
     * @param originalClass  The original class to scan for the specified sub/super-class
     * @param superClass     The sub/super-class target to locate
     * @param scannedClasses The class hierarchy of scanned data
     * @return A pair with the format of isSubclassOf:scannedClasses
     */
    protected static Pair<Boolean, List<Class<?>>> isSubclassOf(final Class<?> originalClass, final Class<?> superClass, final List<Class<?>> scannedClasses) {
        if (originalClass == null || superClass == null) {
            // Top of hierarchy, or no super class defined
            return new Pair<>(false, scannedClasses);
        } else if (originalClass.equals(superClass)) {
            return new Pair<>(true, scannedClasses);
        } else {
            // Attempt to see if things match with their deobfuscated names
            final String className = MappingUtils.getCanonicalName(originalClass);
            final String superClassName = MappingUtils.getCanonicalName(superClass);
            if (className.equals(superClassName)) {
                return new Pair<>(true, scannedClasses);
            } else {
                // try the next level up the hierarchy and add this class to scanned history.
                scannedClasses.add(originalClass);
                final Pair<Boolean, List<Class<?>>> subClassInfo = isSubclassOf(originalClass.getSuperclass(), superClass, scannedClasses);

                if (!subClassInfo.getFirst() && originalClass.getInterfaces() != null) {
                    for (final Class<?> inter : originalClass.getInterfaces()) {
                        if (isSubclassOf(inter, superClass, scannedClasses).getFirst()) {
                            return new Pair<>(true, scannedClasses);
                        }
                    }
                }

                return new Pair<>(subClassInfo.getFirst(), scannedClasses);
            }
        }
    }

    /**
     * Retrieve a List of Classes that extend or implement anything in the search list
     *
     * @param searchTarget        The Super Type Class to look for within the source packages specified
     * @param includeExtraClasses Whether to include any extra sub classes
     * @param sourcePackages      The root package directories to search within
     * @return The List of found classes from the search
     */
    public static List<Class<?>> getClassNamesMatchingSuperType(final Class<?> searchTarget, final boolean includeExtraClasses, final String... sourcePackages) {
        return getClassNamesMatchingSuperType(Lists.newArrayList(searchTarget), includeExtraClasses, sourcePackages);
    }

    /**
     * Return whether or not a class exists out of the specified arguments
     *
     * @param loader The {@link ClassLoader} to attempt loading with
     * @param init   Whether to initialize the class, if found
     * @param paths  The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findValidClass(ClassLoader loader, final boolean init, final String... paths) {
        for (String path : paths) {
            try {
                if (loader == null) {
                    return Class.forName(path);
                } else {
                    return Class.forName(path, init, loader);
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    /**
     * Return whether or not a class exists out of the specified arguments
     *
     * @param loader The {@link ClassLoader} to attempt loading with
     * @param paths  The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findValidClass(final ClassLoader loader, final String... paths) {
        return findValidClass(loader, false, paths);
    }

    /**
     * Return whether or not a class exists out of the specified arguments
     *
     * @param useClassLoader Whether to use the thread's current class loader
     * @param paths          The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findValidClass(final boolean useClassLoader, final String... paths) {
        return findValidClass(useClassLoader ? MappingUtils.CLASS_LOADER : null, paths);
    }

    /**
     * Return whether or not a class exists out of the specified arguments
     *
     * @param paths The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findValidClass(final String... paths) {
        return findValidClass(true, paths);
    }

    /**
     * Retrieve and Cache all known classes within the Class Loader
     *
     * @return a list of all known classes
     */
    public static ClassInfoList getClassList() {
        if (CLASS_LIST.isEmpty()) {
            // Attempt to get all possible classes from the JVM Class Loader
            final ClassGraph graphInfo = new ClassGraph()
                    .enableClassInfo()
                    .rejectPackages(
                            "net.java", "com.sun", "com.jcraft", "com.intellij", "jdk", "akka", "ibxm", "scala",
                            "*.mixin.*", "*.mixins.*", "*.jetty.*"
                    )
                    .disableModuleScanning();
            if (MappingUtils.JAVA_SPEC < 16) {
                // If we are below Java 16, we can just use the Thread's classloader
                // See: https://github.com/classgraph/classgraph/wiki#running-on-jdk-16
                graphInfo.overrideClassLoaders(MappingUtils.CLASS_LOADER);
            }

            try (ScanResult scanResult = graphInfo.scan()) {
                for (ClassInfo result : scanResult.getAllClasses()) {
                    final String resultName = MappingUtils.getMappedPath(result.getName());
                    if (!CLASS_LIST.contains(result) && !resultName.toLowerCase().contains("mixin")) {
                        CLASS_LIST.add(result);
                        try {
                            CLASS_MAP.put(resultName, result.loadClass(true));
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }
        }
        return CLASS_LIST;
    }

    /**
     * Retrieve a list of all classes matching the specified lists of paths
     *
     * @param paths A nullable list of paths to be interpreted
     * @return the resulting list
     */
    public static List<Class<?>> getClasses(final List<String> paths) {
        final List<Class<?>> results = Lists.newArrayList();
        final Map<String, Set<String>> unmappedNames = Maps.newHashMap();
        for (String path : paths) {
            unmappedNames.put(path, MappingUtils.getUnmappedClassesMatching(path));
        }

        for (ClassInfo classInfo : getClassList()) {
            if (classInfo != null) {
                final String classPath = MappingUtils.getMappedPath(classInfo.getName());
                boolean hasMatch = paths.isEmpty();
                // Attempt to Add Classes Matching any of the Source Packages
                for (String path : paths) {
                    final Set<String> unmapped = unmappedNames.get(path);
                    if (classPath.startsWith(path) || unmapped.contains(classPath)) {
                        hasMatch = true;
                        break;
                    }
                }

                if (hasMatch) {
                    try {
                        results.add(CLASS_MAP.get(classPath));
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return results;
    }

    /**
     * Retrieves a List of all readable Class Names for the active mods
     *
     * @return The list of viewable Mod Class Names
     */
    public static List<String> getModClassNames() {
        final List<String> classNames = Lists.newArrayList();
        final File[] mods = new File(ModUtils.modsDir).listFiles();

        if (mods != null) {
            for (File modFile : mods) {
                if (getFileExtension(modFile).equals(".jar")) {
                    try {
                        final JarFile jarFile = new JarFile(modFile.getAbsolutePath());
                        final Enumeration<JarEntry> allEntries = jarFile.entries();
                        while (allEntries.hasMoreElements()) {
                            final JarEntry entry = allEntries.nextElement();
                            final String file = entry.getName();
                            if (file.endsWith(".class")) {
                                final String className = file.replace('/', '.').substring(0, file.length() - 6);
                                classNames.add(className);
                            }
                        }
                        jarFile.close();
                    } catch (Throwable ex) {
                        if (ModUtils.IS_VERBOSE) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            return classNames;
        } else {
            return Lists.newArrayList();
        }
    }

    /**
     * Attempts to Retrieve the Specified Resource as an InputStream
     *
     * @param fallbackClass Alternative Class Loader to Use to Locate the Resource
     * @param pathToSearch  The File Path to search for
     * @return The InputStream for the specified resource, if successful
     */
    public static InputStream getResourceAsStream(final Class<?> fallbackClass, final String pathToSearch) {
        InputStream in = null;
        boolean useFallback = false;

        try {
            in = MappingUtils.CLASS_LOADER.getResourceAsStream(pathToSearch);
        } catch (Exception ex) {
            useFallback = true;
        }

        if (useFallback || in == null) {
            in = fallbackClass.getResourceAsStream(pathToSearch);
        }
        return in;
    }

    /**
     * Applies the specified {@link Modifiers} to a {@link GsonBuilder} instance
     *
     * @param instance The {@link GsonBuilder} to interpret
     * @param args     The Command Arguments to parse
     * @return The modified {@link GsonBuilder} instance
     */
    public static GsonBuilder applyModifiers(final GsonBuilder instance, Modifiers... args) {
        for (Modifiers param : args) {
            switch (param) {
                case DISABLE_ESCAPES:
                    instance.disableHtmlEscaping();
                    break;
                case PRETTY_PRINT:
                    instance.setPrettyPrinting();
                    break;
                default:
                    break;
            }
        }
        return instance;
    }

    /**
     * Constants representing various {@link GsonBuilder} togges,
     * such as Disabling Escape Characters and Toggling Pretty Print
     */
    public enum Modifiers {
        /**
         * Constant for the "Disable Escapes" Modifier.
         */
        DISABLE_ESCAPES,
        /**
         * Constant for the "Pretty Print" Modifier.
         */
        PRETTY_PRINT
    }
}