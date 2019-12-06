package me.mrCookieSlime.Slimefun.hooks.github;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.mrCookieSlime.Slimefun.SlimefunPlugin;
import org.bukkit.Bukkit;

public class ContributionsConnector extends GitHubConnector {
	
	// All names including "bot" are automatically blacklisted. But, others can be too right here.
	// (includes "invalid-email-address" because it is an invalid contributor)
	private static final List<String> blacklist = Collections.singletonList(
			"invalid-email-address"
	);

	// Matches a GitHub name with a Minecraft name.
	private static final Map<String, String> aliases = new HashMap<>();

	static {
		aliases.put("WalshyDev", "HumanRightsAct");
		aliases.put("J3fftw1", "_lagpc_");
	}
	
	private final String prefix;
	private final String repository;
	private final String role;
	
	public ContributionsConnector(String prefix, String repository, String role) {
		this.prefix = prefix;
		this.repository = repository;
		this.role = role;
	}
	
	@Override
	public void onSuccess(JsonElement element) {
		computeContributors(element.getAsJsonArray());
	}
	
	@Override
	public String getRepository() {
		return repository;
	}
	
	@Override
	public String getFileName() {
		return prefix + "_contributors";
	}

	@Override
	public String getURLSuffix() {
		return "/contributors?per_page=100";
	}

	private void computeContributors(JsonArray array) {
	    for (int i = 0; i < array.size(); i++) {
	    	JsonObject object = array.get(i).getAsJsonObject();

	    	String name = object.get("login").getAsString();
	    	int commits = object.get("contributions").getAsInt();
	    	String profile = object.get("html_url").getAsString();

	    	if (!name.toLowerCase().contains("bot") && !blacklist.contains(name)) {
	    		Contributor contributor = SlimefunPlugin.getUtilities().contributors.computeIfAbsent(
	    				name,
						key -> new Contributor(aliases.getOrDefault(name, name), profile)
				);
	    		contributor.setContribution(role, commits);
	    	}
	    }
	}
}