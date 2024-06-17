package uoxx3.io;

import uoxx3.platform.UArchitecture;

import static uoxx3.platform.UArchitecture.currentRunningArchitecture;

/**
 * Interface defining constants for file system operations.
 */
public interface UFSConstants {
	
	/**
	 * An empty array of strings to represent no file extensions.
	 */
	String[] FS_EMPTY_EXTENSIONS = new String[0];
	
	/**
	 * The character used to identify file extensions in file names.
	 */
	char FS_EXTENSION_IDENTIFIER = '.';
	
	/**
	 * The character used as a file separator in the file system.
	 */
	char FS_FILE_SEPARATOR = '/';
	
	/**
	 * The character used to separate resources within a JAR file.
	 */
	char FS_JAR_RESOURCE_SEPARATOR = '!';
	
	/**
	 * The buffer size used for file operations, set to 4 kilobytes.
	 */
	int FS_BUFFER_SIZE = 4 << 10;
	
	/**
	 * The scheme used for Java runtime image file system URIs.
	 */
	String FS_JRT_SCHEME = "jrt:/";
	
	/**
	 * The hash algorithm used for file operations. If the current architecture
	 * is x64, it uses SHA-512; otherwise, it uses SHA-256.
	 */
	String FS_HASH_ALGORITHM = (currentRunningArchitecture() == UArchitecture.X64) ?
							   "SHA-512" : "SHA-256";
	
}