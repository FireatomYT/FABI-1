package dev.fireatom.FABI.commands.other;

import java.util.List;

import dev.fireatom.FABI.base.command.SlashCommandEvent;
import dev.fireatom.FABI.commands.CommandBase;
import dev.fireatom.FABI.objects.constants.CmdCategory;
import dev.fireatom.FABI.objects.constants.Constants;

import dev.fireatom.FABI.utils.file.lang.LocaleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class StatusCmd extends CommandBase {

	public StatusCmd() {
		this.name = "status";
		this.path = "bot.other.status";
		this.options = List.of(
			new OptionData(OptionType.BOOLEAN, "show", lu.getText(path+".show.help"))
		);
		this.category = CmdCategory.OTHER;
		this.guildOnly = false;
	}

	@Override
	protected void execute(SlashCommandEvent event) {
		event.deferReply(event.isFromGuild() && !event.optBoolean("show", false)).queue();

		DiscordLocale userLocale = event.getUserLocale();
		MessageEmbed embed = new EmbedBuilder().setColor(Constants.COLOR_DEFAULT)
			.setAuthor(event.getJDA().getSelfUser().getName(), event.getJDA().getSelfUser().getEffectiveAvatarUrl())
			.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
			.addField(
				lu.getLocalized(userLocale, "bot.other.status.embed.stats_title"),
				String.join(
					"\n",
					lu.getLocalized(userLocale, "bot.other.status.embed.stats.guilds")
						.formatted(event.getJDA().getGuilds().size()),
					lu.getLocalized(userLocale, "bot.other.status.embed.stats.shard")
						.formatted(event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getShardInfo().getShardTotal()),
					memoryUsage(lu, userLocale)
				),
				false
			)
			.addField(lu.getLocalized(userLocale, "bot.other.status.embed.shard_title"),
				String.join(
					"\n",
					lu.getLocalized(userLocale, "bot.other.status.embed.shard.users")
						.formatted(event.getJDA().getUsers().size()),
					lu.getLocalized(userLocale, "bot.other.status.embed.shard.guilds")
						.formatted(event.getJDA().getGuilds().size())
				),
				true
			)
			.addField("",
				String.join(
					"\n",
					lu.getLocalized(userLocale, "bot.other.status.embed.shard.text_channels")
						.formatted(event.getJDA().getTextChannels().size()),
					lu.getLocalized(userLocale, "bot.other.status.embed.shard.voice_channels")
						.formatted(event.getJDA().getVoiceChannels().size())
				),
				true
			)
			.setFooter(lu.getLocalized(userLocale, "bot.other.status.embed.last_restart"))
			.setTimestamp(event.getClient().getStartTime())
			.build();

		editEmbed(event, embed);
	}

	private String memoryUsage(LocaleUtil lu, DiscordLocale locale) {
		return lu.getLocalized(locale, "bot.other.status.embed.stats.memory").formatted(
			(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024),
			Runtime.getRuntime().totalMemory() / (1024 * 1024)
		);
	}

}
