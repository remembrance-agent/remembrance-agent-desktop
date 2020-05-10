package io.p13i.ra.input;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents input to the keyboard
 */
public class KeyboardInputMechanism extends AbstractInputMechanism implements NativeKeyListener {
    @Override
    public void startInputMechanism() {
        // Get the logger for "org.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        try {
            GlobalScreen.setEventDispatcher(new SwingDispatchService());
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }

        inputMechanismEventsListenerCallback.onInputReady(this);
    }

    @Override
    public void closeInputMechanism() {
        try {
            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {

        String callbackValue = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
        boolean isActionKey = nativeKeyEvent.isActionKey();

        this.inputMechanismEventsListenerCallback.onInput(this, callbackValue, isActionKey);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
    }
}
