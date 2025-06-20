package dev.fireatom.FABI.utils.encoding;

public class EncodingUtil {
	/**
	 * @param guildId Guild ID
	 * @param channelId Channel ID
	 * @return Filename 'transcript-[encoded guildId-channelId].html'
	 */
	public static String encodeTranscript(final long guildId, final long channelId) {
		return "transcript-%s.html".formatted(encode(guildId, channelId));
	}

	/**
	 * @param guildId Guild ID
	 * @param userId User ID
	 * @param epochSeconds Epoch seconds (now)
	 * @return Filename 'modstats-[encoded guildId-userId-seconds].png'
	 */
	public static String encodeModstats(final long guildId, final long userId, final long epochSeconds) {
		return "modstats-%s.png".formatted(encode(guildId, userId, epochSeconds));
	}

	/**
	 * @param guildId Guild ID
	 * @param epochSeconds Epoch seconds (now)
	 * @return Filename 'modreport-[encoded guildId-seconds].png'
	 */
	public static String encodeModreport(final long guildId, final long epochSeconds) {
		return "modreport-%s.png".formatted(encode(guildId, epochSeconds));
	}

	/**
	 * @param id Message or Channel ID
	 * @param epochSeconds Epoch seconds (now)
	 * @return Filename 'msg-[encoded id-seconds].txt'
	 */
	public static String encodeMessage(final long id, final long epochSeconds) {
		return "msg-%s.txt".formatted(encode(id, epochSeconds));
	}

	/**
	 * @param guildId Guild ID
	 * @param userId User ID
	 * @return Filename 'user_bg-[encoded guildId:userId].png'
	 */
	public static String encodeUserBg(final long guildId, final long userId) {
		return "user_bg-%s.png".formatted(encode(guildId, userId));
	}

	/**
	 * Encodes a sequence of values in Base62 encoding.
	 *
	 * @param values a Long sequence.
	 * @return a string with Base62-encoded values.
	 */
	private static String encode(final long... values) {
		if (values.length == 0) throw new IllegalArgumentException("No values provided.");
		String[] results = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = Base62.encode(values[i]);
		}
		return String.join("-", results);
	}

}