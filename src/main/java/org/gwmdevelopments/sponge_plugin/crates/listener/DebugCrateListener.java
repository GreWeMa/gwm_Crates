package org.gwmdevelopments.sponge_plugin.crates.listener;

import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DebugCrateListener {

    public static final String SEPARATOR = System.getProperty("line.separator");
    public static File LOG_FILE;

    static {
        new UpdateRunnable().run();
    }

    @Listener(order = Order.LATE)
    public void onOpened(PlayerOpenedCrateEvent event) {
        Player player = event.getPlayer();
        Manager manager = event.getManager();
        Drop drop = event.getDrop();
        if (manager.isSendOpenMessage()) {
            Optional<String> optionalCustomOpenMessage = manager.getCustomOpenMessage();
            if (optionalCustomOpenMessage.isPresent()) {
                player.sendMessage(TextSerializers.FORMATTING_CODE.
                        deserialize(optionalCustomOpenMessage.get().
                                replace("%MANAGER%", manager.getName())));
            } else {
                player.sendMessage(GWMCrates.getInstance().getLanguage().getText("SUCCESSFULLY_OPENED_MANAGER",
                        new Pair<>("%MANAGER%", manager.getName())));
            }
        }
        if (GWMCrates.getInstance().isLogOpenedCrates()) {
            String time = LocalTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME);
            String playerName = player.getName();
            String playerUuid = player.getUniqueId().toString();
            String managerName = manager.getName();
            String managerId = manager.getId();
            String dropName = drop == null ? "null" : drop.getId().orElse("Unknown ID");
            Location<World> location = player.getLocation();
            String playerLocation = location.getExtent().getName() + ' ' +
                    location.getBlockX() + ' ' +
                    location.getBlockY() + ' ' +
                    location.getBlockZ();
            try (OutputStream outputStream = new FileOutputStream(LOG_FILE, true)) {
                outputStream.write((GWMCrates.getInstance().getLanguage().getPhrase("MANAGER_OPENING_LOG_MESSAGE",
                        new Pair<>("%TIME%", time),
                        new Pair<>("%PLAYER%", playerName),
                        new Pair<>("%PLAYER_UUID%", playerUuid),
                        new Pair<>("%MANAGER_NAME%", managerName),
                        new Pair<>("%MANAGER_ID%", managerId),
                        new Pair<>("%DROP%", dropName),
                        new Pair<>("%LOCATION%", playerLocation)) + SEPARATOR).
                        getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Failed to log opened crate!", e);
            }
        }
    }

    static class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            try {
                LOG_FILE = new File(GWMCrates.getInstance().getLogsDirectory(),
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".log");
                if (!LOG_FILE.exists()) {
                    LOG_FILE.createNewFile();
                }
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime updateTime = now.withNano(0).withSecond(0).withMinute(0).withHour(0).plusDays(1);
                Duration duration = Duration.between(now, updateTime);
                scheduler.schedule(this, duration.toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Failed to update log file!", e);
            }
        }
    }
}
