package uoxx3.io.internal;

import org.jetbrains.annotations.NotNull;
import uoxx3.UAssert;

import java.util.Optional;

public record JrtResourceInfo(
	@NotNull String resource,
	@NotNull Optional<String> moduleName
) {
	
	public JrtResourceInfo {
		UAssert.paramNotNull(resource, "String resource");
		UAssert.paramNotNull(moduleName, "Optional<String> moduleName");
	}
	
}
