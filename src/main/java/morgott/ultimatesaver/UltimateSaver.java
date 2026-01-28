package morgott.ultimatesaver;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class UltimateSaver extends JavaPlugin {

    private static UltimateSaver instance;

    public static UltimateSaver get() {
        return instance;
    }

    public UltimateSaver(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("[UltimateSaver] Loaded! Each weapon stores its own ultimate charge.");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("[UltimateSaver] Stopped!");
    }
}
