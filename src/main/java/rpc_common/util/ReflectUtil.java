package rpc_common.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ReflectUtil {
//    private static final Logger log = LoggerFactory.getLogger(ReflectUtil.class);

    private static final String FILE = "file";
    private static final String JAR = "jar";
    private static final String EXTENSION_CLASS = ".class";

    /*
    通过方法调用栈来获取启动类，因为启动类一定位于调用栈的最底端
     */
    public static String getBootClassByStackTrace() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        return stack[stack.length - 1].getClassName();
    }

    public static Set<Class<?>> getClasses(String packageName){
        Set<Class<?>> classes = new HashSet<>();
        boolean isRecursive = true;
        String packageDirName = packageName.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while(dirs.hasMoreElements()){
                URL url = dirs.nextElement();
                /*
                获取地址对应的协议信息（因为不一定是从本地加载的），再根据协议信息处理下一步，这里的协议主要包括文件类型、JAR类型
                 */
                String protocol = url.getProtocol();
                if(FILE.equals(protocol)){
                    String packagePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, packagePath, isRecursive, classes);
                }else if(JAR.equals(protocol)){
                    JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while(entries.hasMoreElements()){
                        /*
                        获取jar里的一个实体，包括所有文件和文件夹
                         */
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        /*
                        不理解含义
                         */
                        if(name.charAt(0) == '/'){
                            name = name.substring(1);
                        }
                        /*
                        只加载 jar 包中和提供包名一致部分的包中的类文件
                         */
                        if(name.startsWith(packageDirName)){
                            /*
                            文件名前的第一个斜杠，比如 xxx/xxx/xx/yy 会获取 yy 前的斜杠
                            则斜杠前的是具体包名，斜杠后的是文件名
                             */
                            int idx = name.lastIndexOf('/');
                            if(idx != -1){
                                packageName = name.substring(0, idx).replace('/', '.');
                                /*
                                如果路径对应文件名是 Java 的字节码文件且该路径不是一个文件夹，则尝试加载该类文件
                                 */
                                if(name.endsWith(EXTENSION_CLASS) && !entry.isDirectory()){
                                    String className = name.substring(packageName.length() + 1,
                                            name.length() - EXTENSION_CLASS.length());
                                    classes.add(Class.forName(packageName + "." + className));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("添加用户自定义视图类错误：找不到此类的.class文件", e);
        }
        return classes;
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         boolean isRecursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if(!dir.exists() || !dir.isDirectory()){
            log.warn("用户定义的包名"  + packageName + "不存在或其下没有任何文件");
        }
        /*
        自定义过滤规则:如果要递归查找且当前路径为一文件夹或者是 Java 字节码文件
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
                String className = file.getName().substring(0, file.getName().length() - EXTENSION_CLASS.length());
                try {
                    /*
                    loadClass()加载类只会进行到加载这一步，而ForName()则会完成加载、链接、初始化三步
                    这里主要考虑惰性加载以加速加载速度
                     */
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    log.error("添加用户自定义视图类错误：找不到此类的.class文件", e);
                }
            }
        }
    }
}
