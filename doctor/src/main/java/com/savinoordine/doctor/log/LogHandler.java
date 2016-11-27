package com.savinoordine.doctor.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class LogHandler implements Runnable {

    private PriorityType mPriorityType = PriorityType.ALL;
    private String mOldLine = "";

    public enum PriorityType {
        ALL,        // All priority
        DIWEF,      // All exept verbose
        IWEF,       // Info, warning, error and fatal
        WEF,        // Warning, error and fatal
        EF,         // Error and fatal
        F           // Fatal
    }

    private static final int LINES = 20;
    private static final int DELAY = 700;

    private ArrayList<String> bufferedlines = new ArrayList<String>();
    private String lastline = "null";
    private Thread t;

    private boolean exit = false;

    public LogHandler(PriorityType priorityType) {
        mPriorityType = priorityType;
        t = new Thread(this);
        t.start();
    }

    public void stop() {
        exit = true;
    }

    public abstract void onLineAdd(String line);

    public void run() {
        while (!exit) {
            checkLogEvents();
            try {
                t.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkLogEvents() {
        try {
            ArrayList<String> chkl = new ArrayList<>();

            String[] cmd = logcatCommandFilter();
            readLogs(chkl, cmd);

            int id = getLastSend(chkl);
            if (id == -1) {
                //resend complete
                bufferedlines = chkl;
                int size = bufferedlines.size();
                if (size > 0) {
                    this.lastline = bufferedlines.get(size - 1);
                    parseLine();
                }
            } else {
                //compute resend part
                ArrayList<String> nl = new ArrayList<>();
                for (int i = id; i < chkl.size(); i++) {
                    nl.add(chkl.get(i));
                }
                bufferedlines = nl;
                int size = bufferedlines.size();
                if (size > 0) {
                    this.lastline = bufferedlines.get(bufferedlines.size() - 1);
                    parseLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseLine() {
        for (String line : bufferedlines) {
            if (!mOldLine.equals(line)) {
                mOldLine = line;
                onLineAdd(line);
            }
        }
    }

    private void readLogs(ArrayList<String> chkl, String[] cmd) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()), LINES * 2000);

        String output = "";
        while ((output = reader.readLine()) != null) {
            chkl.add(output);
        }
        reader.close();
        //Waits for the command to finish.
        process.waitFor();
    }

    private String[] logcatCommandFilter() {
        String[] defaultCmd = {"logcat", "-t", String.valueOf(LINES)};
        String[] cmd;
        switch (mPriorityType) {

            case DIWEF:
                cmd = concat(defaultCmd, new String[]{"*:D"});
                break;

            case IWEF:
                cmd = concat(defaultCmd, new String[]{"*:I"});
                break;

            case WEF:
                cmd = concat(defaultCmd, new String[]{"*:W"});
                break;

            case EF:
                cmd = concat(defaultCmd, new String[]{"*:E"});
                break;

            case F:
                cmd = concat(defaultCmd, new String[]{"*:F"});
                break;

            case ALL:
            default:
                cmd = concat(defaultCmd, new String[]{"*:V"});
                break;
        }
        return cmd;
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private int getLastSend(ArrayList<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals(lastline)) {
                return i;
            }
        }
        return -1;
    }
}
