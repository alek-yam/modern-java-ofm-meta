import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class OutOfMetaspaceApp {
	
	private static Logger log = Logger.getLogger(OutOfMetaspaceApp.class.getName());
	
	public static void main(String[] args) {
		loadDir("/home/alek/.m2/repository/junit/junit/4.12");
	}
	
	private static void loadDir(String dirPath) {
		try {
			File dir = new File(dirPath);
			FileFilter filter = new JarFileFilter();
			File[] jarFiles = dir.listFiles(filter);
			
			for (File jar : jarFiles) {
				loadJar(jar.getPath());
			}
			
		} catch (Throwable y) {
			 System.err.println(y);
		}
	}

	private static void loadJar(String pathToJar) throws IOException {
		log.info("Loading classes from file: " + pathToJar);
		
		long count = 0;
		
		try(JarFile jarFile = new JarFile(pathToJar)) {
			Enumeration<JarEntry> e = jarFile.entries();
			URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);

			while (e.hasMoreElements()) {
			    JarEntry je = e.nextElement();
			    
			    if(je.isDirectory() || !je.getName().endsWith(".class")){
			        continue;
			    }
			    
			    // -6 because of .class
			    String className = je.getName().substring(0,je.getName().length()-6);
			    className = className.replace('/', '.');
			    
			    try {
			    	/*Class c = */cl.loadClass(className);
			    	count++;
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (Throwable e2) {
					e2.printStackTrace();
				}
			}
		}
		
		log.info(Long.toString(count) + " classes have been loaded.");
	}
	
    private static class JarFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			if (pathname.isFile()) {
				return pathname.getName().toLowerCase().endsWith(".jar");
			}
			
			return false;
		}

    }
}
