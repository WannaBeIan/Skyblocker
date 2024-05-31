package de.hysky.skyblocker.skyblock.tabhud.screenbuilder;

import com.google.common.reflect.ClassPath;
import de.hysky.skyblocker.events.SkyblockEvents;
import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.skyblock.tabhud.TabHud;
import de.hysky.skyblocker.skyblock.tabhud.util.PlayerListMgr;
import de.hysky.skyblocker.skyblock.tabhud.util.PlayerLocator;
import de.hysky.skyblocker.skyblock.tabhud.widget.HudWidget;
import de.hysky.skyblocker.skyblock.tabhud.widget.TabHudWidget;
import de.hysky.skyblocker.utils.Location;
import de.hysky.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ScreenMaster {

    private static final Logger LOGGER = LoggerFactory.getLogger("skyblocker");

    private static final int VERSION = 1;

    private static final HashMap<String, ScreenBuilder> standardMap = new HashMap<>();
    private static final HashMap<String, ScreenBuilder> screenAMap = new HashMap<>();
    private static final HashMap<String, ScreenBuilder> screenBMap = new HashMap<>();
    private static final Map<Location, ScreenBuilder> builderMap = new HashMap<>();

    public static final Map<String, HudWidget> widgetInstances = new HashMap<>();

    /**
     * Load a screen mapping from an identifier
     */
    public static void load(Identifier ident) {

        String path = ident.getPath();
        String[] parts = path.split("/");
        String screenType = parts[parts.length - 2];
        String location = parts[parts.length - 1];
        location = location.replace(".json", "");
    }

    public static ScreenBuilder getScreenBuilder(Location location) {
        return builderMap.get(location);
    }

    /**
     * Top level render method.
     * Calls the appropriate ScreenBuilder with the screen's dimensions
     */
    public static void render(DrawContext context, int w, int h) {
        String location = PlayerLocator.getPlayerLocation().internal;
        HashMap<String, ScreenBuilder> lookup;
        if (TabHud.toggleA.isPressed()) {
            lookup = screenAMap;
        } else if (TabHud.toggleB.isPressed()) {
            lookup = screenBMap;
        } else {
            lookup = standardMap;
        }

        ScreenBuilder sb = lookup.get(location);
        // seems suboptimal, maybe load the default first into all possible values
        // and then override?
        if (sb == null) {
            sb = lookup.get("default");
        }

        getScreenBuilder(Utils.getLocation()).run(context, w, h);

    }

    @Init
    public static void init() {

        SkyblockEvents.LOCATION_CHANGE.register(location -> ScreenBuilder.positionsNeedsUpdating = true);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            System.out.println(Object.class);
            try {
                ClassPath.from(TabHudWidget.class.getClassLoader()).getTopLevelClasses("de.hysky.skyblocker.skyblock.tabhud.widget").iterator().forEachRemaining(classInfo -> {
                    try {
                        Class<?> load = Class.forName(classInfo.getName());
                        if (!load.getSuperclass().equals(TabHudWidget.class)) return;
                        TabHudWidget tabHudWidget = (TabHudWidget) load.getDeclaredConstructor().newInstance();
                        PlayerListMgr.tabWidgetInstances.put(tabHudWidget.getHypixelWidgetName(), tabHudWidget);
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        LOGGER.error("[Skyblocker] Failed to load {} hud widget", classInfo.getName(), e);
                    }

                });
            } catch (Exception e) {
                LOGGER.error("[Skyblocker] Failed to get instances of hud widgets", e);
            }
        });

        for (Location value : Location.values()) {
            builderMap.put(value, new ScreenBuilder(value));
        }
        /*


        // WHY MUST IT ALWAYS BE SUCH NESTED GARBAGE MINECRAFT KEEP THAT IN DFU FFS

        ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.of(SkyblockerMod.NAMESPACE, "top_aligned"),
                SkyblockerMod.SKYBLOCKER_MOD,
                ResourcePackActivationType.NORMAL
        );

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                // ...why are we instantiating an interface again?
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.of(SkyblockerMod.NAMESPACE, "tabhud");
                    }

                    @Override
                    public void reload(ResourceManager manager) {

                        standardMap.clear();
                        screenAMap.clear();
                        screenBMap.clear();

                        int excnt = 0;

                        for (Map.Entry<Identifier, Resource> entry : manager
                                .findResources("tabhud", path -> path.getPath().endsWith("version.json"))
                                .entrySet()) {

                            try (BufferedReader reader = MinecraftClient.getInstance().getResourceManager()
                                    .openAsReader(entry.getKey())) {
                                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                                if (json.get("format_version").getAsInt() != VERSION) {
                                    throw new IllegalStateException(String.format("Resource pack isn't compatible! Expected version %d, got %d", VERSION, json.get("format_version").getAsInt()));
                                }

                            } catch (Exception ex) {
                                LOGGER.error("it borked", ex);
                            }
                        }

                        for (Map.Entry<Identifier, Resource> entry : manager
                                .findResources("tabhud", path -> path.getPath().endsWith(".json") && !path.getPath().endsWith("version.json"))
                                .entrySet()) {
                            try {

                                load(entry.getKey());
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage());
                                excnt++;
                            }
                        }
                        if (excnt > 0) {
                            LOGGER.warn("shit went down");
                        }
                    }
                });

         */
    }

}
