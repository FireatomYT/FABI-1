package dev.fireatom.FABI.commands.other;

import java.util.List;

import dev.fireatom.FABI.base.command.SlashCommandEvent;
import dev.fireatom.FABI.commands.CommandBase;
import dev.fireatom.FABI.objects.constants.CmdCategory;
import dev.fireatom.FABI.objects.constants.Constants;
import dev.fireatom.FABI.objects.constants.Links;

import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AboutCmd extends CommandBase {

	public AboutCmd() {
		this.name = "about";
		this.path = "bot.other.about";
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
		MessageEmbed embed = bot.getEmbedUtil().getEmbed()
			.setAuthor(event.getJDA().getSelfUser().getName(), event.getJDA().getSelfUser().getEffectiveAvatarUrl())
			.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
			.addField(
				lu.getLocalized(userLocale, "bot.other.about.embed.about_title")
					.replace("{name}", "FABI bot"),
				lu.getLocalized(userLocale, "bot.other.about.embed.about_value")
					.replace("{developer_name}", Constants.DEVELOPER_TAG)
					.replace("{developer_id}", String.valueOf(Constants.DEVELOPER_ID)),
				false
			)
			.addField(
				lu.getLocalized(userLocale, "bot.other.about.embed.commands_title"),
				lu.getLocalized(userLocale, "bot.other.about.embed.commands_value"),
				false
			)
			.addField(
				lu.getLocalized(userLocale, "bot.other.about.embed.bot_info.title"),
				String.join(
					"\n",
					lu.getLocalized(userLocale, "bot.other.about.embed.bot_info.bot_version").replace("{bot_version}", bot.VERSION),
					lu.getLocalized(userLocale, "bot.other.about.embed.bot_info.library")
						.replace("{jda_version}", JDAInfo.VERSION)
						.replace("{jda_github}", JDAInfo.GITHUB)
						.replace("{chewtils_github}", Links.CHEWTILS_GITHUB)
				),
				false
			)
			.addField(
				lu.getLocalized(userLocale, "bot.other.about.embed.links.title"),
				String.join(
					"\n",
					lu.getLocalized(userLocale, "bot.other.about.embed.links.discord").replace("{guild_invite}", Links.DISCORD),
					lu.getLocalized(userLocale, "bot.other.about.embed.links.github").replace("{github_url}", Links.GITHUB),
					lu.getLocalized(userLocale, "bot.other.about.embed.links.terms").replace("{terms_url}", Links.TERMS),
					lu.getLocalized(userLocale, "bot.other.about.embed.links.privacy").replace("{privacy_url}", Links.PRIVACY)
				),
				true
			)
			.addField(
				lu.getLocalized(userLocale, "bot.other.about.embed.links.translate"),
				"[Crowdin.com](%s)".formatted(Links.CROWDIN),
				false
			)
			.build();
		
		editEmbed(event, embed);
	}

}
