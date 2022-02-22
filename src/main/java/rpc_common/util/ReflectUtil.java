package rpc_common.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public final class ReflectUtil {
    private static final String EXTENSION_CLASS = ".class";

    /*
    get the boot class from stack trace, as the boot class is the bottom of the stack
     */
    public static Class<?> getBootClassByStackTrace() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        String bootClassName = stack[stack.length - 1].getClassName();
        Class<?> bootClass;
        try {
            bootClass = Class.forName(bootClassName);
        } catch (ClassNotFoundException e) {
            log.error("boot class {} not found", bootClassName, e);
            throw new RuntimeException(String.format("boot class %s not found", bootClassName));
        }
        return bootClass;
    }

    /*
    get all class with @Service annotated under the given package
     */
    public static Set<Class<?>> getClasses(String packageName){
        Set<Class<?>> classes = new HashSet<>();
        boolean isRecursive = true;
        String packageDirName = packageName.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while(dirs.hasMoreElements()){
                URL url = dirs.nextElement();
                /*
                get the protocol from url, like file or jar
                 */
                String protocol = url.getProtocol();
                if("file".equals(protocol)){
                    String packagePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, packagePath, isRecursive, classes);
                }else if("jar".equals(protocol)){
                    JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while(entries.hasMoreElements()){
                        /*
                        get an entry from the jar, including all file and directory
                         */
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        /*
                        currently, not understand the meaning
                         */
                        if(name.charAt(0) == '/'){
                            name = name.substring(1);
                        }
                        /*
                        only load part of the package whose name is same with the given package name
                         */
                        if(name.startsWith(packageDirName)){
                            /*
                            the index of first / of path name, like xxx/xxx/yy will get the / before yy
                            thus the part before index is package name and the remaining is file name
                             */
                            int idx = name.lastIndexOf('/');
                            if(idx != -1){
                                packageName = name.substring(0, idx).replace('/', '.');
                                /*
                                load if it is a java class file and is not a directory
                                 */
                                if(name.endsWith(".class") && !entry.isDirectory()){
                                    String className = name.substring(packageName.length() + 1,
                                            name.length() - 6);
                                    classes.add(Class.forName(packageName + "." + className));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("add user-defined class failed: no such class file ", e);
        }
        return classes;
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         boolean isRecursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if(!dir.exists() || !dir.isDirectory()){
            log.warn("user-defined "  + packageName + "contains no class file");
        }
        /*
        get paths recursively which are directories or java class files
         */
        File[] files = dir.listFiles(filepath -> (isRecursive && filepath.isDirectory()) || (filepath.getName().endsWith(EXTENSION_CLASS)));
        if(files == null){
            return;
        }
        for(File file : files){
            if(file.isDirectory()){
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(), isRecursive, classes);
            }else{
                /*
                remove .class from file name
                 */
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    /*
                    loadClass() will only process to load, but forName() will complete load, link and initialization
                    here mostly consider lazy load to speed up the loading
                     */
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    log.error("add user-defined class failed: no such class file ", e);
                }
            }
        }
    }
}
