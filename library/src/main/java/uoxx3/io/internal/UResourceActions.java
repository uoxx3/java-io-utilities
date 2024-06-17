package uoxx3.io.internal;

import org.jetbrains.annotations.NotNull;
import uoxx3.UAssert;
import uoxx3.io.UFSConstants;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

public abstract class UResourceActions {
	
	/* -----------------------------------------------------
	 * Methods
	 * ----------------------------------------------------- */
	
	/**
	 * Throws an IllegalArgumentException indicating that the resource type is invalid.
	 *
	 * @param expected the expected resource type
	 * @param given    the given resource type
	 * @throws IllegalArgumentException always thrown with a message indicating the mismatch between expected and given resource types
	 */
	protected static void resourceTypeError(@NotNull String expected, @NotNull String given) {
		throw new IllegalArgumentException(
			"The resource is not a valid \"%s\" type. \"%s\" given".formatted(expected, given));
	}
	
	/**
	 * Removes all slashes from the given location string and returns the last part of the resource name.
	 *
	 * @param location the full location string
	 * @return the location string without any leading or trailing slashes, and only the last part of the resource name
	 * @throws IllegalArgumentException if the location is null
	 */
	protected static @NotNull String resourceWithoutSlashes(@NotNull String location) {
		UAssert.paramNotNull(location, "String location");
		// File separators
		String commonSeparator = String.valueOf(UFSConstants.FS_FILE_SEPARATOR);
		String cleanLocation = location.replaceAll("[/|\\\\]+", commonSeparator)
			.trim();
		
		// Check that the location does not end with a slash
		while (cleanLocation.endsWith(commonSeparator)) {
			cleanLocation = cleanLocation.substring(0, cleanLocation.length() - 1);
		}
		
		// Only get the last element after a '/' character
		int slashIdx = cleanLocation.lastIndexOf(commonSeparator);
		return cleanLocation.substring(slashIdx == -1 ? 0 : slashIdx + 1);
	}
	
	/**
	 * Extracts the location within a JAR file from the given URI.
	 *
	 * @param uri the URI to extract the location from
	 * @return the location within the JAR file as a string
	 * @throws IllegalArgumentException if the URI is null
	 */
	protected static @NotNull String extractJarLocation(@NotNull URI uri) {
		UAssert.paramNotNull(uri, "URI uri");
		String uriStr = uri.toString();
		String location = String.valueOf(UFSConstants.FS_FILE_SEPARATOR);
		int index = uriStr.lastIndexOf(UFSConstants.FS_JAR_RESOURCE_SEPARATOR);
		
		// Verify that the resource path exists
		if (index != -1) {
			int startIdx = index + 1;
			location = startIdx >= uriStr.length() ?
					   uriStr.substring(index) :
					   uriStr.substring(startIdx);
		}
		return location;
	}
	
	/**
	 * Extracts module information and resource path from a Java runtime image URI.
	 *
	 * @param uri the URI to extract the information from
	 * @return a JrtResourceInfo object containing the resource path and an optional module name
	 * @throws IllegalArgumentException if the URI is null
	 */
	protected static @NotNull JrtResourceInfo extractJrtInfo(@NotNull URI uri) {
		UAssert.paramNotNull(uri, "URI uri");
		
		// Convert chars to Strings
		String fileSeparator = String.valueOf(UFSConstants.FS_FILE_SEPARATOR);
		String extensionIdentifier = String.valueOf(UFSConstants.FS_EXTENSION_IDENTIFIER);
		
		// Split URI into sections
		String[] uriSections = uri.toString().split(fileSeparator);
		
		// Determine module name and the starting index for resource path
		String moduleName =
			(uriSections.length > 1 && !uriSections[1].contains(extensionIdentifier) && !uriSections[1].isBlank()) ?
			uriSections[1] : null;
		int fromIndex = (moduleName == null) ? 1 : 2;
		
		// Construct the resource path
		String resourcePath = String.join(fileSeparator, Arrays.copyOfRange(uriSections, fromIndex, uriSections.length));
		return new JrtResourceInfo(resourcePath, Optional.ofNullable(moduleName));
	}
	
	
}