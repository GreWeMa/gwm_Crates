package dev.gwm.spongeplugin.crates.listener;

import dev.gwm.spongeplugin.crates.GWMCrates;
import dev.gwm.spongeplugin.crates.event.PlayerOpenedCrateEvent;
import dev.gwm.spongeplugin.crates.superobject.manager.Manager;
import dev.gwm.spongeplugin.crates.utils.GWMCratesUtils;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.utils.Language;
import dev.gwm.spongeplugin.library.utils.Pair;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DebugCrateListener {

    private final Language language;

    private final DateTimeFormatter fileNameDateFormatter;
    private final DateTimeFormatter timeFormatter;

    private File logFile;

    private ScheduledFuture<?> scheduledFuture;

    public DebugCrateListener(Language language,
                              DateTimeFormatter fileNameDateFormatter, DateTimeFormatter timeFormatter) {
        this.language = language;
        this.fileNameDateFormatter = fileNameDateFormatter;
        this.timeFormatter = timeFormatter;
        reschedule();
    }

    @Listener(order = Order.LATE)
    public void onOpened(PlayerOpenedCrateEvent event) {
        Player player = event.getTargetEntity();
        Manager manager = event.getManager();
        String formattedDrops = GWMCratesUtils.formatDrops(event.getDrops());
        GWMCratesUtils.sendOpenMessage(player, manager, formattedDrops);
        if (GWMCrates.getInstance().isLogOpenedManagers()) {
            String time = LocalTime.now().withNano(0).format(timeFormatter);
            String playerName = player.getName();
            String playerUuid = player.getUniqueId().toString();
            String managerName = manager.getName();
            String managerId = manager.id();
            String formattedLocation = GWMCratesUtils.formatLocation(player.getLocation());
            try (OutputStream outputStream = new FileOutputStream(logFile, true)) {
                outputStream.write(GWMLibraryUtils.joinString(GWMCrates.getInstance().getLanguage().
                        getSimpleTranslation("OPENED_MANAGER_LOG_MESSAGE", Arrays.asList(
                                new Pair<>("TIME", time),
                                new Pair<>("PLAYER_NAME", playerName),
                                new Pair<>("PLAYER_UUID", playerUuid),
                                new Pair<>("MANAGER_NAME", managerName),
                                new Pair<>("MANAGER_ID", managerId),
                                new Pair<>("DROPS", formattedDrops),
                                new Pair<>("LOCATION", formattedLocation)
                        ))).getBytes(StandardCharsets.UTF_8));
                outputStream.write(System.getProperty("line.separator").getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().error("Failed to log opened crate!", e);
            }
        }
    }

    public void reschedule() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        if (GWMCrates.getInstance().isLogOpenedManagers()) {
            new UpdateRunnable().run();
        }
    }

    private class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            try {
                logFile = new File(GWMCrates.getInstance().getLogsDirectory(),
                        LocalDate.now().format(fileNameDateFormatter) + ".log");
                if (!logFile.exists() && !logFile.createNewFile()) {
                    GWMCrates.getInstance().getLogger().error("Failed to create a log file \"" + logFile.getAbsolutePath() + "\"!");
                }
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime updateTime = now.withNano(0).withSecond(0).withMinute(0).withHour(0).plusDays(1);
                Duration duration = Duration.between(now, updateTime);
                scheduledFuture = Executors.newScheduledThreadPool(1).
                        schedule(this, duration.toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().error("Failed to update a log file!", e);
            }
        }
    }

    public DateTimeFormatter getFileNameDateFormatter() {
        return fileNameDateFormatter;
    }

    public DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }

    public File getLogFile() {
        return logFile;
    }
}
