package org.jarfinder.mojo;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jarfinder.JarFinderUtil;
import org.jarfinder.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo(name = "findClass")
public class JarFinderMojo extends AbstractMojo {

	Logger logger = LoggerFactory.getLogger(JarFinderMojo.class);

	
	@Parameter(property = "findClass.className", defaultValue = "org.none.className.class")
	private String className;

	@Parameter(property = "findClass.folder", defaultValue = "")
	private String folder;
	
	@Parameter(property = "findClass.caseSensitive", defaultValue = "true")
	private String  caseSensitive;
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		
		logger.info("JarFinderMojo: findClass: "+ className);
		
		logger.info("Porperty folder: "+ folder);
		String jarDirecotry=folder;
		if (folder==null || "".equals(folder)) {
			String userHome =System.getProperty("user.home");
			logger.info("Home folder: "+userHome);
			jarDirecotry=userHome+"/.m2/repository";
			logger.info("Maven local repository: "+jarDirecotry);
		}
		
		logger.info("Searching please wait ...");
		List<SearchResult> res= JarFinderUtil.findJar(jarDirecotry, className, "true".equalsIgnoreCase(caseSensitive));
		logger.info("Search DONE. Results (" + (res!=null?res.size():0)+")");
		
		logger.info("-------------------------< "+className+" >-------------------------");

//		result.stream().forEach(System.out::println);
		
		for (Object searchResult : res) {
			logger.info(""+searchResult);
			//logger.info(searchResult.toString());
			
		}
//		result.stream().forEach(System.out::println);
		
	}

}
