package com.taivas.rcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RconClient {

    public static final int DEFAULT_TIMEOUT_MILLISECONDS = 200;
    private static final Logger LOG = LoggerFactory.getLogger(RconClient.class);
    private static final String COMMAND_PREFIX = new String(new char[]{255, 255, 255, 255});
    private static final Charset CHARSET = Charset.forName("cp1252");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^ ([0-9]{1,2}) {5}");
    private static final int RECEIVE_BUFFER_SIZE = 1024;

    private InetSocketAddress address;
    private String password;
    private DatagramSocket socket;

    public void connect(InetSocketAddress address, String password) throws SocketException {
        connect(
                address,
                new InetSocketAddress(0),
                password,
                DEFAULT_TIMEOUT_MILLISECONDS
        );
    }

    public void connect(InetSocketAddress address, InetSocketAddress bindAddress, String password, int timeoutMilliseconds) throws SocketException {
        this.address = address;
        this.password = password;

        LOG.info("Binding to {}:{}", bindAddress.getHostName(), bindAddress.getPort());
        socket = new DatagramSocket(bindAddress);
        LOG.info("Connecting to {}:{}", address.getHostName(), address.getPort());
        socket.connect(address);
        socket.setSoTimeout(timeoutMilliseconds);
        LOG.info("Ready to send commands");
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public RconResult send(String command) {
        String payload = String.format("%srcon %s %s", COMMAND_PREFIX, password, command);
        byte[] bytes = payload.getBytes(CHARSET);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
        RconResult.Status status = RconResult.Status.FAILED;
        StringWriter response = new StringWriter();

        try {
            socket.send(packet);
            while (true) {
                bytes = new byte[RECEIVE_BUFFER_SIZE];
                packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);
                String unfiltered = new String(bytes, CHARSET);
                response.append(unfiltered.replaceAll("\\x00", "").replace(COMMAND_PREFIX + "print\n", ""));
                status = RconResult.Status.OK;
            }
        } catch (SocketTimeoutException ignored) {
        } catch (IOException e) {
            LOG.error("Couldn't send rcon command", e);
        }

        return new RconResult(status, response.toString());
    }

    public RconResult addIp(String ip) {
        return send(String.format("addip \"%s\"", ip));
    }

    public RconResult removeIp(String ip) {
        return send(String.format("removeip \"%s\"", ip));
    }

    public RconResult kick(int slot) {
        return send(String.format("kick %d", slot));
    }

    public RconResult ban(int slot) {
        return send(String.format("ban %d", slot));
    }

    public RconResult status(boolean truncateNames) {
        if (truncateNames) {
            return send("status");
        }

        return send("status notrunc");
    }

    public RconResult status() {
        return status(false);
    }

    public List<Integer> playerSlots() {
        List<Integer> slots = new ArrayList<>();
        RconResult result = status(true);
        if (result.getStatus() == RconResult.Status.FAILED) {
            return slots;
        }

        String[] status = result.getMessage().split("\n");
        if (status.length < 2) {
            return slots;
        }

        for (String line : status) {
            Matcher matcher = STATUS_PATTERN.matcher(line);
            if (matcher.find()) {
                slots.add(Integer.parseInt(matcher.group(1)));
            }
        }

        return slots;
    }

    public RconResult say(String message) {
        return send(String.format("svsay \"%s\"", message));
    }

    public RconResult tell(int slot, String message) {
        return send(String.format("svtell %d \"%s\"", slot, message));
    }

    public RconResult newRound() {
        return send("newround");
    }

    public RconResult map(String map) {
        return send(String.format("map \"%s\"", map));
    }

    public RconResult mode(int mode) {
        return send(String.format("mbmode %d", mode));
    }

    public RconResult mode(int mode, String map) {
        return send(String.format("mbmode %d \"%s\"", mode, map));
    }

    public RconResult snd(String soundPath) {
        return send(String.format("snd \"%s\"", soundPath));
    }

    public RconResult sndTeam(String team, String soundPath) {
        return send(String.format("sndTeam \"%s\" \"%s\"", team, soundPath));
    }

    public RconResult sndClient(int slot, String soundPath) {
        return send(String.format("sndClient %d \"%s\"", slot, soundPath));
    }

    public RconResult printAll(String message) {
        return print("all", message, false);
    }

    public RconResult print(int slot, String message) {
        return print("" + slot, message, false);
    }

    public RconResult printConAll(String message) {
        return print("all", message, true);
    }

    public RconResult printCon(int slot, String message) {
        return print("" + slot, message, true);
    }

    private RconResult print(String target, String message, boolean consoleOnly) {
        if (consoleOnly) {
            return send(String.format("svprintcon %s \"%s\"", target, message));
        }

        return send(String.format("svprint %s \"%s\"", target, message));
    }
}
