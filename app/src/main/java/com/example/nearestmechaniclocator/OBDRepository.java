package com.example.nearestmechaniclocator;

import android.util.Log;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;

import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class OBDRepository {
    private static final String TAG = "OBDRepository";
    private final InputStream in;
    private final OutputStream out;
    private boolean initialized = false;

    public OBDRepository(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    /**
     * Run a tiny init sequence and a few commands; returns a simple map of values.
     * Always call this off the UI thread.
     */
    public Map<String, String> readSnapshot() {
        Map<String, String> data = new HashMap<>();
        try {
            // Init sequence only once per connection
            if (!initialized) {
                new EchoOffCommand().run(in, out);
                new LineFeedOffCommand().run(in, out);
                new TimeoutCommand(125).run(in, out);
                new SelectProtocolCommand(ObdProtocols.AUTO).run(in, out);
                initialized = true;
            }

            // Read engine RPM
            try {
                RPMCommand rpm = new RPMCommand();
                rpm.run(in, out);
                data.put("RPM", rpm.getFormattedResult());
            } catch (Exception e) {
                Log.e(TAG, "RPM read failed", e);
                data.put("RPM", "N/A");
            }

            // Read vehicle speed
            try {
                SpeedCommand speed = new SpeedCommand();
                speed.run(in, out);
                data.put("Speed", speed.getFormattedResult());
            } catch (Exception e) {
                Log.e(TAG, "Speed read failed", e);
                data.put("Speed", "N/A");
            }

            // Read engine coolant temperature
            try {
                EngineCoolantTemperatureCommand coolant = new EngineCoolantTemperatureCommand();
                coolant.run(in, out);
                data.put("CoolantTemp", coolant.getFormattedResult());
            } catch (Exception e) {
                Log.e(TAG, "Coolant temp read failed", e);
                data.put("CoolantTemp", "N/A");
            }

            // Read fuel level
            try {
                FuelLevelCommand fuel = new FuelLevelCommand();
                fuel.run(in, out);
                data.put("FuelLevel", fuel.getFormattedResult());
            } catch (Exception e) {
                Log.e(TAG, "Fuel level read failed", e);
                data.put("FuelLevel", "N/A");
            }

            // Read ambient air temperature
            try {
                AmbientAirTemperatureCommand temp = new AmbientAirTemperatureCommand();
                temp.run(in, out);
                data.put("AmbientTemp", temp.getFormattedResult());
            } catch (Exception e) {
                Log.e(TAG, "Ambient temp read failed", e);
                data.put("AmbientTemp", "N/A");
            }

        } catch (Exception e) {
            Log.e(TAG, "OBD read failed", e);
            data.put("error", e.getMessage() == null ? "read failed" : e.getMessage());
        }
        return data;
    }
}
