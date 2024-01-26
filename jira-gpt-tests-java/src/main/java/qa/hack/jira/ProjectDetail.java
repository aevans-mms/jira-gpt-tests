package qa.hack.jira;

import java.util.List;

public class ProjectDetail {
	public String expand;
	public String self;
	public String id;
	public String key;
	public String description;
	public User lead;
	public List<Component> components;
	public List<IssueType> issueTypes;
	public String assigneeType;
	public List<Version> versions;
	public Object roles;
	public Object avatarUrls;
	public String projectTypeKey;
	public boolean archived;
}
