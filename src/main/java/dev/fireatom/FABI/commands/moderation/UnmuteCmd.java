package dev.fireatom.FABI.commands.moderation;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import dev.fireatom.FABI.base.command.CooldownScope;
import dev.fireatom.FABI.base.command.SlashCommandEvent;
import dev.fireatom.FABI.commands.CommandBase;
import dev.fireatom.FABI.objects.CaseType;
import dev.fireatom.FABI.objects.CmdAccessLevel;
import dev.fireatom.FABI.objects.CmdModule;
import dev.fireatom.FABI.objects.constants.CmdCategory;
import dev.fireatom.FABI.utils.database.managers.CaseManager.CaseData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class UnmuteCmd extends CommandBase {
	
	public UnmuteCmd() {
		this.name = "unmute";
		this.path = "bot.moderation.unmute";
		this.options = List.of(
			new OptionData(OptionType.USER, "user", lu.getText(path+".user.help"), true),
			new OptionData(OptionType.STRING, "reason", lu.getText(path+".reason.help")).setMaxLength(400)
		);
		this.botPermissions = new Permission[]{Permission.MODERATE_MEMBERS};
		this.category = CmdCategory.MODERATION;
		this.module = CmdModule.MODERATION;
		this.accessLevel = CmdAccessLevel.MOD;
		this.cooldown = 5;
		this.cooldownScope = CooldownScope.GUILD;
	}

	@Override
	protected void execute(SlashCommandEvent event) {
		event.deferReply().queue();
		
		Member tm = event.optMember("user");
		if (tm == null) {
			editError(event, path+".not_found");
			return;
		}

		Guild guild = Objects.requireNonNull(event.getGuild());
		String reason = event.optString("reason", lu.getLocalized(event.getGuildLocale(), path+".no_reason"));
		
		CaseData muteData = bot.getDBUtil().cases.getMemberActive(tm.getIdLong(), guild.getIdLong(), CaseType.MUTE);
		if (muteData != null) {
			try {
				bot.getDBUtil().cases.setInactive(muteData.getRowId());
			} catch (SQLException ex) {
				editErrorDatabase(event, ex, "set mute case inactive");
				return;
			}
		}

		if (tm.isTimedOut()) {
			tm.removeTimeout().reason(reason).queue(done -> {
				Member mod = event.getMember();
				// add info to db
				CaseData unmuteData;
				try {
					unmuteData = bot.getDBUtil().cases.add(
						CaseType.UNMUTE, tm.getIdLong(), tm.getUser().getName(),
						mod.getIdLong(), mod.getUser().getName(),
						guild.getIdLong(), reason, Instant.now(), null
					);
				} catch (Exception ex) {
					editErrorDatabase(event, ex, "Failed to create new case.");
					return;
				}
				// log unmute
				bot.getLogger().mod.onNewCase(guild, tm.getUser(), unmuteData, muteData != null ? muteData.getReason() : null).thenAccept(logUrl -> {
					// reply
					editEmbed(event, bot.getModerationUtil().actionEmbed(guild.getLocale(), unmuteData.getLocalIdInt(),
						path+".success", tm.getUser(), mod.getUser(), reason, logUrl)
					);
				});
			},
			failed -> editError(event, path+".abort", failed.getMessage()));
		} else {
			editError(event, path+".not_muted");
		}
	}

}
