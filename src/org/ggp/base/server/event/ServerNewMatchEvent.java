package org.ggp.base.server.event;

import java.io.Serializable;
import java.util.List;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;


@SuppressWarnings("serial")
public final class ServerNewMatchEvent extends Event implements Serializable
{

	private final List<Role> roles;
	private final MachineState initialState;

	public ServerNewMatchEvent(List<Role> roles, MachineState initialState)
	{
		this.roles = roles;
		this.initialState = initialState;
	}

	public List<Role> getRoles()
	{
		return roles;
	}
	
	public MachineState getInitialState()
	{
		return initialState;
	}

}
