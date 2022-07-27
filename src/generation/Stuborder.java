package generation;

public class Stuborder implements Order {
	
	private int seed;
	
	private int skill;
	
	private boolean perfect;
	
	private Builder build;
	
	public Maze maze;
	
	public Stuborder(int Seed, int Skill, boolean Perfect, Builder Build) {
		seed = Seed;
		skill = Skill;
		perfect = Perfect;
		build = Build;
	}
	
	@Override
	public int getSkillLevel() {
		// TODO Auto-generated method stub
		return skill;
	}

	@Override
	public Builder getBuilder() {
		// TODO Auto-generated method stub
		return build;
	}

	@Override
	public boolean isPerfect() {
		// TODO Auto-generated method stub
		return perfect;
	}

	@Override
	public int getSeed() {
		// TODO Auto-generated method stub
		return seed;
	}

	@Override
	public void deliver(Maze mazeConfig) {
		// TODO Auto-generated method stub
		maze = mazeConfig;
	}

	@Override
	public void updateProgress(int percentage) {
		// TODO Auto-generated method stub
		
	}

}
