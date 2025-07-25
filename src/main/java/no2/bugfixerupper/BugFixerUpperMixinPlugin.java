package no2.bugfixerupper;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class BugFixerUpperMixinPlugin implements IMixinConfigPlugin {
    private static final String CONFIG_FILE_NAME = "bug-fixer-upper-config.properties";
    private Properties config;

    @Override
    public void onLoad(String mixinPackage) {
        loadConfig();
    }

    private void loadConfig() {
        config = new Properties();

        // Try to load from config folder first, then from resources
        Path configPath = Paths.get("config", CONFIG_FILE_NAME);
        InputStream configStream = null;

        try {
            if (Files.exists(configPath)) {
                configStream = Files.newInputStream(configPath);
                config.load(configStream);
            } else {
                // Try to load from resources
                configStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
                if (configStream != null) {
                    config.load(configStream);
                } else {
                    // Create default config if it doesn't exist anywhere
                    createDefaultConfig(configPath);
                }
            }
        } catch (IOException e) {
            BugFixerUpper.LOGGER.error("Failed to load BugFixerUpper config, creating default: {}", e.getMessage());
            createDefaultConfig(configPath);
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private void createDefaultConfig(Path configPath) {
        Properties defaultConfig = new Properties();

        // Auto-detect all mixin packages
        Set<String> detectedPackages = detectMixinPackages();

        // Set all detected packages to true
        for (String packageName : detectedPackages) {
            defaultConfig.setProperty(packageName, "true");
        }

        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream out = Files.newOutputStream(configPath)) {
                defaultConfig.store(out, "BugFixerUpper Mixin Configuration\n" +
                    "Set to false to disable specific mixin packages\n" +
                    "Parent packages control their children (e.g., misc=false disables all misc.* packages)\n" +
                    "Children packages can only be configured if their parent packages are enabled");
            }
            config = defaultConfig;
            BugFixerUpper.LOGGER.info("Created default BugFixerUpper config at: {}", configPath);
        } catch (IOException e) {
            BugFixerUpper.LOGGER.error("Failed to create default config: {}", e.getMessage());
            config = defaultConfig;
        }
    }

    private Set<String> detectMixinPackages() {
        Set<String> packages = new java.util.HashSet<>();

        try {
            // Get all mixin classes from both main and client configurations
            detectPackagesFromMixinConfig("bug-fixer-upper.mixins.json", "no2.bugfixerupper.mixin", packages);
            detectPackagesFromMixinConfig("bug-fixer-upper.client.mixins.json", "no2.bugfixerupper.mixin.client", packages);
        } catch (Exception e) {
            BugFixerUpper.LOGGER.error("Failed to auto-detect mixin packages: {}", e.getMessage());
        }

        return packages;
    }

    private void detectPackagesFromMixinConfig(String configFileName, String basePackage, Set<String> packages) {
        try {
            InputStream configStream = getClass().getClassLoader().getResourceAsStream(configFileName);
            if (configStream == null) {
                return;
            }

            String jsonContent = new java.util.Scanner(configStream, java.nio.charset.StandardCharsets.UTF_8).useDelimiter("\\A").next();
            configStream.close();

            String mixinsSection = extractJsonArray(jsonContent, "mixins");
            extractPackages(basePackage, packages, mixinsSection);

            // Also check client array for client mixins
            String clientSection = extractJsonArray(jsonContent, "client");
            extractPackages(basePackage, packages, clientSection);

        } catch (Exception e) {
            BugFixerUpper.LOGGER.error("Failed to parse mixin config {}: {}", configFileName, e.getMessage());
        }
    }

    private void extractPackages(String basePackage, Set<String> packages, String clientSection) {
        if (clientSection != null) {
            String[] clientEntries = clientSection.split(",");
            for (String entry : clientEntries) {
                String mixinClass = entry.trim().replaceAll("\"", "").trim();
                if (!mixinClass.isEmpty()) {
                    String fullClassName = basePackage + "." + mixinClass;
                    String packagePath = extractPackagePath(fullClassName);
                    if (packagePath != null) {
                        addPackageHierarchy(packagePath, packages);
                    }
                }
            }
        }
    }

    private String extractJsonArray(String json, String arrayName) {
        String pattern = "\"" + arrayName + "\"\\s*:\\s*\\[([^]]+)]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private void addPackageHierarchy(String packagePath, Set<String> packages) {
        // Add the full package path and all parent packages
        String[] parts = packagePath.split("\\.");

        for (int i = 1; i <= parts.length; i++) {
            String currentPath = String.join(".", java.util.Arrays.copyOfRange(parts, 0, i));
            packages.add(currentPath);
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Extract full package path from mixin class name
        String packagePath = extractPackagePath(mixinClassName);

        if (packagePath != null) {
            return isPackageEnabled(packagePath);
        }
        BugFixerUpper.LOGGER.warn("Fallback to enabled for mixin class {}", mixinClassName);
        return true;
    }

    private String extractPackagePath(String mixinClassName) {
        // no2.bugfixerupper.mixin.chunkload.item_frame_sound.ItemFrameMixin -> chunkload.item_frame_sound

        String basePackage = "no2.bugfixerupper.mixin.";
        if (mixinClassName.startsWith(basePackage)) {
            String remainder = mixinClassName.substring(basePackage.length());

            // Remove the final class name to get just the package path
            int lastDotIndex = remainder.lastIndexOf('.');
            if (lastDotIndex > 0) {
                return remainder.substring(0, lastDotIndex);
            }
        }

        return null;
    }

    private boolean isPackageEnabled(String packagePath) {
        // Check hierarchical dependency: all parent packages must be enabled for a child package to be enabled

        String[] parts = packagePath.split("\\.");

        for (int i = 1; i <= parts.length; i++) {
            String currentPath = String.join(".", java.util.Arrays.copyOfRange(parts, 0, i));
            String value = config.getProperty(currentPath);

            if (value != null) {
                boolean enabled = Boolean.parseBoolean(value);
                if (!enabled) {
                    return false;
                }
            }
        }

        // Default: enabled
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
