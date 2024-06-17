module uo.io.utilities {
	// Required packages
	requires java.net.http;
	requires java.xml.crypto;
	requires java.xml;
	
	requires uo.core.utilities;
	requires static org.jetbrains.annotations;
	
	// Export packages
	exports uoxx3.io;
	
	// Open resources
	opens uoxx3.io;
}