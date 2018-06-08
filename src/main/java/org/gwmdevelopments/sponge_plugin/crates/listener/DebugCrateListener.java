package org.gwmdevelopments.sponge_plugin.crates.listener;

import org.apache.commons.io.FileUtils;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;

import java.io.File;
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
            try {
                String time = LocalTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME);
                String player_name = player.getName();
                String player_uuid = player.getUniqueId().toString();
                String manager_name = manager.getName();
                String manager_id = manager.getId();
                String drop_name = drop == null ? "null" : drop.getId().orElse("Unknown ID");
                Location<World> location = player.getLocation();
                String player_location = location.getExtent().getName() + ' ' + location.getBlockX() + ' ' + location.getBlockY() + ' ' + location.getBlockZ();
                FileUtils.writeStringToFile(LOG_FILE,
                        GWMCrates.getInstance().getLanguage().getPhrase("MANAGER_OPENING_LOG_MESSAGE",
                                new Pair<>("%TIME%", time),
                                new Pair<>("%PLAYER%", player_name),
                                new Pair<>("%PLAYER_UUID%", player_uuid),
                                new Pair<>("%MANAGER_NAME%", manager_name),
                                new Pair<>("%MANAGER_ID%", manager_id),
                                new Pair<>("%DROP%", drop_name),
                                new Pair<>("%LOCATION%", player_location)) + SEPARATOR,
                        StandardCharsets.UTF_8, true);
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
