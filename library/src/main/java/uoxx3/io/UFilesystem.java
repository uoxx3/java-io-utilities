package uoxx3.io;

import org.jetbrains.annotations.NotNull;
import uoxx3.UAssert;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public final class UFilesystem {
	
	private static final Set<FileSystem> filesystems = Collections.synchronizedSet(new HashSet<>());
	
	/* -----------------------------------------------------
	 * Constants
	 * ----------------------------------------------------- */
	
	/**
	 * This class cannot be instantiated
	 */
	private UFilesystem() {
		throw new IllegalStateException("This class cannot be instantiated");
	}
	
	/* -----------------------------------------------------
	 * Filesystem methods
	 * ----------------------------------------------------- */
	
	/**
	 * Returns a FileSystem object for the given URL.
	 *
	 * @param url the URL to convert to a FileSystem
	 * @return a FileSystem object for the given URL
	 * @throws IOException              if an I/O error occurs or if the URL cannot be converted to a URI
	 * @throws IllegalArgumentException if the URL is null
	 */
	public static @NotNull FileSystem filesystem(@NotNull URL url) throws IOException {
		try {
			UAssert.paramNotNull(url, "URL url");
			return filesystem(url.toURI());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Returns a FileSystem object for the given URI.
	 *
	 * @param uri the URI to convert to a FileSystem
	 * @return a FileSystem object for the given URI
	 * @throws IOException              if an I/O error occurs or if the URI scheme is not supported
	 * @throws IllegalArgumentException if the URI is null
	 */
	public static @NotNull FileSystem filesystem(@NotNull URI uri) throws IOException {
		UAssert.paramNotNull(uri, "URI uri");
		String scheme = uri.getScheme();
		
		return switch (scheme) {
			case "file" -> Path.of(uri).getFileSystem();
			case "jar" -> {
				// Generate new filesystem
				FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>());
				Optional<FileSystem> found = filesystems.stream()
					.filter(fs::equals)
					.findFirst();
				
				if (found.isPresent()) {
					fs.close();
					yield found.get();
				}
				
				filesystems.add(fs);
				yield fs;
			}
			default -> throw new IOException();
		};
	}
	
	/* -----------------------------------------------------
	 * Close filesystem methods
	 * ----------------------------------------------------- */
	
	/**
	 * Closes and removes file systems from the collection based on the given condition.
	 *
	 * @param action a function that returns true for file systems that should be closed and removed
	 * @throws IllegalArgumentException if the action is null
	 */
	public static void closeFilesystemsIf(@NotNull Function<FileSystem, Boolean> action) {
		UAssert.paramNotNull(action, "Function<FileSystem, Boolean> action");
		// Get filesystem iterator
		Iterator<FileSystem> iterator = filesystems.iterator();
		
		// We perform an iteration this way, because it allows
		// us to make changes to the collection at the same time as iterate.
		while (iterator.hasNext()) {
			FileSystem fs = iterator.next();
			
			if (action.apply(fs)) {
				try {
					if (fs.isOpen()) fs.close();
				} catch (IOException ignore) {
				}
				iterator.remove();
			}
		}
	}
	
	/**
	 * Closes and removes all file systems from the collection.
	 */
	public static void closeFilesystems() {
		closeFilesystemsIf((ignore) -> true);
	}
	
}