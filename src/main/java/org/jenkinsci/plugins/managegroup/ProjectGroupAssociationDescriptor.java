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
import hudson.ExtensionList;
import hudson.model.JobPropertyDescriptor;
import hudson.model.TopLevelItem;
import hudson.model.Job;
import hudson.model.ListView;
import hudson.model.View;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.Ancestor;
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

		// Remove the project direct association if view is a group view
		fixProjectAssociation(req);

		return property;
	}

	private void fixProjectAssociation(StaplerRequest req) {
		fixDefaultView(req);
		Ancestor viewAncestor = req.findAncestor(ListView.class);
		if (viewAncestor == null) {
			return;
		}
		ListView view = (ListView) viewAncestor.getObject();
		Ancestor projectAncestor = req.findAncestor(TopLevelItem.class);
		if (projectAncestor == null) {
			return;
		}
		try {
			view.remove((TopLevelItem) projectAncestor.getObject());
		} catch (IOException e) {
			// NO OP
		}
	}

	private void fixDefaultView(StaplerRequest req) {
		Ancestor projectAncestor = req.findAncestor(TopLevelItem.class);
		if (projectAncestor == null) {
			return;
		}
		for (View v : Jenkins.getInstance().getViews()) {
			if (v.isDefault() && v instanceof ListView) {
				try {
					((ListView) v).remove((TopLevelItem) projectAncestor.getObject());
				} catch (IOException e) {
					// NO OP
				}
			}
		}
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

		Set<String> groups = new TreeSet<String>();
		for (GroupProvider p : ExtensionList.lookup(GroupProvider.class)) {
			groups.addAll(p.groupFor(Jenkins.getAuthentication().getName()));
		}
		groups.addAll(globalGroup.getGroups());

		for (String group : groups) {
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
