package com.umarbhutta.xlightcompanion.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.StatusReceiver;
import com.umarbhutta.xlightcompanion.control.ControlFragment;
import com.umarbhutta.xlightcompanion.scenario.AddScenarioActivity;
import com.umarbhutta.xlightcompanion.scenario.ColorSelectActivity;
import com.umarbhutta.xlightcompanion.scenario.ScenarioFragment;

import java.util.ArrayList;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

/**
 * Created by Administrator on 2017/3/5.
 */

public class EditDeviceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_control);

        scenarioDropdown = new ArrayList<>(ScenarioFragment.name);
        scenarioDropdown.add(0, "None");

        powerSwitch = (Switch) findViewById(R.id.powerSwitch);
        brightnessSeekBar = (SeekBar) findViewById(R.id.brightnessSeekBar);
        cctSeekBar = (SeekBar) findViewById(R.id.cctSeekBar);
        cctSeekBar.setMax(6500 - 2700);
        colorTextView = (TextView) findViewById(R.id.colorTextView);
        scenarioNoneLL = (LinearLayout) findViewById(R.id.scenarioNoneLL);
        scenarioNoneLL.setAlpha(1);
        ring1Button = (ToggleButton) findViewById(R.id.ring1Button);
        ring2Button = (ToggleButton) findViewById(R.id.ring2Button);
        ring3Button = (ToggleButton) findViewById(R.id.ring3Button);
        deviceRingLabel = (TextView) findViewById(R.id.deviceRingLabel);
        brightnessLabel = (TextView) findViewById(R.id.brightnessLabel);
        cctLabel = (TextView) findViewById(R.id.cctLabel);
        powerLabel = (TextView) findViewById(R.id.powerLabel);
        colorLabel = (TextView) findViewById(R.id.colorLabel);
        lightImageView = (ImageView) findViewById(R.id.lightImageView);

        scenarioSpinner = (Spinner) findViewById(R.id.scenarioSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(this, R.layout.control_scenario_spinner_item, scenarioDropdown);
        // Specify the layout to use when the list of choices appears
        scenarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        scenarioSpinner.setAdapter(scenarioAdapter);

        // Just for demo. In real world, should get from DMI
        MainActivity.m_mainDevice.setDeviceName(DEFAULT_LAMP_TEXT);

        powerSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
        brightnessSeekBar.setProgress(MainActivity.m_mainDevice.getBrightness());
        cctSeekBar.setProgress(MainActivity.m_mainDevice.getCCT() - 2700);

        if (MainActivity.m_mainDevice.getEnableEventBroadcast()) {
            IntentFilter intentFilter = new IntentFilter(xltDevice.bciDeviceStatus);
            intentFilter.setPriority(3);
            registerReceiver(m_StatusReceiver, intentFilter);
        }

        if (MainActivity.m_mainDevice.getEnableEventSendMessage()) {
            m_handlerControl = new Handler() {
                public void handleMessage(Message msg) {
                    int intValue = msg.getData().getInt("State", -255);
                    if (intValue != -255) {
                        powerSwitch.setChecked(intValue > 0);
                    }

                    intValue = msg.getData().getInt("BR", -255);
                    if (intValue != -255) {
                        brightnessSeekBar.setProgress(intValue);
                    }

                    intValue = msg.getData().getInt("CCT", -255);
                    if (intValue != -255) {
                        cctSeekBar.setProgress(intValue - 2700);
                    }
                }
            };
            MainActivity.m_mainDevice.addDeviceEventHandler(m_handlerControl);
        }

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //check if on or off
                state = isChecked;
                //ParticleAdapter.JSONCommandPower(ParticleAdapter.DEFAULT_DEVICE_ID, state);
                //ParticleAdapter.FastCallPowerSwitch(ParticleAdapter.DEFAULT_DEVICE_ID, state);
                MainActivity.m_mainDevice.PowerSwitch(state);
            }
        });

        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabPressed();
