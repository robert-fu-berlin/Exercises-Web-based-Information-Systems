package guruPackage;

import java.util.Random;

public class Guru {
	private static Random myRandom;
	private static final String[] Sayings = {
		"When a man brings his wife flowers for no reason, there`s a reason.",
		"Women like silent men. Theay think they`re listening.",
		"When you are courting a nice girl an hour seems like a second. When you sit on a red-hot cinder a second seems like an hour. That's relativity.",
		"It is a little embarrassing that, after years of research and study, the best advice I can give to people is to be a little kinder to each other.",
		"Love your neighbour as yourself, but choose your neighbourhood. ",
		"Nothing endures but change.",
		"Cats are smarter than dogs. You can not get eight cats to pull a sled through snow.",
		"The beginning is the most important part of the work.",
		"Nothing is easier than being busy - and nothing more difficult than being effective.",
		"So many women, so little time.",
		"A diplomat is a man who always remembers a woman's birthday but never remembers her age.",
		"It is better to deserve honours and not have them than to have them and not to deserve them.",
		"Any fool can criticise - and many of them do."};
	
	public String getEnlightenment() {
		int select=myRandom.nextInt(Sayings.length);
		return Sayings[select];
	}
	{
		myRandom = new Random();
	}
}
