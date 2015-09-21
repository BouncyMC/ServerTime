package Staartvin.GlobalTime;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GlobalTime extends JavaPlugin {

	Calendar calendar = Calendar.getInstance();
	String[] array = {};
	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	Boolean firstRun = true;
	
	public void onEnable() {
		loadConfiguration();
		startBroadcasting();
		System.out.println("[" + getDescription().getName()
				+ "] has been enabled!");
	}

	public void onDisable() {
		reloadConfig();
		saveConfig();
		getServer().getScheduler().cancelAllTasks();
		System.out.println("[" + getDescription().getName()
				+ "] has been disabled!");
	}
	
	protected void reload() {
		getServer().getPluginManager().disablePlugin(this);
		getServer().getPluginManager().enablePlugin(this);
		System.out.print("[Global Time] Reloaded!");
	}

	protected void loadConfiguration() {
		getConfig().options().header(
				"Global Time v" + getDescription().getVersion() + " Config"
				+ "\nGlobal times is a list of times shown when a player performs the /gt command"
				+ "\nA list of timezones can be found here: http://dev.bukkit.org/server-mods/global-time/pages/list-of-times/"
				+ "\n\n Thanks for using Global Time! Questions? http://dev.bukkit.org/server-mods/global-time/");

		getConfig().addDefault("verboseLogging", true);
		getConfig().addDefault("IntervalTime", 60);
		getConfig().addDefault("BroadcastMessage", "§eCurrent Machine Time is §3(%timezone%) §4%time% §2%date%");
		
		if (getConfig().getList("Global times") == null) {
			getConfig().set("Global times", Arrays.asList(array));
			List<String> times = getConfig().getStringList("Global times");
			times.add("Europe/Amsterdam");
			times.add("US/Hawaii");
			times.add("Africa/Cairo");
			getConfig().set("Global times", times);
		}
		
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandlabel, String[] args) {

		if (cmd.getName().equalsIgnoreCase("globaltime")) {
			
			if (args.length == 0) {
				if (!sender.hasPermission("globaltime.time")) {
					sender.sendMessage(ChatColor.RED + "You don't have the correct permission to check the global time!");
					return true;
				}
				for (String timezone: getConfig().getStringList("Global times")) {
					Calendar timezone_calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
					sender.sendMessage(ChatColor.GREEN + "Time in Adelaide, Australia:" + ChatColor.AQUA + formatTime(timezone_calendar) + ChatColor.YELLOW + formatDate(calendar));
				}
				return true;
			}
			else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission("globaltime.reload")) {
					sender.sendMessage(ChatColor.RED + "You don't have the correct permission to reload!");
					return true;
				}
				reload();
				sender.sendMessage(ChatColor.GREEN + "Global Time has been reloaded.");
				return true;
				}
			}
		}
		return false;
	}
	
	protected void startBroadcasting() {
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
		    @Override  
		    public void run() {
		    	if (firstRun) {
		    		firstRun = false;
		    		return;
		    	}
		        getServer().broadcastMessage(format((String) getConfig().get("BroadcastMessage"), format.format(new Date()), calendar, formatDate(calendar)));
		    }
		}, 0L, (getConfig().getInt("IntervalTime") * 1200L));
	}
	
	protected String formatTime(Calendar calendar) {
		String time;
		time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		return time;
	}
	
	protected String formatDate(Calendar calendar) {
		String date;
		String month;
		String day;
		switch (calendar.get(Calendar.MONTH)) {
	    case 0:  month = "January";
        	break;
	    case 1:  month = "February";
        	break;
	    case 2:  month = "March";
			break;
	    case 3:  month = "April";
        	break;
	    case 4:  month = "May";
        	break;
	    case 5:  month = "June";
        	break;
	    case 6:  month = "July";
        	break;
	    case 7:  month = "August";
        	break;
	    case 8:  month = "September";
        	break;
	    case 9: month = "October";
        	break;
	    case 10: month = "November";
        	break;
	    case 11: month = "December";
        	break;
	    default: month = "Invalid month";
        	break;
		}
		
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
	    case 1:  day = "Sunday";
        	break;
	    case 2:  day = "Monday";
        	break;
	    case 3:  day = "Tuesday";
			break;
	    case 4:  day = "Wednesday";
        	break;
	    case 5:  day = "Thursday";
        	break;
	    case 6:  day = "Friday";
        	break;
	    case 7:  day = "Saturday";
        	break;
	    default: day = "Invalid month";
        	break;
		}
		date = day + " " + calendar.get(Calendar.DAY_OF_WEEK) + " " + month + " " + calendar.get(Calendar.YEAR);
		return date;
	}
	
	protected String replaceColours(String message) {
		return message.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
	}
	
	protected String format(String string, String time, Calendar calender, String date) {
		string = replaceColours(string);
	    string = string.replaceAll("%time%", time.toString()).replaceAll("%timezone%", calender.getTimeZone().getDisplayName()).replaceAll("%date%", date);
	    return string;
	}
}
