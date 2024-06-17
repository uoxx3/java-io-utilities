package uoxx3.io;

import org.jetbrains.annotations.NotNull;
import uoxx3.UAssert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class UResourceHash {
	
	/**
	 * This class cannot be instantiated
	 */
	private UResourceHash() {
		throw new IllegalStateException("This class cannot be instantiated");
	}
	
	/* -----------------------------------------------------
	 * Byte hash methods
	 * ----------------------------------------------------- */
	
	/**
	 * Computes the hash of the data read from the given input stream using the specified algorithm.
	 *
	 * @param stream the input stream to read data from
	 * @param algo   the hash algorithm to use
	 * @return the computed hash as a byte array
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the specified algorithm is not available
	 * @throws IllegalArgumentException if the stream or algorithm is null
	 */
	@SuppressWarnings("StatementWithEmptyBody")
	public static byte[] hash(@NotNull InputStream stream, @NotNull String algo) throws IOException, NoSuchAlgorithmException {
		UAssert.paramNotNull(stream, "InputStream stream");
		UAssert.paramNotNull(algo, "String algo");
		
		try (stream) {
			MessageDigest digest = MessageDigest.getInstance(algo);
			DigestInputStream digestStream = new DigestInputStream(stream, digest);
			byte[] buffer = new byte[UFSConstants.FS_BUFFER_SIZE];
			
			while (digestStream.read(buffer) != -1) {/*Do nothing*/}
			return digest.digest();
		}
	}
	
	/**
	 * Computes the hash of the data read from the given input stream using the default algorithm.
	 *
	 * @param stream the input stream to read data from
	 * @return the computed hash as a byte array
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the default algorithm is not available
	 * @throws IllegalArgumentException if the stream is null
	 */
	public static byte[] hash(@NotNull InputStream stream) throws IOException, NoSuchAlgorithmException {
		return hash(stream, UFSConstants.FS_HASH_ALGORITHM);
	}
	
	/**
	 * Computes the hash of the data read from the given path using the specified algorithm.
	 *
	 * @param path the path to read data from
	 * @param algo the hash algorithm to use
	 * @return the computed hash as a byte array
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the specified algorithm is not available
	 * @throws IllegalArgumentException if the path or algorithm is null
	 */
	public static byte[] hash(@NotNull Path path, @NotNull String algo) throws IOException, NoSuchAlgorithmException {
		UAssert.paramNotNull(path, "Path path");
		return hash(Files.newInputStream(path), algo);
	}
	
	/**
	 * Computes the hash of the data read from the given path using the default algorithm.
	 *
	 * @param path the path to read data from
	 * @return the computed hash as a byte array
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the default algorithm is not available
	 * @throws IllegalArgumentException if the path is null
	 */
	public static byte[] hash(@NotNull Path path) throws IOException, NoSuchAlgorithmException {
		return hash(path, UFSConstants.FS_HASH_ALGORITHM);
	}
	
	/**
	 * Computes the hash of the data read from the given file using the specified algorithm.
	 *
	 * @param file the file to read data from
	 * @param algo the hash algorithm to use
	 * @return the computed hash as a byte array
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the specified algorithm is not available
	 * @throws IllegalArgumentException if the file or algorithm is null
	 */
	public static byte[] hash(@NotNull File file, @NotNull String algo) throws IOException, NoSuchAlgorithmException {
		UAssert.paramNotNull(file, "File file");
		return hash(new FileInputStream(file), algo);
	}
	
	/**
	 * Computes the hash of the data read from the given file using the default algorithm.
	 *
	 * @param file the file to read data from
	 * @return the computed hash as a byte array
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the default algorithm is not available
	 * @throws IllegalArgumentException if the file is null
	 */
	public static byte[] hash(@NotNull File file) throws IOException, NoSuchAlgorithmException {
		return hash(file, UFSConstants.FS_HASH_ALGORITHM);
	}
	
	/**
	 * Computes the hash of the data read from the given zip entry using the specified algorithm.
	 *
	 * @param zip   the zip file containing the entry
	 * @param entry the zip entry to read data from
	 * @param algo  the hash algorithm to use
	 * @return the computed hash as a byte array
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the specified algorithm is not available
	 * @throws IllegalArgumentException if the zip file, entry, or algorithm is null
	 */
	public static byte[] hash(@NotNull ZipFile zip, @NotNull ZipEntry entry, @NotNull String algo) throws IOException,
		NoSuchAlgorithmException {
		UAssert.paramNotNull(zip, "ZipFile zip");
		UAssert.paramNotNull(entry, "ZipEntry entry");
		
		return hash(zip.getInputStream(entry), algo);
	}
	
	/**
	 * Computes the hash of the data read from the given zip entry using the default algorithm.
	 *
	 * @param zip   the zip file containing the entry
	 * @param entry the zip entry to read data from
	 * @return the computed hash as a byte array
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the default algorithm is not available
	 * @throws IllegalArgumentException if the zip file or entry is null
	 */
	public static byte[] hash(@NotNull ZipFile zip, @NotNull ZipEntry entry) throws IOException, NoSuchAlgorithmException {
		return hash(zip, entry, UFSConstants.FS_HASH_ALGORITHM);
	}
	
	/* -----------------------------------------------------
	 * String hash methods
	 * ----------------------------------------------------- */
	
	/**
	 * Converts the given byte array hash into a hexadecimal string representation.
	 *
	 * @param hash the byte array hash to convert
	 * @return the hexadecimal string representation of the hash
	 * @throws IllegalArgumentException if the hash is null
	 */
	public static @NotNull String hashStr(byte[] hash) {
		UAssert.paramNotNull(hash, "byte[] hash");
		return IntStream.range(0, hash.length)
			.map(i -> hash[i])
			.mapToObj(b -> String.format("%02x", b))
			.collect(Collectors.joining());
	}
	
	/**
	 * Computes the hash of the data read from the given input stream using the specified algorithm and returns it as a string.
	 *
	 * @param stream the input stream to read data from
	 * @param algo   the hash algorithm to use
	 * @return the computed hash as a string
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the specified algorithm is not available
	 * @throws IllegalArgumentException if the stream or algorithm is null
	 */
	public static @NotNull String hashStr(@NotNull InputStream stream, @NotNull String algo) throws IOException,
		NoSuchAlgorithmException {
		return hashStr(hash(stream, algo));
	}
	
	/**
	 * Computes the hash of the data read from the given input stream using the default algorithm and returns it as a string.
	 *
	 * @param stream the input stream to read data from
	 * @return the computed hash as a string
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the default algorithm is not available
	 * @throws IllegalArgumentException if the stream is null
	 */
	public static @NotNull String hashStr(@NotNull InputStream stream) throws IOException, NoSuchAlgorithmException {
		return hashStr(hash(stream));
	}
	
	/**
	 * Computes the hash of the data read from the given path using the specified algorithm and returns it as a string.
	 *
	 * @param path the path to read data from
	 * @param algo the hash algorithm to use
	 * @return the computed hash as a string
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the specified algorithm is not available
	 * @throws IllegalArgumentException if the path or algorithm is null
	 */
	public static @NotNull String hashStr(@NotNull Path path, @NotNull String algo) throws IOException,
		NoSuchAlgorithmException {
		return hashStr(hash(path, algo));
	}
	
	/**
	 * Computes the hash of the data read from the given path using the default algorithm and returns it as a string.
	 *
	 * @param path the path to read data from
	 * @return the computed hash as a string
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the default algorithm is not available
	 * @throws IllegalArgumentException if the path is null
	 */
	public static @NotNull String hashStr(@NotNull Path path) throws IOException, NoSuchAlgorithmException {
		return hashStr(hash(path));
	}
	
	/**
	 * Computes the hash of the data read from the given file using the specified algorithm and returns it as a string.
	 *
	 * @param file the file to read data from
	 * @param algo the hash algorithm to use
	 * @return the computed hash as a string
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the specified algorithm is not available
	 * @throws IllegalArgumentException if the file or algorithm is null
	 */
	public static @NotNull String hashStr(@NotNull File file, @NotNull String algo) throws IOException,
		NoSuchAlgorithmException {
		return hashStr(hash(file, algo));
	}
	
	/**
	 * Computes the hash of the data read from the given file using the default algorithm and returns it as a string.
	 *
	 * @param file the file to read data from
	 * @return the computed hash as a string
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the default algorithm is not available
	 * @throws IllegalArgumentException if the file is null
	 */
	public static @NotNull String hashStr(@NotNull File file) throws IOException, NoSuchAlgorithmException {
		return hashStr(hash(file));
	}
	
	/**
	 * Computes the hash of the data read from the given zip entry using the specified algorithm and returns it as a string.
	 *
	 * @param zip   the zip file containing the entry
	 * @param entry the zip entry to read data from
	 * @param algo  the hash algorithm to use
	 * @return the computed hash as a string
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the specified algorithm is not available
	 * @throws IllegalArgumentException if the zip file, entry, or algorithm is null
	 */
	public static @NotNull String hashStr(@NotNull ZipFile zip, @NotNull ZipEntry entry, @NotNull String algo) throws
		IOException, NoSuchAlgorithmException {
		return hashStr(hash(zip, entry, algo));
	}
	
	/**
	 * Computes the hash of the data read from the given zip entry using the default algorithm and returns it as a string.
	 *
	 * @param zip   the zip file containing the entry
	 * @param entry the zip entry to read data from
	 * @return the computed hash as a string
	 * @throws IOException              if an I/O error occurs
	 * @throws NoSuchAlgorithmException if the default algorithm is not available
	 * @throws IllegalArgumentException if the zip file or entry is null
	 */
	public static @NotNull String hashStr(@NotNull ZipFile zip, @NotNull ZipEntry entry) throws IOException,
		NoSuchAlgorithmException {
		return hashStr(hash(zip, entry));
	}
	
}