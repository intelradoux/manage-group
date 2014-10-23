package org.jenkinsci.plugins.managegroup;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.Job;

import java.util.Set;

import javax.inject.Inject;

import org.kohsuke.stapler.DataBoundConstructor;

@Extension
public class ProjectGroupAssociation extends JobProperty<Job<?, ?>> {
	private String groupName = null;

	@Inject
	private GroupsGlobalConfiguration globalConfiguration;

	public ProjectGroupAssociation() {
		this("");
	}

	@DataBoundConstructor
	public ProjectGroupAssociation(String grp) {
		this.groupName = grp;
	}


	public String getGroupName() {
		return groupName;
	}

	public Set<String> getAllGroups() {
		return globalConfiguration.getGroups();
	}

	@Override
	public ProjectGroupAssociationDescriptor getDescriptor() {
		return (ProjectGroupAssociationDescriptor) super.getDescriptor();
	}
}
