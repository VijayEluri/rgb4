package de.fhtrier.gdig.engine.network.impl;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.newdawn.slick.util.Log;

import de.fhtrier.gdig.demos.jumpnrun.identifiers.Constants;
import de.fhtrier.gdig.engine.network.INetworkCommand;
import de.fhtrier.gdig.engine.network.impl.protocol.ClientQueryConnect;
import de.fhtrier.gdig.engine.network.impl.protocol.ClientQueryDisconnect;
import de.fhtrier.gdig.engine.network.impl.protocol.ServerAckConnect;
import de.fhtrier.gdig.engine.network.impl.protocol.ServerAckDisconnect;

enum LocalState {
	DISCONNECTED, WAITINGFORNETWORKID, READYTOSEND, WAITINGFORDISCONNECT
}

public class NetworkComponentClient extends NetworkComponentImpl {

	private ServerHandler serverHandler;
	private LocalState localState;
	private int networkId;
	private Queue<INetworkCommand> queue;

	public NetworkComponentClient() {
		super();
		this.networkId = -1;
		setState(LocalState.DISCONNECTED);
		this.queue = new LinkedList<INetworkCommand>();
	}

	@Override
	public boolean connect(String host, int port) {
		if (localState == LocalState.DISCONNECTED) {
			try {
				this.socket = new Socket(host, port);
				this.serverHandler = new ServerHandler(this.socket, this);
				this.serverHandler.start();
				askForNetworkId();
			} catch (UnknownHostException e) {
				Log.error("Unknown host");
				// e.printStackTrace();
				return false;
			} catch (IOException e) {
				// Log.error("Fail connecting");
				// e.printStackTrace();
				return false;
			}
		} else {
			throw new RuntimeException("already connected");
		}
		return true;
	}

	@Override
	boolean handleProtocolCommand(INetworkCommand command) {

		if (localState == LocalState.WAITINGFORNETWORKID) {
			if (command instanceof ServerAckConnect) {
				this.networkId = ((ServerAckConnect) command).getNetworkId();
				setState(LocalState.READYTOSEND);

				// if commands have queued up, send them
				for (INetworkCommand cmd : queue) {
					sendCommand(cmd);
				}
				queue.clear();
				return true;
			}
		}

		if (localState == LocalState.WAITINGFORDISCONNECT) {
			// if server tells us to disconnect, do it
			if (command instanceof ServerAckDisconnect) {
				this.serverHandler.close();
				this.networkId = -1;
				setState(LocalState.DISCONNECTED);
				return true;
			}
		}
		return false;
	}

	void setState(LocalState state) {
		if (state == null) {
			throw new IllegalArgumentException("new state must not be null");
		}

		if (Constants.Debug.networkDebug) {
			Log.debug("NetworkComponent: Changed state from "
					+ ((localState == null) ? "null" : localState.name())
					+ " to " + state.name());
		}
		localState = state;
	}

	@Override
	public void disconnect() {
		if (localState == LocalState.READYTOSEND) {
			sendCommand(new ClientQueryDisconnect());
			setState(LocalState.WAITINGFORDISCONNECT);
		}
	}

	void askForNetworkId() {
		this.serverHandler.sendToServer(new ClientQueryConnect());
		setState(LocalState.WAITINGFORNETWORKID);
	}

	@Override
	public void sendCommand(INetworkCommand command) {
		if (localState == LocalState.READYTOSEND) {
			this.serverHandler.sendToServer(command);
		} else {
			queue.add(command);
		}
	}

	@Override
	public Integer getNetworkId() {
		return this.networkId;
	}

	@Override
	public void sendCommand(int clientNetworkId, INetworkCommand command) {
		throw new RuntimeException(
				"send command to specific client is not allowed for clients");
	}

	@Override
	public void startListening(InterfaceAddress ni, int port) {
		throw new RuntimeException("startListening is not possible on a client");
	}

	@Override
	public void stopListening() {
		throw new RuntimeException("stopListening is not possible on a client");
	}

	@Override
	public List<ClientHandler> getClients() {
		throw new RuntimeException("getClients is not possible on a client");
	}

	@Override
	public void addClient(Socket s) {
		throw new RuntimeException("addClient is not possible on a client");
	}

	@Override
	public void removeClient(ClientHandler c) {
		throw new RuntimeException("removeClient is not possible on a client");
	}
}