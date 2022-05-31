package com.padma.bscaller;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.padma.bscaller.fragments.HomeFragment;
import com.padma.bscaller.listener.ChangeFragmentListener;
import com.padma.bscaller.listener.OnBackPressedListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, ChangeFragmentListener, TextToSpeech.OnInitListener {

    public static String id1 = "NOTIFICATION_CHANNEL";
    Toolbar toolbar;
    AppCompatTextView toolbar_heading;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createchannel();
        tts = new TextToSpeech(this, this);

        setupToolbar();
        initDefaults();
    }

    private void initDefaults() {

        HomeFragment homeFragment = new HomeFragment();

        /*
         * Adding initial fragment in back stack.
         */
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container, homeFragment, homeFragment.getClass().getName())
                .addToBackStack(homeFragment.getClass().getName())
                .commitAllowingStateLoss();

        /*
         *  BackStackChangedListener for fragments.
         */
        getSupportFragmentManager().addOnBackStackChangedListener(this);

    }


    private void setupToolbar() {

        toolbar = findViewById(R.id.toolbar);
        toolbar_heading = findViewById(R.id.toolbar_heading);
        toolbar_heading.setText(getString(R.string.app_name));
        toolbar_heading.setSingleLine();
        toolbar_heading.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void addFragmentWithAnimation(Fragment fragment, Boolean stacked, Boolean addToBackStack) {

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment available = fragmentManager.findFragmentByTag(fragment.getClass().getName());
        if (stacked && available != null) {
            int stackPosition = isFragmentInBackStack(fragmentManager, fragment.getClass().getName());
            if (stackPosition != -1 && fragmentManager.getBackStackEntryCount() > stackPosition) {
                FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(stackPosition);
                fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } else {

            fragmentTransaction.add(R.id.frame_container, fragment, fragment.getClass().getName());
            if (addToBackStack)
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            else
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }

    }

    @Override
    public void textToVoice(String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }


    public int isFragmentInBackStack(FragmentManager fragmentManager, String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount() - 1; entry++) {
            if (fragmentTagName == fragmentManager.getBackStackEntryAt(entry).getName()) {
                return entry + 1;
            }
        }
        return -1;
    }

    @Override
    public void setTitle(CharSequence title) {
        toolbar_heading.setSelected(true);
        toolbar_heading.setSingleLine(true);
        toolbar_heading.setText(title);
    }

    private void createchannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel mChannel = new NotificationChannel(id1,
                    getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            mChannel.setDescription(getString(R.string.channel_description));
            mChannel.enableLights(true);
            mChannel.setShowBadge(true);
            nm.createNotificationChannel(mChannel);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

        if (fragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) fragment).onBackpressed();
        } else if (fragment instanceof HomeFragment) {
            this.finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment != null)
            fragment.onResume();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Toast.makeText(getApplicationContext(), "Language not supported",
                        Toast.LENGTH_LONG).show();
                Log.e("TTS", "Language is not supported");
            }

        } else {

            Toast.makeText(this, "TTS Initialization Failed", Toast.LENGTH_LONG)
                    .show();
            Log.e("TTS", "Initialization Failed");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isMyServiceRunning(MyForeGroundService.class)) {

            Intent service = new Intent(getApplicationContext(), MyForeGroundService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Close the Text to Speech Library
        if (tts != null) {

            tts.stop();
            tts.shutdown();

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
