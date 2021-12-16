package com.redhat.demo.eapobservabilty;

import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.opentracing.Traced;

@Path("/")
public class EAPObservableService {

	private final Random random = new Random();

	@GET
	@Path("/english")

	@Counted(name = "greetInEnglishInvocations", description = "Counting the invocations of the English greeting API", displayName = "englishGreetingInvoke", tags = {
			"usecase=simple" })
	public String greetInEnglish() {
		generateRandomFaultAndDelay();
		return "Hello";
	}

	@Traced()
	@GET
	@Path("/french")
	@Timed(name = "greetInFrenchDuration")
	@Counted(name = "greetInFrenchInvocations", description = "Counting the invocations of the French greeting API", displayName = "frenchGreetingInvoke", tags = {
			"usecase=simple" })
	public String greetInFrench() {
		generateRandomFaultAndDelay();
		return "Bonjour";
	}

	@GET
	@Path("/hindi")
	@Counted(name = "greetInHindiInvocations", description = "Counting the invocations of the Hindi greeting API", displayName = "hindiGreetingInvoke", tags = {
			"usecase=simple" })
	public String greetInHindi() {
		generateRandomFaultAndDelay();
		return "Namaste";
	}

	private void generateRandomFaultAndDelay() {
		try {
			Thread.sleep(random.nextInt(200));
		} catch (InterruptedException e) {
		}

		if (random.nextInt(10) <= 3) {
			throw new IllegalStateException("Big problem in my application");
		}
	}
}
