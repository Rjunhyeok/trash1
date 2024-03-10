package test.hscooldown;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class HSCoolDown extends JavaPlugin implements Listener {
    private HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final int COOLDOWN_TIME_SECONDS = 120;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("커스텀쿨리셋").setExecutor(this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("커스텀쿨리셋")) {
            if (!sender.hasPermission("cooldown.reset")) {
                sender.sendMessage("권한이 없습니다.");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§a§l/커스텀쿨리셋 <플레이어이름>");
                return true;
            }

            Player targetPlayer = getServer().getPlayer(args[0]);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                sender.sendMessage("§f§l[ §c§l! §f§l] §f§l플레이어가 온라인 상태가 아닙니다.");
                return true;
            }

            UUID targetUUID = targetPlayer.getUniqueId();
            cooldowns.remove(targetUUID);
            sender.sendMessage("§b§l" + targetPlayer.getName() + "§f§l플레이어의 쿨타임이 초기화되었습니다.");
            return true;
        }
        // 다른 명령어 처리
        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (event.getAction().name().contains("RIGHT")) {
            if (player.getItemInHand().getType() == Material.DIAMOND) {
                if (cooldowns.containsKey(uuid)) {
                    long secondsLeft = ((cooldowns.get(uuid) / 1000) + COOLDOWN_TIME_SECONDS) - (System.currentTimeMillis() / 1000);
                    if (secondsLeft > 0) {
                        player.sendMessage("§f§l[ §6§l커스텀 다람쥐 §f§l] §a§l쿨타임이 §e§l" + secondsLeft + "§a§l초 남았습니다.");
                        event.setCancelled(true);
                        return;
                    }
                }

                // 여기에 원하는 동작 추가

                // 쿨타임 설정
                cooldowns.put(uuid, System.currentTimeMillis());
                player.sendMessage("§f§l[ §6§l커스텀 다람쥐 §f§l] §6§l커스텀 다람쥐§e§l의 능력을 사용하였습니다!");
                getServer().getScheduler().runTaskLater(this, () -> {
                    player.sendMessage("§f§l[ §6§l커스텀 다람쥐 §f§l] §e§l이제 다시 §6§l커스텀 다람쥐§e§l의 능력을 사용할 수 있습니다!");
                }, COOLDOWN_TIME_SECONDS * 20);
            }
        }
    }
}