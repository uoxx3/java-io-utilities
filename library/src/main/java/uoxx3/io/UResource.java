package uoxx3.io;

import org.jetbrains.annotations.NotNull;
import uoxx3.UAssert;
import uoxx3.io.internal.JrtResourceInfo;
import uoxx3.io.internal.UResourceActions;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.ZipEntry;

public final class UResource extends UResourceActions {
	
	/**
	 * This class cannot be instantiated
	 */
	private UResource() {
		throw new IllegalStateException("This class cannot be instantiated");
	}
	
	/* -----------------------------------------------------
	 * User location methods
	 * ----------------------------------------------------- */
	
	/**
	 * Returns a Path object representing the user directory with the specified location.
	 *
	 * @param location additional path elements to be appended to the user directory
	 * @return a Path object representing the user directory with the specified location
	 * @throws IllegalArgumentException if the location is null
	 */
	public static @NotNull Path userDirectory(String @NotNull ... location) {
		return Path.of(
			System.getProperty("user.home"),
			location
		);
	}
	
	/**
	 * Returns a Path object representing the user directory with the specified top-level directory and location.
	 *
	 * @param top      the top-level directory to be included in the path
	 * @param location additional path elements to be appended to the user directory
	 * @return a Path object representing the user directory with the specified top-level directory and location
	 * @throws IllegalArgumentException if the top or location is null
	 */
	public static @NotNull Path userDirectory(@NotNull String top, String @NotNull ... location) {
		String[] realLocation = new String[location.length + 1];
		realLocation[0] = top;
		
		System.arraycopy(location, 0, realLocation, 1, location.length);
		return userDirectory(realLocation);
	}
	
	/* -----------------------------------------------------
	 * Basename methods
	 * ----------------------------------------------------- */
	
	/**
	 * Returns the basename of the given location.
	 *
	 * @param location      the full location string
	 * @param isDirectory   whether the location is a directory
	 * @param partialResult if true, returns the basename without the extension; otherwise, includes the extension
	 * @return the basename of the location
	 * @throws IllegalArgumentException if the location is null
	 */
	public static @NotNull String basename(@NotNull String location, boolean isDirectory, boolean partialResult) {
		UAssert.paramNotNull(location, "String location");
		// Eliminate all separator characters and only keep the
		// last part of the resource
		location = resourceWithoutSlashes(location);
		// Directories do not contain extensions and should
		// not be processed by default
		if (isDirectory) return location;
		
		// The partial result means that only the file name will be
		// returned, but without any extension; otherwise, the file
		// name with the extensions will be returned.
		if (!partialResult) return location;
		
		int index = location.indexOf(UFSConstants.FS_EXTENSION_IDENTIFIER);
		return index == -1 ? location : location.substring(0, index);
	}
	
	/**
	 * Returns the basename of the given location, excluding the extension.
	 *
	 * @param location    the full location string
	 * @param isDirectory whether the location is a directory
	 * @return the basename of the location without the extension
	 * @throws IllegalArgumentException if the location is null
	 */
	public static @NotNull String basename(@NotNull String location, boolean isDirectory) {
		return basename(location, isDirectory, true);
	}
	
	/**
	 * Returns the basename of the given path.
	 *
	 * @param path          the path object
	 * @param partialResult if true, returns the basename without the extension; otherwise, includes the extension
	 * @return the basename of the path
	 * @throws IllegalArgumentException if the path is null
	 */
	public static @NotNull String basename(@NotNull Path path, boolean partialResult) {
		UAssert.paramNotNull(path, "Path path");
		return basename(path.getFileName().toString(),
						Files.isDirectory(path),
						partialResult);
	}
	
	/**
	 * Returns the basename of the given path, excluding the extension.
	 *
	 * @param path the path object
	 * @return the basename of the path without the extension
	 * @throws IllegalArgumentException if the path is null
	 */
	public static @NotNull String basename(@NotNull Path path) {
		return basename(path, true);
	}
	
	/**
	 * Returns the basename of the given file.
	 *
	 * @param file          the file object
	 * @param partialResult if true, returns the basename without the extension; otherwise, includes the extension
	 * @return the basename of the file
	 * @throws IllegalArgumentException if the file is null
	 */
	public static @NotNull String basename(@NotNull File file, boolean partialResult) {
		UAssert.paramNotNull(file, "File file");
		return basename(file.getName(),
						file.isDirectory(),
						partialResult);
	}
	
