package main;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import engine.Window;

public class Run {
	public static void main(String[] args) throws Exception {
		System.out.println("main");
		
		Window.get().run();
//		File directory = new File(".");
//	    File[] subDirectories = directory.listFiles(File::isDirectory);
//	    for (File subDirectory : subDirectories) {
//	      System.out.println(subDirectory.getName());
//	    }
	}
}