//                new ChromaDialog.Builder()
//                        .initialColor(ContextCompat.getColor(EditDeviceActivity.this, R.color.colorAccent))
//                        .colorMode(ColorMode.RGB) // There's also ARGB and HSV
//                        .onColorSelected(new ColorSelectListener() {
//                            @Override
//                            public void onColorSelected(int color) {
//                                Log.e(TAG, "int: " + color);
//                                colorHex = String.format("%06X", (0xFFFFFF & color));
//                                Log.e(TAG, "HEX: #" + colorHex);
//
//                                int br = 65;
//                                int ww = 0;
//                                int c = (int) Long.parseLong(colorHex, 16);
//                                int r = (c >> 16) & 0xFF;
//                                int g = (c >> 8) & 0xFF;
//                                int b = (c >> 0) & 0xFF;
//                                Log.e(TAG, "RGB: " + r + "," + g + "," + b);
//
//                                colorHex = "#" + colorHex;
//                                colorTextView.setText(colorHex);
//                                colorTextView.setTextColor(Color.parseColor(colorHex));
//
//                                //send message to Particle based on which rings have been selected
//                                if ((ring1 && ring2 && ring3) || (!ring1 && !ring2 && !ring3)) {
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_ALL, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_ALL, state, br, ww, r, g, b);
//                                } else if (ring1 && ring2) {
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_1, state, br, ww, r, g, b);
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_2, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_1, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_2, state, br, ww, r, g, b);
//                                } else if (ring2 && ring3) {
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_2, state, br, ww, r, g, b);
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_3, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_2, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_3, state, br, ww, r, g, b);
//                                } else if (ring1 && ring3) {
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_1, state, br, ww, r, g, b);
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_3, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_1, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_3, state, br, ww, r, g, b);
//                                } else if (ring1) {
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_1, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_1, state, br, ww, r, g, b);
//                                } else if (ring2) {
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_2, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_2, state, br, ww, r, g, b);
//                                } else if (ring3) {
//                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_3, state, br, ww, r, g, b);
//                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_3, state, br, ww, r, g, b);
//                                } else {
//                                    //do nothing
//                                }
//                            }
//                        })
//                        .create()
//                        .show(getSupportFragmentManager(), "dialog");
            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "The brightness value is " + seekBar.getProgress());
                //ParticleAdapter.JSONCommandBrightness(ParticleAdapter.DEFAULT_DEVICE_ID, seekBar.getProgress());
                MainActivity.m_mainDevice.ChangeBrightness(seekBar.getProgress());
            }
        });

        cctSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "The CCT value is " + seekBar.getProgress() + 2700);
                //ParticleAdapter.JSONCommandCCT(ParticleAdapter.DEFAULT_DEVICE_ID, seekBar.getProgress()+2700);
                MainActivity.m_mainDevice.ChangeCCT(seekBar.getProgress() + 2700);
            }
        });

        scenarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString() == "None") {
                    //scenarioNoneLL.animate().alpha(1).setDuration(600).start();

                    //enable all views below spinner
                    disableEnableControls(true);
                } else {
                    //if anything but "None" is selected, fade scenarioNoneLL out
                    //scenarioNoneLL.animate().alpha(0).setDuration(500).start();

                    //disable all views below spinner
                    disableEnableControls(false);

                    //ParticleAdapter.JSONCommandScenario(ParticleAdapter.DEFAULT_DEVICE_ID, position);
                    //position passed into above function corresponds to the scenarioId i.e. s1, s2, s3 to trigger
                    MainActivity.m_mainDevice.ChangeScenario(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ring1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring1 = isChecked;
                updateDeviceRingLabel();
            }
        });
        ring2Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring2 = isChecked;
                updateDeviceRingLabel();
            }
        });
        ring3Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring3 = isChecked;
                updateDeviceRingLabel();
            }
        });


    }

    private static final String TAG = ControlFragment.class.getSimpleName();

    private static final String DEFAULT_LAMP_TEXT = "LIVING ROOM";
    private static final String RINGALL_TEXT = "ALL RINGS";
    private static final String RING1_TEXT = "RING 1";
    private static final String RING2_TEXT = "RING 2";
    private static final String RING3_TEXT = "RING 3";

    private Switch powerSwitch;
    private SeekBar brightnessSeekBar;
    private SeekBar cctSeekBar;
    private TextView colorTextView;
    private Spinner scenarioSpinner;
    private LinearLayout scenarioNoneLL;
    private ToggleButton ring1Button, ring2Button, ring3Button;
    private TextView deviceRingLabel, powerLabel, brightnessLabel, cctLabel, colorLabel;
    private ImageView lightImageView;

    private ArrayList<String> scenarioDropdown;

    private String colorHex;
    private boolean state = false;
    boolean ring1 = false, ring2 = false, ring3 = false;

    private Handler m_handlerControl;

    private class MyStatusReceiver extends StatusReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            powerSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
            brightnessSeekBar.setProgress(MainActivity.m_mainDevice.getBrightness());
            cctSeekBar.setProgress(MainActivity.m_mainDevice.getCCT() - 2700);
        }
    }

    private final EditDeviceActivity.MyStatusReceiver m_StatusReceiver = new EditDeviceActivity.MyStatusReceiver();

    @Override
    public void onDestroy() {
        MainActivity.m_mainDevice.removeDeviceEventHandler(m_handlerControl);
        if (MainActivity.m_mainDevice.getEnableEventBroadcast()) {
            unregisterReceiver(m_StatusReceiver);
        }
        super.onDestroy();
    }


    private void disableEnableControls(boolean isEnabled) {
        powerSwitch.setEnabled(isEnabled);
        colorTextView.setEnabled(isEnabled);
        brightnessSeekBar.setEnabled(isEnabled);
        cctSeekBar.setEnabled(isEnabled);

        int selectColor = R.color.colorAccent, allLabels = R.color.textColorPrimary;
        if (isEnabled) {
            selectColor = R.color.colorAccent;
            allLabels = R.color.textColorPrimary;
        } else {
            selectColor = R.color.colorDisabled;
            allLabels = R.color.colorDisabled;
        }
        colorTextView.setTextColor(ContextCompat.getColor(this, selectColor));
        powerLabel.setTextColor(ContextCompat.getColor(this, allLabels));
        brightnessLabel.setTextColor(ContextCompat.getColor(this, allLabels));
        cctLabel.setTextColor(ContextCompat.getColor(this, allLabels));
        colorLabel.setTextColor(ContextCompat.getColor(this, allLabels));
    }

    private void updateDeviceRingLabel() {
        String label = MainActivity.m_mainDevice.getDeviceName();

        if (ring1 && ring2 && ring3) {
            label += ": " + RINGALL_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring123);
        } else if (!ring1 && !ring2 && !ring3) {
            label += ": " + RINGALL_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_noring);
        } else if (ring1 && ring2) {
            label += ": " + RING1_TEXT + " & " + RING2_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring12);
        } else if (ring2 && ring3) {
            label += ": " + RING2_TEXT + " & " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring23);
        } else if (ring1 && ring3) {
            label += ": " + RING1_TEXT + " & " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring13);
        } else if (ring1) {
            label += ": " + RING1_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring1);
        } else if (ring2) {
            label += ": " + RING2_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring2);
        } else if (ring3) {
            label += ": " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring3);
        } else {
            label += "";
            lightImageView.setImageResource(R.drawable.aquabg_noring);
        }

        deviceRingLabel.setText(label);
    }
    private void onFabPressed() {
        Intent intent = new Intent(EditDeviceActivity.this, ColorSelectActivity.class);
        startActivityForResult(intent, 1);
    }
}