	/**
	 * Returns the basename of the given file, excluding the extension.
	 *
	 * @param file the file object
	 * @return the basename of the file without the extension
	 * @throws IllegalArgumentException if the file is null
	 */
	public static @NotNull String basename(@NotNull File file) {
		return basename(file, true);
	}
	
	/**
	 * Returns the basename of the given ZipEntry.
	 *
	 * @param entry         the ZipEntry object
	 * @param partialResult if true, returns the basename without the extension; otherwise, includes the extension
	 * @return the basename of the ZipEntry
	 * @throws IllegalArgumentException if the ZipEntry is null
	 */
	public static @NotNull String basename(@NotNull ZipEntry entry, boolean partialResult) {
		UAssert.paramNotNull(entry, "ZipEntry entry");
		return basename(entry.getName(),
						entry.isDirectory(),
						partialResult);
	}
	
	/**
	 * Returns the basename of the given ZipEntry, excluding the extension.
	 *
	 * @param entry the ZipEntry object
	 * @return the basename of the ZipEntry without the extension
	 * @throws IllegalArgumentException if the ZipEntry is null
	 */
	public static @NotNull String basename(@NotNull ZipEntry entry) {
		return basename(entry, true);
	}
	
	/* -----------------------------------------------------
	 * Extensions methods
	 * ----------------------------------------------------- */
	
	/**
	 * Returns an array of extensions of the given location string.
	 *
	 * @param location the full location string
	 * @return an array of extensions of the location; if no extensions are found, returns an empty array
	 * @throws IllegalArgumentException if the location is null
	 */
	public static String @NotNull [] extensions(@NotNull String location) {
		UAssert.paramNotNull(location, "String location");
		// Eliminate all separator characters and only keep the
		// last part of the resource
		String locationWithoutExt = basename(location, false);
		location = resourceWithoutSlashes(location);
		int index = location.indexOf(UFSConstants.FS_EXTENSION_IDENTIFIER);
		
		checkResource:
		{
			if (index == -1) break checkResource;
			
			String[] extensions = location.split("\\.");
			return Arrays.stream(extensions)
				.filter(e -> !locationWithoutExt.contentEquals(e))
				.toArray(String[]::new);
		}
		return UFSConstants.FS_EMPTY_EXTENSIONS;
	}
	
	/**
	 * Returns an array of extensions of the given path.
	 *
	 * @param path the path object
	 * @return an array of extensions of the path; if no extensions are found, returns an empty array
	 * @throws IllegalArgumentException if the path is null
	 * @throws IllegalArgumentException if the path is a directory
	 */
	public static String @NotNull [] extensions(@NotNull Path path) {
		UAssert.paramNotNull(path, "Path path");
		if (Files.isDirectory(path)) {
			resourceTypeError("Regular File", "Directory");
		}
		
		return extensions(path.getFileName().toString());
	}
	
	/**
	 * Returns an array of extensions of the given file.
	 *
	 * @param file the file object
	 * @return an array of extensions of the file; if no extensions are found, returns an empty array
	 * @throws IllegalArgumentException if the file is null
	 * @throws IllegalArgumentException if the file is a directory
	 */
	public static String @NotNull [] extensions(@NotNull File file) {
		UAssert.paramNotNull(file, "File file");
		if (file.isDirectory()) {
			resourceTypeError("Regular File", "Directory");
		}
		
		return extensions(file.getName());
	}
	
	/**
	 * Returns an array of extensions of the given ZipEntry.
	 *
	 * @param entry the ZipEntry object
	 * @return an array of extensions of the ZipEntry; if no extensions are found, returns an empty array
	 * @throws IllegalArgumentException if the entry is null
	 * @throws IllegalArgumentException if the entry is a directory
	 */
	public static String @NotNull [] extensions(@NotNull ZipEntry entry) {
		UAssert.paramNotNull(entry, "ZipEntry entry");
		if (entry.isDirectory()) {
			resourceTypeError("Regular File", "Directory");
		}
		
		return extensions(entry.getName());
	}
	
	/* -----------------------------------------------------
	 * Extension methods
	 * ----------------------------------------------------- */
	
