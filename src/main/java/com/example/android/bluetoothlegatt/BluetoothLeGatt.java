package com.example.android.bluetoothlegatt;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * Created by STUDENT on 2016-08-07.
 */
public class BluetoothLeGatt extends JPanel implements ActionListener{

    private FirebaseDatabase db;
    private DatabaseReference ref;
    private Robot robot;

    private boolean isChecking = false;
    private static final int WAIT_TIME = 2500;

    private int prevX, prevY;

    private Timer timer;

    private static final float SCALE = 1.5f;
    private static final int BOUND = 10;

    public BluetoothLeGatt(){
        super();

        setup();
        timer = new Timer();

        ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int y = dataSnapshot.child("y").getValue(Integer.class);
                    int z = dataSnapshot.child("z").getValue(Integer.class);
                    moveMouse(y, z);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        JButton start = new JButton("start");
        start.addActionListener(this);
        JButton stop = new JButton("stop");
        stop.addActionListener(this);

        add(start);
        add(stop);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void moveMouse(int x, int y){
        Point point = MouseInfo.getPointerInfo().getLocation();
        int scaledX = Math.round(x * SCALE);
        int scaledY = Math.round(y * SCALE);
        robot.mouseMove(point.x - scaledX, point.y - scaledY);
        if(!isChecking) {
            System.out.println("scheduled task");
            timer.schedule(new ClickCheck(), WAIT_TIME);
            isChecking = true;
            prevX = point.x;
            prevY = point.y;
        }
    }

    private class ClickCheck extends TimerTask {
        @Override
        public void run(){
            Point point = MouseInfo.getPointerInfo().getLocation();
            int currX = point.x;
            int currY = point.y;
            if(Math.abs(currX - prevX) < BOUND && Math.abs(currY - prevY) < BOUND){
                System.out.println("Im inside!");
                robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
                robot.delay(50);
                robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
            } else {
                System.out.println("Im outside!");
            }
            isChecking = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("start")){

        } else if (e.getActionCommand().equals("Stop")){

        }
    }

    private void setup(){
        System.out.println("setup");
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("cursor");
        try{
            robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
            robot.setAutoWaitForIdle(true);
        } catch (AWTException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void createAndShowGUI(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());;
        } catch (Exception e){
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Eye Cursor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JComponent pane = new BluetoothLeGatt();
        pane.setOpaque(true);
        frame.setContentPane(pane);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args){
        try{
            FirebaseOptions options = new FirebaseOptions.Builder().
                    setServiceAccount(new FileInputStream("MLH-Prime-bd9070264b49.json")).
                    setDatabaseUrl("https://mlh-prime-b68ad.firebaseio.com/").
                    build();
            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
