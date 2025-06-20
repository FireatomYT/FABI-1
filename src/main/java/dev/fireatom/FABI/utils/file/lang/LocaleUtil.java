package dev.fireatom.FABI.utils.file.lang;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dev.fireatom.FABI.App;
import dev.fireatom.FABI.objects.Emote;

import dev.fireatom.FABI.utils.message.MessageUtil;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class LocaleUtil {

	private final App bot;
	private final LangUtil langUtil;

	private final DiscordLocale defaultLocale;

	public LocaleUtil(App bot, DiscordLocale defaultLocale) {
		this.bot = bot;
		this.langUtil = new LangUtil(bot.getFileManager());
		this.defaultLocale = defaultLocale;
	}

	@NotNull
	public String getLocalized(DiscordLocale locale, String path) {
		return Emote.getWithEmotes(langUtil.getString(locale, path));
	}

	@NotNull
	public String getLocalized(DiscordLocale locale, String path, String user) {
		return getLocalized(locale, path, user, true);
	}

	@NotNull
	public String getLocalized(DiscordLocale locale, String path, String user, boolean format) {
		if (format)
			user = MessageUtil.getFormattedMembers(this, user);

		return Objects.requireNonNull(getLocalized(locale, path).replace("{user}", user));
	}

	@NotNull
	public String getLocalized(DiscordLocale locale, String path, String user, String target) {
		target = (target == null ? "null" : target);
		
		return getLocalized(locale, path, user, Collections.singletonList(target), false);
	}

	@NotNull
	public String getLocalized(DiscordLocale locale, String path, String user, List<String> targets) {
		return getLocalized(locale, path, user, targets, false);
	}

	@NotNull
	public String getLocalized(DiscordLocale locale, String path, String user, List<String> targets, boolean format) {
		String targetReplacement = targets.isEmpty() ? "null" : MessageUtil.getFormattedMembers(this, targets.toArray(new String[0]));

		return Objects.requireNonNull(getLocalized(locale, path, user, format)
			.replace("{target}", targetReplacement)
			.replace("{targets}", targetReplacement)
		);
	}

	@Nullable
	public String getLocalizedNullable(DiscordLocale locale, String path) {
		return langUtil.getNullableString(locale, path);
	}

	@NotNull
	public String getLocalizedRandom(DiscordLocale locale, String path) {
		return Emote.getWithEmotes(langUtil.getRandomString(locale, path));
	}

	@NotNull
	public Map<DiscordLocale, String> getFullLocaleMap(String path, String defaultText) {
		Map<DiscordLocale, String> localeMap = new HashMap<>();
		for (DiscordLocale locale : bot.getFileManager().getLanguages()) {
			// Ignores UK/US change
			if (locale.equals(DiscordLocale.ENGLISH_UK) || locale.equals(DiscordLocale.ENGLISH_US)) continue;
			localeMap.put(locale, getLocalized(locale, path));
		}
		localeMap.put(DiscordLocale.ENGLISH_UK, defaultText);
		localeMap.put(DiscordLocale.ENGLISH_US, defaultText);
		return localeMap;
	}

	@NotNull
	public Map<DiscordLocale, String> getLocaleMap(String path) {
		Map<DiscordLocale, String> localeMap = new HashMap<>();
		for (DiscordLocale locale : bot.getFileManager().getLanguages()) {
			// Ignores UK/US change
			if (locale.equals(DiscordLocale.ENGLISH_UK) || locale.equals(DiscordLocale.ENGLISH_US)) continue;
			localeMap.put(locale, getLocalized(locale, path));
		}
		return localeMap;
	}

	@NotNull
	public String getText(@NotNull String path) {
		return getLocalized(defaultLocale, path);
	}

	@NotNull
	public String getText(IReplyCallback replyCallback, @NotNull String path) {
		return getLocalized(replyCallback.getUserLocale(), path);
	}

	@NotNull
	public String getUserText(IReplyCallback replyCallback, @NotNull String path) {
		return getUserText(replyCallback, path, Collections.emptyList(), false);
	}

	@NotNull
	public String getUserText(IReplyCallback replyCallback, @NotNull String path, boolean format) {
		return getUserText(replyCallback, path, Collections.emptyList(), format);
	}

	@NotNull
	public String getUserText(IReplyCallback replyCallback, @NotNull String path, String target) {
		return getUserText(replyCallback, path, Collections.singletonList(target), false);
	}
	
	@NotNull
	public String getUserText(IReplyCallback replyCallback, @NotNull String path, List<String> targets) {
		return getUserText(replyCallback, path, targets, false);
	}

	@NotNull
	private String getUserText(IReplyCallback replyCallback, @NotNull String path, List<String> targets, boolean format) {
		return getLocalized(replyCallback.getUserLocale(), path, replyCallback.getUser().getEffectiveName(), targets, format);
	}

	@NotNull
	public String getGuildText(IReplyCallback replyCallback, @NotNull String path) {
		return getLocalized(replyCallback.getGuildLocale(), path);
	}

}
