package org.jenkinsci.plugins.managegroup;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.Job;

import org.kohsuke.stapler.DataBoundConstructor;

@Extension
public class ProjectGroupAssociation extends JobProperty<Job<?, ?>> {
	private String groupName = null;

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

	@Override
	public ProjectGroupAssociationDescriptor getDescriptor() {
		return (ProjectGroupAssociationDescriptor) super.getDescriptor();
	}
}
