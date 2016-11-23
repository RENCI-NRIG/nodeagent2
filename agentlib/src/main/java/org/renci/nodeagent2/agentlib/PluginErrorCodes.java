package orca.nodeagent2.agentlib;

public enum PluginErrorCodes {

	OK(0),
	EXCEPTION(255);

	public int code;

	PluginErrorCodes(int c) {
		code = c;
	}
}
