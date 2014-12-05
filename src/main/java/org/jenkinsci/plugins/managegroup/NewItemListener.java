package org.jenkinsci.plugins.managegroup;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.listeners.ItemListener;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import jenkins.model.Jenkins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class NewItemListener extends ItemListener {
	private static final Logger LOG = LoggerFactory.getLogger(NewItemListener.class);

	@Inject
	private transient GroupsGlobalConfiguration globalGroup;

	@Override
	public void onCreated(Item item) {
		if (!Item.class.isAssignableFrom(item.getClass())) {
			return;
		}

		String group = getName();
		if (group == null) {
			return;
		}

		Job<?, ?> job = (Job<?, ?>) item;

		try {
			job.removeProperty(ProjectGroupAssociation.class);

			ProjectGroupAssociation pga = new ProjectGroupAssociation(group);

			job.addProperty(pga);
		} catch (IOException e) {
			LOG.warn("Unable to set project group...");
		}

		super.onCreated(item);
	}

	public String getName() {
		for (GroupProvider p : ExtensionList.lookup(GroupProvider.class)) {
			Set<String> s = p.groupFor(Jenkins.getAuthentication().getName());
			if (s != null && !s.isEmpty()) {
				return s.iterator().next();
			}
		}

		Set<String> s = globalGroup.getGroups();
		if (s != null && !s.isEmpty()) {
			return s.iterator().next();
		}

		return null;
	}

}
