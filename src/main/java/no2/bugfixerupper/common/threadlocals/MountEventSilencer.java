package no2.bugfixerupper.common.threadlocals;

public class MountEventSilencer {
    private static final ThreadLocal<Boolean> SILENCE_MOUNT_EVENT = ThreadLocal.withInitial(() -> false);
    
    public static void silenceMountEvent() {
        SILENCE_MOUNT_EVENT.set(true);
    }
    
    public static void unsilenceMountEvent() {
        SILENCE_MOUNT_EVENT.set(false);
    }
    
    public static boolean isMountEventSilenced() {
        return SILENCE_MOUNT_EVENT.get();
    }
}
