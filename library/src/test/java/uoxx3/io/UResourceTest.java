package uoxx3.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uoxx3.platform.UPlatform;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

class UResourceTest {
	
	@Test
	public void userDirectoryTest() {
		Path userDir = UResource.userDirectory();
		String userDirStr = userDir.toString();
		
		switch (UPlatform.currentRunningPlatform()) {
			case WINDOWS -> Assertions.assertTrue(userDirStr.startsWith("C:\\Users\\"),
												  "Invalid location");
			case LINUX -> Assertions.assertTrue(userDirStr.startsWith("/home/"),
												"Invalid location");
			case MACOS -> Assertions.assertTrue(userDirStr.startsWith("/Users/"),
												"Invalid location");
			case SOLARIS -> Assertions.assertTrue(userDirStr.startsWith("/export/home/"),
												  "Invalid location");
			case FREE_BSD -> Assertions.assertTrue(userDirStr.startsWith("/usr/home/"),
												   "Invalid location");
			default -> throw new IllegalArgumentException("Platform not supported");
		}
		
		System.out.println(userDir);
	}
	
	@Test
	public void basenameTest() {
		String resource = "/opt/info/example.local.txt";
		
		String expectedBasename = "example";
		Assertions.assertEquals(expectedBasename, UResource.basename(resource, false),
								"Invalid basename");
		
		String expectedFilename = "example.local.txt";
		Assertions.assertEquals(expectedFilename, UResource.basename(resource, false, false),
								"Invalid filename");
		
		System.out.printf("Original resource: %s%n", resource);
		System.out.printf("Basename: %s%n", UResource.basename(resource, false));
		System.out.printf("Filename: %s%n", UResource.basename(resource, false, false));
	}
	
	@Test
	public void extensionsTest() {
		String resource = "/opt/info/example.local.txt";
		String[] extensions = UResource.extensions(resource);
		String[] expected = {"local", "txt"};
		
		Assertions.assertArrayEquals(expected, extensions,
									 "The extensions are not equals");
		
		System.out.printf("Resource name: %s%n", resource);
		System.out.printf("Expected: %s%n", Arrays.toString(expected));
		System.out.printf("Result: %s%n", Arrays.toString(extensions));
	}
	
	@Test
	public void extensionTest() {
		String resource = "/opt/info/example.local.txt";
		Optional<String> extension = UResource.extension(resource);
		String expected = "txt";
		
		Assertions.assertTrue(extension.isPresent(),
							  "Extension is not present");
		Assertions.assertEquals(expected, extension.get(),
								"The extensions are not equals");
		
		System.out.printf("Resource name: %s%n", resource);
		System.out.printf("Expected: %s%n", expected);
		System.out.printf("Result: %s%n", extension);
	}
	
	@Test
	public void pathOfTest() throws IOException {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		URL resource = loader.getResource("uoxx3/io/example.txt");
		
		Assertions.assertNotNull(resource,
								 "Cannot load the resource");
		
		Path resourcePath = UResource.pathOf(resource);
		System.out.println(resourcePath);
	}
	
}