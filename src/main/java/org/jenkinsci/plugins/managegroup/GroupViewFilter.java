package org.jenkinsci.plugins.managegroup;

import hudson.Extension;
import hudson.model.TopLevelItem;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.views.ViewJobFilter;
import hudson.views.AbstractIncludeExcludeJobFilter;

import org.kohsuke.stapler.DataBoundConstructor;

public class GroupViewFilter extends AbstractIncludeExcludeJobFilter {

	private String groupName = "";

	@DataBoundConstructor
	public GroupViewFilter(String groupName, String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.groupName = groupName;
	}


	@Override
	protected boolean matches(TopLevelItem item) {
		if (!Job.class.isAssignableFrom(item.getClass())) {
			return false;
		}

		Job<?, ?> job = (Job<?, ?>) item;
		ProjectGroupAssociation desc = (ProjectGroupAssociation) job.getProperty(ProjectGroupAssociation.class);
		if (desc == null)
			return false;

		return groupName.equals(desc.getGroupName());
	}

	public String getGroupName() {
		return groupName;
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {

		@Override
		public String getDisplayName() {
			return "Group Filter";
		}

	}

}
