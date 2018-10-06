package io.github.martinschneider.yasew.junit;

import cucumber.api.CucumberOptionsProvider;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/** JUnit runner for tests. */
@RunWith(Cucumber.class)
@CucumberOptionsProvider(OptionsProvider.class)
public class YasewTest {}
