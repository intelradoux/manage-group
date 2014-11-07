package org.jenkinsci.plugins.managegroup;

import hudson.ExtensionPoint;

import java.util.Set;

public abstract class GroupProvider implements ExtensionPoint {
	public abstract Set<String> groupList();

	public abstract Set<String> groupFor(String uid);
}
