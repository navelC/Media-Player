package com.nqc.nqccuoiki;

public class MusicConstants {

    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 8466503;

    public static class ACTION {
        public static final String MAIN_ACTION = "music.action.main";
        public static final String PAUSE_ACTION = "music.action.pause";
        public static final String PLAY_ACTION = "music.action.play";
        public static final String START_ACTION = "music.action.start";
        public static final String STOP_ACTION = "music.action.stop";
        public static final String PRE_ACTION = "music.action.pre";
        public static final String NEXT_ACTION = "music.action.next";
        public static final String FORWARD_ACTION = "music.action.forward";
        public static final String PREPARED_ACTION = "music.action.prepared";
        public static final String LOOP_ACTION = "music.action.loop";
        public static final String SHUFFLE_ACTION = "music.action.shuffle";
        public static final String UPDATE_TIME_ACTION = "music.action.updateTime";
        public static final String ERROR_ACTION = "music.action.error";
        public static final String CLOCK_ACTION = "music.action.clock";
    }

    public static class STATE_SERVICE {

        public static final int PREPARE = 30;
        public static final int PLAY = 20;
        public static final int PAUSE = 10;
        public static final int NOT_INIT = 0;


    }
    public static class STATE_MEDIA{
        public static final String SHUFFLE = "shuffle";
        public static final String LOOP = "loop";
        public static final String NONE = "none";
    }

}
