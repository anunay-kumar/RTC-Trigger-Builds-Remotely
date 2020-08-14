package com.devopscode.net;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

public class generateDepedencyPom {

		public static void main(String[] args) throws IOException {
			String workingDir = System.getProperty("user.dir");
			String libfolder = workingDir + "/lib";
			System.out.println(libfolder);
			String pathnames[];
			
			 File f = new File(libfolder);

		        // Populates the array with names of files and directories
		        pathnames = f.list();

		        // For each pathname in the pathnames array
		        System.out.println("	<dependencies>");
		        int i=0;
		        String mf_cp = "";
		        for (String pathname : pathnames) {
		        	i=i+1;
		        	mf_cp = mf_cp + " lib/" + pathname;
		            // Print the names of files and directories
		            System.out.println("		<dependency>");
		            System.out.println("			<groupId>" + "com.devopscode.net" + "</groupId>");
		            System.out.println("			<artifactId>" + pathname.split("_")[0] + "</artifactId>");
		            System.out.println("			<scope>system</scope>");
		            System.out.println("			<systemPath>${project.basedir}/lib/" + pathname + "</systemPath>");
		            System.out.println("			<version>" + "1." + i + "</version>");
		            System.out.println("		</dependency>");
		    		/*<dependency>
					<groupId>depGrp1</groupId>
					<artifactId>depArt1</artifactId>
					<version>apache-mime4j-0.6_apache-mime4j-0.6</version>
					<scope>system</scope>
					<systemPath>${project.basedir}/lib/apache-mime4j-0.6.jar</systemPath>
					</dependency>*/
		        }
		        System.out.println("	</dependencies>");
		        System.out.println(mf_cp);
			
		/*
           
        */
    }
}
