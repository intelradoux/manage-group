package org.jenkinsci.plugins.managegroup;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.Job;
import hudson.model.View;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class ProjectGroupAssociation extends JobProperty<Job<?, ?>> {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectGroupAssociation.class);
	private String groupName = null;

	public ProjectGroupAssociation() {
		this("");
	}

	@DataBoundConstructor
	public ProjectGroupAssociation(String grp) {
		this.groupName = grp;
		// Check if the group view already exist
		if (Jenkins.getInstance().getView(grp)==null){
			try {
				InputStream is = generateXMLForView(grp);
				View v = View.createViewFromXML(grp, is);
				is.close();
				Jenkins.getInstance().addView(v);
				v.save();
			} catch (Exception e) {
				LOG.error("View creation for group " + grp + " failed", e);
			}
		}
	}


	private InputStream generateXMLForView(String grp) {
		StringBuilder sb = new StringBuilder()
				.append("<listView>")
				.append("<owner class=\"hudson\" reference=\"../../..\"/>")
				.append("<name>").append(grp).append("</name>")
				.append("<filterExecutors>false</filterExecutors>")
				.append("<filterQueue>false</filterQueue>")
				.append("<properties class=\"hudson.model.View$PropertyList\"/>")
				.append("<jobNames>")
				.append("  <comparator class=\"hudson.util.CaseInsensitiveComparator\"/>")
				.append("</jobNames>")
				.append("<jobFilters>")
				.append("  <org.jenkinsci.plugins.managegroup.GroupViewFilter plugin=\"manage-group@1.0\">")
				.append("    <includeExcludeTypeString>includeMatched</includeExcludeTypeString>")
				.append("    <groupName>").append(grp).append("</groupName>")
				.append("  </org.jenkinsci.plugins.managegroup.GroupViewFilter>")
				.append("</jobFilters>")
				.append("<columns>")
				.append("  <hudson.views.StatusColumn/>")
				.append("  <hudson.views.WeatherColumn/>")
				.append("  <hudson.views.JobColumn/>")
				.append("  <hudson.views.LastSuccessColumn/>")
				.append("  <hudson.views.LastFailureColumn/>")
				.append("  <hudson.views.LastDurationColumn/>")
				.append("  <hudson.views.BuildButtonColumn/>")
				.append("</columns>")
				.append("<recurse>false</recurse>")
				.append("</listView>");

		return new ByteArrayInputStream(sb.toString().getBytes());
	}

	public String getGroupName() {
		return groupName;
	}

	@Override
	public ProjectGroupAssociationDescriptor getDescriptor() {
		return (ProjectGroupAssociationDescriptor) super.getDescriptor();
	}
}
