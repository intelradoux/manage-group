/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Jorg Heymans, Peter Hayes, Red Hat, Inc., Stephen Connolly, id:cactusman
 * Olivier Lamy
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class GroupsGlobalConfiguration extends GlobalConfiguration {
	private Set<String> groups;

	public GroupsGlobalConfiguration() {
		this(Collections.<String> emptySet());
	}

	/**
	 * Create a the global groups
	 */
	@DataBoundConstructor
	public GroupsGlobalConfiguration(final Set<String> groups) {
		super();
		load();
		this.groups = Collections.unmodifiableSet(groups);
	}

	@Override
	public String getDisplayName() {
		return "Groups";
	}

	@Override
	public boolean configure(final StaplerRequest request, final JSONObject json) throws FormException {
		// request.bindJSON(this, json);
		Set<String> currentGroups = new HashSet<String>();

		if (json != null && !json.isNullObject()) {
			// JSON groupData = (JSON) json.get("groups");
			String groupsString = json.optString("groups", "");
			for (String grp : groupsString.split("[ \\n\\r]")) {
				currentGroups.add(grp);
			}
		}

		groups = Collections.unmodifiableSet(currentGroups);
		save();
		return true;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public String getGroupsString() {
		StringBuilder sb = new StringBuilder();
		for (String s : groups) {
			sb.append(s).append(" ");
		}
		return sb.toString();
	}
}