	/**
	 * Returns the extension of the given location string.
	 *
	 * @param location the full location string
	 * @return an Optional containing the extension of the location if present, otherwise an empty Optional
	 * @throws IllegalArgumentException if the location is null
	 */
	public static @NotNull Optional<String> extension(@NotNull String location) {
		UAssert.paramNotNull(location, "String location");
		String[] extensions = extensions(location);
		return extensions.length == 0 ? Optional.empty() :
			   Optional.of(extensions[extensions.length - 1]);
	}
	
	/**
	 * Returns the extension of the given path.
	 *
	 * @param path the path object
	 * @return an Optional containing the extension of the path if present, otherwise an empty Optional
	 * @throws IllegalArgumentException if the path is null
	 * @throws IllegalArgumentException if the path is a directory
	 */
	public static @NotNull Optional<String> extension(@NotNull Path path) {
		UAssert.paramNotNull(path, "Path path");
		if (Files.isDirectory(path)) {
			resourceTypeError("Regular File", "Directory");
		}
		
		return extension(path.getFileName().toString());
	}
	
	/**
	 * Returns the extension of the given file.
	 *
	 * @param file the file object
	 * @return an Optional containing the extension of the file if present, otherwise an empty Optional
	 * @throws IllegalArgumentException if the file is null
	 * @throws IllegalArgumentException if the file is a directory
	 */
	public static @NotNull Optional<String> extension(@NotNull File file) {
		UAssert.paramNotNull(file, "File file");
		if (file.isDirectory()) {
			resourceTypeError("Regular File", "Directory");
		}
		
		return extension(file.getName());
	}
	
	/**
	 * Returns the extension of the given ZipEntry.
	 *
	 * @param entry the ZipEntry object
	 * @return an Optional containing the extension of the ZipEntry if present, otherwise an empty Optional
	 * @throws IllegalArgumentException if the entry is null
	 * @throws IllegalArgumentException if the entry is a directory
	 */
	public static @NotNull Optional<String> extension(@NotNull ZipEntry entry) {
		UAssert.paramNotNull(entry, "ZipEntry entry");
		if (entry.isDirectory()) {
			resourceTypeError("Regular File", "Directory");
		}
		
		return extension(entry.getName());
	}
	
	/* -----------------------------------------------------
	 * Path from other resources
	 * ----------------------------------------------------- */
	
	/**
	 * Converts the given URL to a Path object.
	 *
	 * @param url the URL to convert to a Path
	 * @return a Path object representing the given URL
	 * @throws IOException              if an I/O error occurs or if the URL cannot be converted to a URI
	 * @throws IllegalArgumentException if the URL is null
	 */
	public static @NotNull Path pathOf(@NotNull URL url) throws IOException {
		try {
			return pathOf(url.toURI());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Converts the given URI to a Path object, handling different URI schemes.
	 *
	 * @param uri the URI to convert to a Path
	 * @return a Path object representing the given URI
	 * @throws IOException              if an I/O error occurs or if the URI scheme is not supported
	 * @throws IllegalArgumentException if the URI is null
	 */
	@SuppressWarnings("resource")
	public static @NotNull Path pathOf(@NotNull URI uri) throws IOException {
		String scheme = uri.getScheme();
		
		return switch (scheme) {
			case "file" -> Path.of(uri);
			case "jar" -> {
				FileSystem fs = UFilesystem.filesystem(uri);
				
				// Extract the path of the resource to access within the JAR file
				yield fs.getPath(extractJarLocation(uri));
			}
			case "jrt" -> {
				// Handle the JVM bug for jlink images where URIs need to be reconstructed
				String uriLocation = UFSConstants.FS_JRT_SCHEME;
				JrtResourceInfo jrtInfo = extractJrtInfo(uri);
				
				if (jrtInfo.moduleName().isPresent()) {
					uriLocation += jrtInfo.moduleName().get();
					// Add file separator if needed
					if (!jrtInfo.resource().isBlank() && !jrtInfo.resource().startsWith(
						String.valueOf(UFSConstants.FS_FILE_SEPARATOR))) {
						uriLocation += UFSConstants.FS_FILE_SEPARATOR;
					}
				}
				
				if (!jrtInfo.resource().isBlank()) {
					uriLocation += jrtInfo.resource();
				}
				
				yield Paths.get(URI.create(uriLocation));
			}
			default -> throw new IOException();
		};
	}
	
}