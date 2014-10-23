/*
 * The MIT License
 * 
 * Copyright (c) 2011, Jesse Farinacci
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.managegroup;

import hudson.Extension;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;

import javax.inject.Inject;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class ProjectGroupAssociationDescriptor extends JobPropertyDescriptor {
	private String groupName;

	@Inject
	private transient GroupsGlobalConfiguration globalGroup;

	public ProjectGroupAssociationDescriptor() {
		super(ProjectGroupAssociation.class);
		load();
	}

	@DataBoundConstructor
	public ProjectGroupAssociationDescriptor(String groupName) {
		setGroupName(groupName);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends Job> jobType) {
		return true;
	}

	@Override
	public String getDisplayName() {
		return "GroupName";
	}

	@Override
	public ProjectGroupAssociation newInstance(StaplerRequest req, JSONObject formData) throws FormException {
		String grp = null;
		if (formData != null && !formData.isNullObject()) {
			grp = formData.getString("groupName");
		}

		if (grp == null || grp.trim().length() == 0) {
			throw new FormException("Error group empty", "groupName");
		}

		ProjectGroupAssociation property = new ProjectGroupAssociation(grp);
		return property;
	}

	public FormValidation doCheckGroupName(
			@QueryParameter(value = "groupName", fixEmpty = true, required = true) String grp) {
		if (grp == null) {
			return FormValidation.warning("Group must be set");

		}
		return FormValidation.ok();
	}

	public ListBoxModel doFillGroupNameItems() {
		ListBoxModel items = new ListBoxModel();

		for (String group : globalGroup.getGroups()) {
			Option o = new Option(group, group, (group.equals(groupName)));
			items.add(o);
		}

		return items;
	}

	@Override
	public boolean configure(StaplerRequest req, JSONObject formData) {
		// req.bindJSON(this, formData);
		save();
		return true;
	}
}
