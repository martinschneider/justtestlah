package io.github.martinschneider.yasew.examples.carousell.pages.ios;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.martinschneider.yasew.configuration.Platform;
import io.github.martinschneider.yasew.examples.carousell.pages.HomePage;
import io.github.martinschneider.yasew.user.User;

@Component
@Profile(Platform.IOS)
public class LoginPage extends io.github.martinschneider.yasew.examples.carousell.pages.LoginPage {

	private HomePage home;
	
	public HomePage login(User user)
	{
		super.login(user);
		$("CONFIRM_CHAT_ALERTS").click();
		$("ALLOW_NOTIFICATIONS").click();
		return home;
	}
}
