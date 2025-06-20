package dev.fireatom.FABI.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import dev.fireatom.FABI.base.command.SlashCommand;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class CommandBase extends SlashCommand {

	// Edit Message(String or MED) and Embed
	public final void editMsg(IReplyCallback event, @NotNull String msg) {
		event.getHook().editOriginal(msg).queue();
	}

	public final void editMsg(IReplyCallback event, @NotNull MessageEditData data) {
		event.getHook().editOriginal(data).queue();
	}

	public final void editEmbed(IReplyCallback event, @NotNull MessageEmbed... embeds) {
		event.getHook().editOriginalEmbeds(embeds).queue();
	}

	// Edit Error
	public final void editError(IReplyCallback event, @NotNull MessageEditData data) {
		event.getHook().editOriginal(data)
			.setComponents()
			.queue(msg -> {
				if (!msg.isEphemeral())
					msg.delete().queueAfter(20, TimeUnit.SECONDS, null, ignoreRest);
			});
	}

	public final void editError(IReplyCallback event, @NotNull MessageEmbed embed) {
		editError(event, new MessageEditBuilder()
			.setContent(lu.getText(event, "misc.temp_msg"))
			.setEmbeds(embed)
			.build()
		);
	}

	public final void editError(IReplyCallback event, @NotNull String path) {
		editError(event, path, null);
	}

	public final void editError(IReplyCallback event, @NotNull String path, String reason) {
		editError(event, bot.getEmbedUtil().getError(event, path, reason));
	}

	public final void editErrorOther(IReplyCallback event, String details) {
		editError(event, bot.getEmbedUtil().getError(event, "errors.error", details));
	}

	public final void editErrorUnknown(IReplyCallback event, String details) {
		editError(event, bot.getEmbedUtil().getError(event, "errors.unknown", details));
	}

	public final void editErrorDatabase(IReplyCallback event, Exception exception, String details) {
		if (exception instanceof SQLException ex) {
			editError(event, bot.getEmbedUtil().getError(event, "errors.database", "%s: %s".formatted(ex.getErrorCode(), details)));
		} else {
			editError(event, bot.getEmbedUtil().getError(event, "errors.database", "%s\n> %s".formatted(details, exception.getMessage())));
		}
	}

	// PermError
	public final void editPermError(IReplyCallback event, Permission perm, boolean self) {
		editError(event, MessageEditData.fromCreateData(bot.getEmbedUtil().createPermError(event, perm, self)));
	}


	protected static final Consumer<Throwable> ignoreRest = ignored -> {
		// Nothing to see here
		// Ignore everything
	};

}
